package io.token.rpc.client;

import com.google.auto.value.AutoValue;
import com.typesafe.config.Config;
import io.reactivex.annotations.Nullable;
import io.token.rpc.ContextConfig;
import io.token.rpc.RpcLogConfig;
import io.token.rpc.SslConfig;
import io.token.rpc.client.healthchecks.HealthCheckConfig;

import java.util.concurrent.TimeUnit;

/**
 * gRPC client config.
 */
@AutoValue
public abstract class RpcClientConfig {
    /**
     * Creates a new rpc client config.
     *
     * @param url the url
     * @param timeout the timeout
     * @param healthCheckConfig the health check configuration
     * @param useSsl whether to use ssl
     * @param sslConfig the ssl configuration
     * @param rpcLogConfig the rpc log configuration
     * @param contextConfig context config
     * @param authority authority override
     * @return a rpc client config
     */
    public static RpcClientConfig create(
            String url,
            @Nullable Long timeout,
            @Nullable HealthCheckConfig healthCheckConfig,
            boolean useSsl,
            @Nullable SslConfig sslConfig,
            RpcLogConfig rpcLogConfig,
            ContextConfig contextConfig,
            @Nullable String authority) {
        return new AutoValue_RpcClientConfig(
                url,
                timeout,
                healthCheckConfig,
                useSsl,
                sslConfig,
                rpcLogConfig,
                contextConfig,
                authority);
    }

    /**
     * Creates a new rpc client config.
     *
     * @param url url
     * @return a rpc client config
     */
    public static RpcClientConfig create(String url) {
        return create(
                url,
                null,
                null,
                false,
                null,
                RpcLogConfig.defaultConfig(),
                ContextConfig.defaultConfig(),
                null);
    }

    /**
     * Wraps {@code config} into {@link RpcClientConfig}.
     *
     * @param config config
     * @return RpcClientConfig
     */
    public static RpcClientConfig wrap(Config config) {
        return create(
                config.getString("url"),
                parseDuration(config, "timeout"),
                config.hasPath("health-check")
                        ? HealthCheckConfig.from(config.getConfig("health-check"))
                        : null,
                parseBoolean(config, "use-ssl"),
                SslConfig.from(config),
                RpcLogConfig.from(config),
                ContextConfig.from(config),
                config.hasPath("authority") ? config.getString("authority") : null);
    }

    public abstract String getUrl();

    @Nullable public abstract Long getTimeout();

    @Nullable public abstract HealthCheckConfig getHealthCheckConfig();

    public abstract boolean useSsl();

    @Nullable public abstract SslConfig getSslConfig();

    public abstract RpcLogConfig getRpcLogConfig();

    public abstract ContextConfig getContextConfig();

    @Nullable public abstract String getAuthority();

    private static Long parseDuration(Config config, String path) {
        return config.hasPath(path)
                ? config.getDuration(path, TimeUnit.MILLISECONDS)
                : null;
    }

    private static boolean parseBoolean(Config config, String path) {
        return config.hasPath(path) && config.getBoolean(path);
    }
}
