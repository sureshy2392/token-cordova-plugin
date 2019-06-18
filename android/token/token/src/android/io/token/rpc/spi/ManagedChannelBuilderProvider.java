package io.token.rpc.spi;

import javax.net.ssl.SSLException;

import io.grpc.ManagedChannelBuilder;
import io.token.rpc.SslConfig;

/**
 * An SPI provider for a {@link ManagedChannelBuilder}.
 */
public interface ManagedChannelBuilderProvider {
    /**
     * Provides a new {@link ManagedChannelBuilder} for the given target.
     *
     * @param target the channel target
     * @return a builder
     */
    ManagedChannelBuilder provide(String target);

    /**
     * Provides a new {@link ManagedChannelBuilder} for the given target an ssl configuration.
     *
     * @param target the channel target
     * @return a ManagedChannelBuilder
     * @throws SSLException if there is an error with the ssl configuration
     */
    ManagedChannelBuilder provideTls(String target) throws SSLException;

    /**
     * Provides a new {@link ManagedChannelBuilder} for the given target an ssl configuration.
     * This can be used to configure mutual TLS or use a specific trusted TLS certificate.
     *
     * @param target the channel target
     * @param sslConfig the mutual ssl configuration
     * @return a ManagedChannelBuilder
     * @throws SSLException if there is an error with the ssl configuration
     */
    ManagedChannelBuilder provideConfiguredTls(
            String target,
            SslConfig sslConfig) throws SSLException;
}