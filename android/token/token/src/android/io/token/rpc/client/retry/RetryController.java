package io.token.rpc.client.retry;

import io.grpc.Status;

import javax.annotation.Nullable;

public interface RetryController {
    /**
     * Returns backoff in milliseconds for the next retry. If null, then do not retry.
     *
     * @param status status of last response
     * @return backoff in ms
     */
    @Nullable Long recordRetryAttempt(Status status);
}
