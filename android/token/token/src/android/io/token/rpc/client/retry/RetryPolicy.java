package io.token.rpc.client.retry;

import io.grpc.CallOptions;
import io.grpc.MethodDescriptor;

public interface RetryPolicy {
    /**
     * Returns a retry controller that is responsible for deciding the parameters of retry attempts.
     * @param method method
     * @param callOptions call options
     * @param <ReqT> request type
     * @param <RespT> response type
     * @return retry controlle
     */
    <ReqT, RespT> RetryController retryController(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions);
}
