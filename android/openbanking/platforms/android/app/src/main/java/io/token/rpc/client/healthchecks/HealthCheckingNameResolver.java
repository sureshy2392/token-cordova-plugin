package io.token.rpc.client.healthchecks;

import static com.google.common.base.Preconditions.checkState;

import com.codahale.metrics.MetricRegistry;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;
import io.token.rpc.SslConfig;

import java.util.List;

final class HealthCheckingNameResolver extends NameResolver {
    private final NameResolver delegate;
    private final HealthCheckConfig healthCheckConfig;
    private final SslConfig sslConfig;
    private final MetricRegistry metrics;
    private volatile HealthChecker healthChecker;

    public HealthCheckingNameResolver(
            NameResolver delegate,
            HealthCheckConfig healthCheckConfig,
            SslConfig sslConfig,
            MetricRegistry metrics) {
        this.delegate = delegate;
        this.healthCheckConfig = healthCheckConfig;
        this.sslConfig = sslConfig;
        this.metrics = metrics;
    }

    @Override
    public String getServiceAuthority() {
        return delegate.getServiceAuthority();
    }

    @Override
    public void start(final Listener listener) {
        checkState(healthChecker == null);
        healthChecker = new HealthChecker(listener, healthCheckConfig, sslConfig, metrics);
        delegate.start(new Listener() {
            @Override
            public void onAddresses(List<EquivalentAddressGroup> servers, Attributes attributes) {
                healthChecker.setServerList(servers, attributes);
            }

            @Override
            public void onError(Status error) {
                listener.onError(error);
            }
        });
    }

    @Override
    public void refresh() {
        delegate.refresh();
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
        if (healthChecker != null) {
            healthChecker.close();
        }
    }
}
