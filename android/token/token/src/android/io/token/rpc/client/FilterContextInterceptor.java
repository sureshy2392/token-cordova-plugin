package io.token.rpc.client;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;
import static io.token.rpc.ContextConfig.Scope.EXTERNAL;
import static io.token.rpc.ContextKeys.ACCOUNT_HOLDER_ID_KEY;
import static io.token.rpc.ContextKeys.BANK_ID_KEY;
import static io.token.rpc.ContextKeys.REDEEMER_ID_KEY;
import static io.token.rpc.ContextKeys.SECURITY_METADATA_KEY;
import static io.token.rpc.ContextKeys.TPP_ID_KEY;
import static io.token.rpc.util.Tracing.TRACE_ID_KEY;

import com.google.common.collect.ImmutableSet;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.token.rpc.ContextConfig;

import java.util.Set;

/**
 * Interceptor which copies values from the GRPC Context into the GRPC Call's metadata, filtering
 * all metadata headers with names prefixed with "token".
 */
public class FilterContextInterceptor implements ClientInterceptor {
    private static final Set<String> WHITELIST = ImmutableSet.of(
            TRACE_ID_KEY,
            BANK_ID_KEY.getKey(),
            TPP_ID_KEY.getKey(),
            REDEEMER_ID_KEY.getKey(),
            SECURITY_METADATA_KEY.getKey(),
            ACCOUNT_HOLDER_ID_KEY.getKey());

    private final ContextConfig config;

    FilterContextInterceptor(ContextConfig config) {
        this.config = config;
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
                for (String key : headers.keys()) {
                    if (config.getScope() == EXTERNAL
                            && key.startsWith("token-")
                            && !WHITELIST.contains(key)) {
                        headers.removeAll(Metadata.Key.of(key, ASCII_STRING_MARSHALLER));
                    }
                }
                super.start(responseListener, headers);
            }
        };
    }
}
