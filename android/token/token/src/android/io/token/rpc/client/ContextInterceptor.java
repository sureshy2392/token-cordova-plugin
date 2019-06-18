package io.token.rpc.client;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.token.rpc.ContextKeys;

import java.util.Arrays;

/**
 * Interceptor which copies values from the GRPC Context into the GRPC Call's metadata.
 */
public class ContextInterceptor implements ClientInterceptor {
    private static final String ASCII_ITEM_SEPARATOR_REGEX = "" + (char) 31;
    private final ContextKeys key;

    ContextInterceptor(ContextKeys key) {
        this.key = key;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> methodDescriptor,
            CallOptions callOptions,
            Channel channel) {
        ClientCall<ReqT, RespT> call = channel.newCall(methodDescriptor, callOptions);
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(call) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                if (key.getContextKey().get() != null) {
                    String serialized = key.getContextKey().get();
                    // deserialize collapsed values for each key
                    for (String val : Arrays.asList(serialized.split(ASCII_ITEM_SEPARATOR_REGEX))) {
                        headers.put(key.getMetadataKey(), val);
                    }
                }
                super.start(responseListener, headers);
            }
        };
    }
}
