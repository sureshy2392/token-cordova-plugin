package io.token.rpc.client;

import static io.token.rpc.client.RpcChannelFactory.forTarget;
import static java.util.Objects.requireNonNull;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Strings;
import com.typesafe.config.Config;
import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.MDC;

/**
 * Wrapper around custom app-specific context mapped by unique route target.
 */
public final class Routable<T> {
    private final Map<String, T> routeMap;

    Routable(Map<String, T> routeMap) {
        this.routeMap = requireNonNull(routeMap);
    }

    /**
     * Builds an instance of {@link Routable} stubs from a given configuration. Duplicated
     * configurations will map to the same stub.
     *
     * @param config configuration to get stub details from
     * @param stubBuilder stub builder
     * @param metrics metrics registry instance
     * @param <T> type of the stub
     * @return an instance of {@link Routable}
     */
    public static <T> Routable<T> forStubs(
            Config config,
            StubBuilder<T> stubBuilder,
            MetricRegistry metrics) {
        Map<RpcClientConfig, T> configToStub = new HashMap<>();
        Map<String, T> routeMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : config.root().unwrapped().entrySet()) {
            String key = entry.getKey();
            RpcClientConfig clientConfig = RpcClientConfig.wrap(config.getConfig(key));
            if (configToStub.containsKey(clientConfig)) {
                routeMap.put(key, configToStub.get(clientConfig));
                continue;
            }
            T stub = stubBuilder.apply(forTarget(clientConfig, metrics));
            configToStub.put(clientConfig, stub);
            routeMap.put(key, stub);
        }

        return new Routable<>(routeMap);
    }

    /**
     * Builds an instance of {@link Routable} stubs from a given configuration. Duplicatied
     * configurations will map to the same stub.
     *
     * @param configs rpc client configurations
     * @param stubBuilder stub builder
     * @param metrics metrics registry instance
     * @param <T> type of the stub
     * @return an instance of {@link Routable}
     */
    public static <T> Routable<T> forStubs(
            Map<String, RpcClientConfig> configs,
            StubBuilder<T> stubBuilder,
            MetricRegistry metrics) {
        Map<RpcClientConfig, T> configToStub = new HashMap<>();
        for (RpcClientConfig config : configs.values()) {
            if (!configToStub.containsKey(config)) {
                configToStub.put(config, stubBuilder.apply(forTarget(config, metrics)));
            }
        }

        Map<String, T> routeMap = new HashMap<>();
        for (Map.Entry<String, RpcClientConfig> entry : configs.entrySet()) {
            String key = entry.getKey();
            routeMap.put(key, configToStub.get(entry.getValue()));
        }

        return new Routable<>(routeMap);
    }

    /**
     * Builds an instance of {@link Routable} context from a given configuration.
     *
     * @param config configuration to get context details from
     * @param contextBuilder context builder
     * @param <T> type of the context
     * @return an instance of {@link Routable}
     */
    public static <T> Routable<T> forContext(
            Config config,
            ContextBuilder<T> contextBuilder) {
        Map<String, T> routeMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : config.root().unwrapped().entrySet()) {
            String key = entry.getKey();
            routeMap.put(key, contextBuilder.apply(config.getConfig(key), key));
        }

        return new Routable<>(routeMap);
    }

    /**
     * Returns all the available route targets.
     *
     * @return route targets
     */
    public Set<String> getTargets() {
        return routeMap.keySet();
    }

    /**
     * Converts a route target into the mapped routable instance.
     *
     * @param routeTarget route target specification
     * @return looked up routable instance
     * @throws StatusRuntimeException if there is no mapped content for the requested route target
     */
    public T routeFor(String routeTarget) {
        if (Strings.isNullOrEmpty(routeTarget)) {
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT
                    .withDescription("Invalid route target"));
        }

        T result = routeMap.get(routeTarget);

        if (result == null) {
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT
                    .withDescription("Unrecognized route target: " + routeTarget));
        }

        return result;
    }

    /**
     * Executes a supplied function with the looked up route target. The method
     * sets {@link MDC} which makes it easier to debug the flow.
     *
     * @param routeTarget route target specification
     * @param func function to execute for the given target
     * @param <R> result type
     * @return execution result
     * @throws StatusRuntimeException if there is no mapped content for the requested route target
     */
    public <R> R withRoute(String routeTarget, Function<T, R> func) {
        MDC.put("route", routeTarget);
        try {
            T target = routeFor(routeTarget);
            return func.apply(target);
        } finally {
            MDC.remove("route");
        }
    }

    /**
     * Stub builder Function.
     *
     * @param <T> the return type
     */
    public interface StubBuilder<T> {
        T apply(Channel channel);
    }

    /**
     * Config builder BiFunction.
     *
     * @param <T> the return type
     */
    public interface ContextBuilder<T> {
        T apply(Config config, String key);
    }

    /**
     * A function to execute for a given routable target.
     *
     * @param <T> target
     * @param <R> execution result
     */
    public interface Function<T, R> {
        R apply(T target);
    }
}
