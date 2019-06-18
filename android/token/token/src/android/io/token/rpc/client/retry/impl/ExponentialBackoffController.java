package io.token.rpc.client.retry.impl;

import static io.grpc.Status.Code.UNAVAILABLE;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.random;
import static java.util.Collections.singletonList;

import io.grpc.Status;
import io.grpc.Status.Code;
import io.token.rpc.client.retry.RetryController;

import java.util.List;
import javax.annotation.Nullable;

public class ExponentialBackoffController implements RetryController {
    private static final List<Code> RETRY_CODES = singletonList(UNAVAILABLE);

    private final long startBackoff;
    private final long maxBackoff;
    private final int maxRetries;

    private int numAttempts;

    ExponentialBackoffController(
            long startBackoff,
            long maxBackoff,
            int maxRetries) {
        this.startBackoff = startBackoff;
        this.maxBackoff = maxBackoff;
        this.maxRetries = maxRetries;
    }

    @Override
    public @Nullable Long recordRetryAttempt(Status status) {
        if (numAttempts >= maxRetries
                || !RETRY_CODES.contains(status.getCode())) {
            return null;
        }

        long retryBackoff = numAttempts == 0
                ? 0 // retry immediately if first attempt
                : calculateBackoff(startBackoff, maxBackoff, numAttempts);

        numAttempts++;
        return retryBackoff;
    }

    /**
     * Calculate exponential backoff using full jitter and multiplier of 2.
     * Uses the formula: backoff = rand_between(0, min(cap, base * 2 ** attempt)).
     *
     * @param base initial backoff
     * @param cap capped backoff (max)
     * @param attempts retry attempt number
     * @return backoff time
     */
    private static long calculateBackoff(
            long base,
            long cap,
            int attempts) {
        return (long) (min(cap, base * pow(2, attempts)) * random());
    }
}
