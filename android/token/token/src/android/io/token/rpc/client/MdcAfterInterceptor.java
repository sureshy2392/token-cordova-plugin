package io.token.rpc.client;

import static io.token.rpc.util.Tracing.withMdc;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;

import java.util.concurrent.atomic.AtomicReference;

/**
 * An interceptor that sets tracing id MDC context. This interceptor is setup to
 * be after logging/etc for a given client call, so that MDC context is set when
 * an RPC call returns and before logging is done.
 */
public final class MdcAfterInterceptor implements ClientInterceptor {
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> methodDescriptor,
            CallOptions callOptions,
            Channel channel) {
        ClientCall<ReqT, RespT> call = channel.newCall(methodDescriptor, callOptions);
        return new CallWrapper<>(call);
    }

    private static class CallWrapper<ReqT, RespT> extends SimpleForwardingClientCall<ReqT, RespT> {
        private AtomicReference<Metadata> metadata = new AtomicReference<>();

        public CallWrapper(ClientCall<ReqT, RespT> call) {
            super(call);
        }

        @Override
        public void start(final Listener<RespT> responseListener, final Metadata headers) {
            this.metadata.set(headers);

            withMdc(headers, new Runnable() {
                @Override
                public void run() {
                    CallWrapper.super.start(
                            new CallListener<>(responseListener, headers),
                            headers);
                }
            });
        }

        private static class CallListener<RespT> extends SimpleForwardingClientCallListener<RespT> {
            private final Metadata metadata;

            protected CallListener(Listener<RespT> delegate, Metadata metadata) {
                super(delegate);
                this.metadata = metadata;
            }

            @Override
            protected Listener<RespT> delegate() {
                return super.delegate();
            }

            @Override
            public void onHeaders(final Metadata headers) {
                withMdc(metadata, new Runnable() {
                    @Override public void run() {
                        CallListener.super.onHeaders(headers);
                    }
                });
            }

            @Override
            public void onMessage(final RespT message) {
                withMdc(metadata, new Runnable() {
                    @Override public void run() {
                        CallListener.super.onMessage(message);
                    }
                });
            }

            @Override
            public void onClose(final Status status, final Metadata trailers) {
                withMdc(metadata, new Runnable() {
                    @Override public void run() {
                        CallListener.super.onClose(status, trailers);
                    }
                });
            }

            @Override
            public void onReady() {
                withMdc(metadata, new Runnable() {
                    @Override public void run() {
                        CallListener.super.onReady();
                    }
                });
            }
        }
    }
}

