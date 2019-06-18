package io.token.rpc.client.healthchecks;

import com.codahale.metrics.MetricRegistry;
import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.token.rpc.SslConfig;

import java.net.URI;
import javax.annotation.Nullable;

/**
 * Wraps a passed in name resolver and health checks all the servers that
 * it returns.
 */
public final class HealthCheckingNameResolverFactory extends NameResolver.Factory {
    private final NameResolver.Factory delegate;
    private final HealthCheckConfig healthCheckConfig;
    private final SslConfig sslConfig;
    private final MetricRegistry metrics;

    /**
     * Creates a new name resolver that health checks the backend servers.
     *
     * @param delegate the name resolver to wrap
     * @param healthCheckConfig health check configuration
     * @param sslConfig TLS configuration details
     * @param metrics metric registry
     */
    public HealthCheckingNameResolverFactory(
            NameResolver.Factory delegate,
            HealthCheckConfig healthCheckConfig,
            SslConfig sslConfig,
            MetricRegistry metrics) {
        this.delegate = delegate;
        this.healthCheckConfig = healthCheckConfig;
        this.sslConfig = sslConfig;
        this.metrics = metrics;
    }

    @Nullable
    @Override
    public NameResolver newNameResolver(URI targetUri, Attributes params) {
        NameResolver resolver = delegate.newNameResolver(targetUri, params);
        return new HealthCheckingNameResolver(resolver, healthCheckConfig, sslConfig, metrics);
    }

    @Override
    public String getDefaultScheme() {
        return delegate.getDefaultScheme();
    }
}
