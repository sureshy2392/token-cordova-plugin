package io.token.rpc.client;

import static io.token.rpc.Endpoint.CLIENT;
import static java.lang.String.format;
import static java.util.concurrent.Executors.newCachedThreadPool;

import io.token.rpc.client.*;
import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.typesafe.config.Config;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.token.rpc.ContextConfig;
import io.token.rpc.ContextKeys;
import io.token.rpc.RpcLogConfig;
import io.token.rpc.SslConfig;
import io.token.rpc.client.healthchecks.HealthCheckingNameResolverFactory;
import io.token.rpc.client.retry.RetryInterceptor;
import io.token.rpc.client.retry.RetryPolicy;
import io.token.rpc.client.retry.impl.ExponentialBackoffPolicy;
import io.token.rpc.interceptor.InterceptorFactory;
import io.token.rpc.interceptor.LoggingInterceptor;
import io.token.rpc.interceptor.MetadataInterceptor;
import io.token.rpc.interceptor.MetricsInterceptor;
import io.token.rpc.spi.ManagedChannelBuilderProvider;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;
import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RpcChannelFactory {
    private static final Logger logger = LoggerFactory.getLogger(RpcChannelFactory.class);
    private static final ExecutorService EXECUTOR = newCachedThreadPool(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("rpc-client-%d")
            .setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                public void uncaughtException(Thread thread, Throwable ex) {
                    logger.error("Unhandled error", ex);
                }
            })
            .build());
    private static final Channels allChannels = new Channels();

    /**
     * Creates a channel builder.
     *
     * @param config rpc config
     * @param metrics metrics
     * @return Builder instance
     */
    public static Builder builder(RpcClientConfig config, MetricRegistry metrics) {
        Builder builder = new Builder(
                config.getUrl(),
                metrics,
                config.getSslConfig(),
                config.getRpcLogConfig(),
                config.getContextConfig(),
                config.getTimeout(),
                config.getAuthority());
//        if (config.getHealthCheckConfig() != null) {
//            builder.withHealthChecks(config.getHealthCheckConfig());
//        }

        return builder.withSsl(config.useSsl());
    }

    /**
     * Adds channel URL.
     *
     * @param url url
     * @return Builder instance
     */
    public static Builder builder(String url) {
        return builder(RpcClientConfig.create(url), new MetricRegistry());
    }

    /**
     * Adds channel URL.
     *
     * @param url url
     * @param metrics metric registry
     * @return Builder instance
     */
    public static Builder builder(String url, MetricRegistry metrics) {
        return builder(RpcClientConfig.create(url), metrics);
    }

    /**
     * Adds channel port.
     *
     * @param port port
     * @return Builder instance
     */
    public static Builder builder(int port) {
        return builder(
                RpcClientConfig.create(format("dns:///localhost:%d/", port)),
                new MetricRegistry());
    }

    /**
     * Adds host and port with ssl flag.
     *
     * @param host host
     * @param port port
     * @param useSsl ssl flag
     * @return Builder instance
     */
    public static Builder builder(String host, int port, boolean useSsl) {
        return builder(
                RpcClientConfig.create(format("dns:///%s:%d/", host, port)),
                new MetricRegistry())
                .withSsl(useSsl);
    }

    /**
     * Adds host and port.
     *
     * @param host host
     * @param port port
     * @return Builder instance
     */
    public static Builder builder(String host, int port) {
        return builder(host, port, false);
    }

    /**
     * Builds a {@link ManagedChannel} instance.
     *
     * @param target target to build fo
     * @return ManagedChannel instance
     */
    public static ManagedChannel forTarget(String target) {
        return builder(target).build();
    }

    /**
     * Builds a {@link ManagedChannel} instance.
     *
     * @param config config
     * @param metrics metrics
     * @return ManagedChannel instance
     */
    public static ManagedChannel forTarget(Config config, MetricRegistry metrics) {
        return builder(RpcClientConfig.wrap(config), metrics).build();
    }

    /**
     * Builds a {@link ManagedChannel} instance.
     *
     * @param config config
     * @param metrics metrics
     * @return ManagedChannel instance
     */
    public static ManagedChannel forTarget(RpcClientConfig config, MetricRegistry metrics) {
        return builder(config, metrics).build();
    }

    /**
     * Builds a {@link ManagedChannel} instance.
     *
     * @param port port
     * @return ManagedChannel instance
     */
    public static ManagedChannel forPort(int port) {
        return builder(port).build();
    }

    /**
     * Decorates a {@code channel} with interceptors.
     *
     * @param channel channel to decorate
     * @param interceptorFactories interceptor factories
     * @return decorated Channel instance
     */
    public static Channel intercept(Channel channel, InterceptorFactory... interceptorFactories) {
        List<Interceptor> interceptors = new LinkedList<>();
        for (InterceptorFactory factory : interceptorFactories) {
            interceptors.add(new Interceptor(factory));
        }
        return ClientInterceptors.intercept(channel, interceptors);
    }

    /**
     * Returns all the channels instantiated by this factory.
     *
     * @return map of channels and their targets
     */
    public static Channels getAllChannels() {
        return allChannels;
    }

    public static final class Builder {
        private final MetricRegistry metrics;
        private final List<ClientInterceptor> interceptors;
        private SslConfig sslConfig;
        private String target;
        private boolean useSsl;
        private Metadata metadata;
        private NameResolver.Factory nameResolverFactory;
        private RpcLogConfig rpcLogConfig;
        private ContextConfig contextConfig;
        private RetryPolicy retryPolicy;
        @Nullable private Long timeout;
        @Nullable private String authority;

        Builder(
                String target,
                MetricRegistry metrics,
                @Nullable SslConfig sslConfig,
                RpcLogConfig rpcLogConfig,
                ContextConfig contextConfig,
                @Nullable Long timeout,
                @Nullable String authority) {
            this.target = target;
            this.metrics = metrics;
            this.sslConfig = sslConfig;
            this.interceptors = new ArrayList<>();
            this.metadata = new Metadata();
            this.nameResolverFactory = NameResolverProvider.asFactory();
            this.rpcLogConfig = rpcLogConfig;
            this.contextConfig = contextConfig;
            this.retryPolicy = ExponentialBackoffPolicy.defaultInstance();
            this.timeout = timeout;
            this.authority = authority;
        }

        /**
         * Install an interceptor invoked for every request.
         *
         * @param interceptor interceptor to install
         * @return this object
         */
        public Builder intercept(ClientInterceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        /**
         * Install an interceptor invoked for every request.
         *
         * @param factory creates per request interceptor instance
         * @return this object
         */
        public Builder intercept(InterceptorFactory factory) {
            interceptors.add(new Interceptor(factory));
            return this;
        }

        /**
         * Sets the specified timeout for every request.
         *
         * @param timeout per request timeout
         * @return this object
         */
        public Builder withTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Configures server-only TLS channel.
         *
         * @param enabled setup for server-only TLS if true
         * @return this object
         */
        public Builder withSsl(boolean enabled) {
            this.useSsl = enabled;
            return this;
        }

        /**
         * Configures client (mutual) TLS.
         *
         * @param sslConfig SslConfig instance
         * @return this object
         */
        public Builder withClientSsl(@Nullable SslConfig sslConfig) {
            this.sslConfig = sslConfig;
            return this;
        }

        /**
         * Instructs the client to health check the servers.
         *
         * @param healthCheckConfig health check configuration
         * @return this builder
         */
//        public Builder withHealthChecks(HealthCheckConfig healthCheckConfig) {
//            this.nameResolverFactory = new HealthCheckingNameResolverFactory(
//                    nameResolverFactory,
//                    healthCheckConfig,
//                    sslConfig,
//                    metrics);
//            return this;
//        }

        /**
         * Set metadata headers for each request.
         *
         * @param metadata headers
         * @return this builder
         */
        public Builder withMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Set retry policy for each request.
         *
         * @param retryPolicy retry policy
         * @return this builder
         */
        public Builder withRetryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        /**
         * Overrides the authority used with TLS and HTTP virtual hosting. It does not change what
         * host is actually connected to
         *
         * @param authority authority
         * @return this builder
         */
        public Builder withAuthority(@Nullable String authority) {
            this.authority = authority;
            return this;
        }

        /**
         * Builds a {@link ManagedChannel} instance.
         *
         * @return ManagedChannel
         */
        public ManagedChannel build() {
            List<ClientInterceptor> allInterceptors = new ArrayList<>();
            allInterceptors.add(new RetryInterceptor(retryPolicy, metrics, rpcLogConfig));
            allInterceptors.addAll(interceptors);
            allInterceptors.add(new FilterContextInterceptor(contextConfig));

            for (ContextKeys key : ContextKeys.values()) {
                allInterceptors.add(new ContextInterceptor(key));
            }

            allInterceptors.add(new MdcAfterInterceptor());
            allInterceptors.add(new TimeoutInterceptor(timeout));
            allInterceptors.add(
                    new Interceptor(LoggingInterceptor.newFactory(CLIENT, rpcLogConfig)));
            allInterceptors.add(new MdcBeforeInterceptor());
            allInterceptors.add(new Interceptor(MetricsInterceptor.newFactory(CLIENT, metrics)));
            allInterceptors.add(new TracingInterceptor());

            if (metadata.keys().size() > 0) {
                allInterceptors.add(new Interceptor(MetadataInterceptor.newFactory(metadata)));
            }

//            ManagedChannelBuilderProvider builderProvider = lookupProviderService();

            try {
//                ManagedChannelBuilder builder = getManagedChannelBuilder(builderProvider);
//                if (authority != null) {
//                    builder = builder.overrideAuthority(authority);
//                }


                ManagedChannel channel = OkHttpChannelBuilder.forAddress("api-grpc.sandbox.token.io",443)
                        .useTransportSecurity().build();
//                ManagedChannel channel = builder
//                        .intercept(allInterceptors)
//                        .nameResolverFactory(nameResolverFactory)
//                        .defaultLoadBalancingPolicy("round_robin")
//                        .idleTimeout(365, TimeUnit.DAYS)
//                        .executor(EXECUTOR)
//                        .build();
                allChannels.add(target, channel);
                return channel;
            } catch (Exception ex) {
                logger.error("Failed to initialize SSL.", ex);
                throw new StatusRuntimeException(
                        Status.INTERNAL.withDescription("Unable to initialize SSL."));
            }
        }

        private ManagedChannelBuilder getManagedChannelBuilder(
                ManagedChannelBuilderProvider builderProvider) throws SSLException {
            if (sslConfig != null && sslConfig.getTrustedCertFile() != null) {
                return builderProvider.provideConfiguredTls(target, sslConfig);
            }
            return useSsl
                    ? builderProvider.provideTls(target)
                    : builderProvider.provide(target);
        }
    }
}
