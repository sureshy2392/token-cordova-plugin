package io.token.rpc.client.healthchecks;

import static io.grpc.health.v1.HealthCheckResponse.ServingStatus.SERVING;

import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.ManagedChannel;
import io.grpc.NameResolver;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.token.rpc.SslConfig;
import io.token.rpc.client.RpcChannelFactory;
import io.token.rpc.util.Converters;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
final class HealthChecker implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(HealthChecker.class);

    private final NameResolver.Listener listener;
    private final long checkInterval;
    private final SslConfig sslConfig;
    private final MetricRegistry metrics;
    private final ScheduledExecutorService executor;
    private final Set<ServerInfo> healthyServers;
    private final HealthCheckMetrics healthCheckMetrics;
    @Nullable private final String authority;
    private Set<ServerInfo> allServers;
    private Attributes config;

    public HealthChecker(
            NameResolver.Listener listener,
            HealthCheckConfig healthCheckConfig,
            SslConfig sslConfig,
            MetricRegistry metrics) {
        logger.info("Scheduling health checks, every {} ms", healthCheckConfig.getInterval());
        this.listener = listener;
        this.checkInterval = healthCheckConfig.getInterval();
        this.authority = healthCheckConfig.getAuthority();
        this.sslConfig = sslConfig;
        this.metrics = metrics;
        this.executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
                .setNameFormat("rpc-client-health-check-%d")
                .setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread thread, Throwable ex) {
                        logger.error("Unhandled error", ex);
                    }
                })
                .build());
        this.executor.scheduleWithFixedDelay(
                new Runnable() {
                    public void run() {
                        runHealthCheck();
                    }
                },
                0,
                checkInterval,
                TimeUnit.MILLISECONDS);
        this.healthyServers = new HashSet<>();
        this.healthCheckMetrics = HealthCheckMetrics.create(healthCheckConfig.getMetricName());
    }

    public synchronized void setServerList(
            List<EquivalentAddressGroup> servers,
            Attributes config) {
        logger.debug("Received new server list: {}", servers);

        this.allServers = new HashSet<>();

        for (EquivalentAddressGroup group : servers) {
            List<SocketAddress> list = group.getAddresses();
            if (!list.isEmpty()) {
                allServers.add(new ServerInfo(list.get(0)));
            }
        }

        Iterator<ServerInfo> iterator = healthyServers.iterator();
        while (iterator.hasNext()) {
            ServerInfo server = iterator.next();
            if (!allServers.contains(server)) {
                iterator.remove();
            }
        }

        this.config = config;

        this.onChanged();
        if (healthyServers.isEmpty()) {
            // Check right now, the list has changed and we have no healthy servers
            // to pick from.
            this.executor.execute(new Runnable() {
                public void run() {
                    runHealthCheck();
                }
            });
        }
    }

    @Override
    public synchronized void close() {
        executor.shutdown();
        try {
            executor.awaitTermination(checkInterval * 2, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
        }
    }

    private synchronized void runHealthCheck() {
        if (allServers == null || allServers.isEmpty()) {
            healthCheckMetrics.onError();
            return;
        }

        for (final ServerInfo server : allServers) {
            String target = server.toGrpcTarget();
            logger.debug("Opening health checker channel to {}", target);
            final ManagedChannel channel = RpcChannelFactory
                    .builder(server.toGrpcTarget(), metrics)
                    .withClientSsl(sslConfig)
                    .withTimeout(checkInterval / 2)
                    .withAuthority(authority)
                    .build();
            HealthGrpc.HealthFutureStub health = HealthGrpc.newFutureStub(channel);
            Converters
                    .toObservable(health.check(HealthCheckRequest.getDefaultInstance()))
                    .subscribe(
                            new Consumer<HealthCheckResponse>() {
                                public void accept(HealthCheckResponse response) {
                                    if (response.getStatus().equals(SERVING)) {
                                        onServerHealthy(server);
                                    } else {
                                        onServerNotHealthy(server);
                                    }
                                }
                            },
                            new Consumer<Throwable>() {
                                public void accept(Throwable throwable) {
                                    onServerNotHealthy(server);
                                }
                            },
                            new Action() {
                                public void run() {
                                    channel.shutdown();
                                }
                            });
        }
    }

    private synchronized void onServerHealthy(ServerInfo server) {
        if (allServers.contains(server)) {
            if (healthyServers.add(server)) {
                logger.info("Server {} became healthy", server);
                onChanged();
            }
            healthCheckMetrics.onSuccess();
        }
    }

    private synchronized void onServerNotHealthy(ServerInfo server) {
        if (healthyServers.remove(server)) {
            logger.warn("Server {} became unhealthy", server);
            onChanged();
        }
        healthCheckMetrics.onError();
    }

    private void onChanged() {
        if (healthyServers.isEmpty()) {
            logger.debug("No healthy servers available yet");
            return;
        }
        logger.debug("New healthy server set: {}", healthyServers);
        List<SocketAddress> addresses = new LinkedList<>();
        for (ServerInfo info : healthyServers) {
            addresses.add(info.toSocketAddress());
        }
        listener.onAddresses(
                Collections.singletonList(new EquivalentAddressGroup(addresses)),
                config);
    }
}
