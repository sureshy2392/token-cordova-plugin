package io.token.rpc.client.healthchecks;

import com.google.auto.value.AutoValue;
import com.typesafe.config.Config;

import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

@AutoValue
public abstract class HealthCheckConfig {
    /**
     * Creates an instance from config.
     *
     * @param config rpc-client config
     * @return health check config instance
     */
    public static HealthCheckConfig from(Config config) {
        return create(
                config.getDuration("interval", TimeUnit.MILLISECONDS),
                config.getString("metric-name"),
                config.hasPath("authority") ? config.getString("authority") : null);
    }

    public static HealthCheckConfig create(
            long interval,
            String metricName,
            @Nullable String authority) {
        return new AutoValue_HealthCheckConfig(interval, metricName, authority);
    }

    public abstract long getInterval();

    public abstract String getMetricName();

    @Nullable public abstract String getAuthority();
}
