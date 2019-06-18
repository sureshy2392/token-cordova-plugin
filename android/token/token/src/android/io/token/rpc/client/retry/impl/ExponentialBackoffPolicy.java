package io.token.rpc.client.retry.impl;

import io.grpc.CallOptions;
import io.grpc.MethodDescriptor;
import io.token.rpc.client.retry.RetryController;
import io.token.rpc.client.retry.RetryPolicy;

public class ExponentialBackoffPolicy implements RetryPolicy {
    private final long startBackoff;
    private final long maxBackoff;
    private final int maxRetries;

    /**
     * Create new exponential backoff policy.
     *
     * @param startBackoff start backoff
     * @param maxBackoff max backoff
     * @param maxRetries max retries
     */
    public ExponentialBackoffPolicy(
            long startBackoff,
            long maxBackoff,
            int maxRetries) {
        this.startBackoff = startBackoff;
        this.maxBackoff = maxBackoff;
        this.maxRetries = maxRetries;
    }

    public static ExponentialBackoffPolicy defaultInstance() {
        return new ExponentialBackoffPolicy(100, 1_000, 3);
    }

    @Override
    public <ReqT, RespT> RetryController retryController(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions) {
        return new ExponentialBackoffController(
                startBackoff,
                maxBackoff,
                maxRetries);
    }
}
