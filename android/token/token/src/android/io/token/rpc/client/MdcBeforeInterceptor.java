package io.token.rpc.client;

import static io.token.rpc.util.Tracing.withMdc;

import io.grpc.Attributes;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

/**
 * An interceptor that sets tracing id MDC context. This interceptor is setup to
 * be before logging/etc for a given client call, so that MDC context is set
 * before call is initiated.
 */
public final class MdcBeforeInterceptor implements ClientInterceptor {
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
        public void request(final int numMessages) {
            withMdc(metadata.get(), new Runnable() {
                @Override
                public void run() {
                    CallWrapper.super.request(numMessages);
                }
            });
        }

        @Override
        public void cancel(final @Nullable String message, final @Nullable Throwable cause) {
            withMdc(metadata.get(), new Runnable() {
                @Override
                public void run() {
                    CallWrapper.super.cancel(message, cause);
                }
            });
        }

        @Override
        public void halfClose() {
            withMdc(metadata.get(), new Runnable() {
                @Override
                public void run() {
                    CallWrapper.super.halfClose();
                }
            });
        }

        @Override
        public void sendMessage(final ReqT message) {
            withMdc(metadata.get(), new Runnable() {
                @Override
                public void run() {
                    CallWrapper.super.sendMessage(message);
                }
            });
        }

        @Override
        public void setMessageCompression(final boolean enabled) {
            withMdc(metadata.get(), new Runnable() {
                @Override
                public void run() {
                    CallWrapper.super.setMessageCompression(enabled);
                }
            });
        }

        @Override
        public boolean isReady() {
            return withMdc(metadata.get(), new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return CallWrapper.super.isReady();
                }
            });
        }

        @Override
        public Attributes getAttributes() {
            return withMdc(metadata.get(), new Callable<Attributes>() {
                @Override
                public Attributes call() {
                    return CallWrapper.super.getAttributes();
                }
            });
        }

        @Override
        public void start(final Listener<RespT> responseListener, final Metadata headers) {
            this.metadata.set(headers);

            withMdc(headers, new Runnable() {
                @Override
                public void run() {
                    CallWrapper.super.start(responseListener, headers);
                }
            });
        }
    }
}

