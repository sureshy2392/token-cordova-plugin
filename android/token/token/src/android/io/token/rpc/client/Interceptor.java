package io.token.rpc.client;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.token.rpc.interceptor.InterceptorFactory;
import io.token.rpc.interceptor.SimpleInterceptor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Simplifies dealing with gRPC interceptors. Covers the case when we want
 * to have a callback invoked when request is received and response/error
 * for that request is generated.
 */
public final class Interceptor implements ClientInterceptor {
    private final InterceptorFactory factory;

    public Interceptor(InterceptorFactory factory) {
        this.factory = factory;
    }

    @Override
    public <ReqT, ResT> ClientCall<ReqT, ResT> interceptCall(
            MethodDescriptor<ReqT, ResT> method,
            CallOptions callOptions,
            Channel next) {
        final SimpleInterceptor<ReqT, ResT> simpleInterceptor = factory.create(method);
        final ClientCall<ReqT, ResT> call = next.newCall(method, callOptions);

        return new ForwardingClientCall<ReqT, ResT>() {
            private final AtomicReference<ReqT> request = new AtomicReference<>();
            private final AtomicReference<Metadata> headers = new AtomicReference<>();
            private final AtomicReference<Listener<ResT>> responseListener =
                    new AtomicReference<>();
            private final AtomicReference<Integer> requestCount = new AtomicReference<>();

            @Override
            protected ClientCall<ReqT, ResT> delegate() {
                return call;
            }

            @Override
            public void start(Listener<ResT> responseListener, Metadata headers) {
                // We do NOT start the call here intentionally. The problem is
                // that we want to be able to access request object when
                // generating headers. But gRPC interceptor API doesn't allow
                // us to do that. To do what we want we delay the start call
                // until we have seen request.
                this.headers.set(headers);
                this.responseListener.set(responseListener);
            }

            @Override
            public void request(int numMessages) {
                // Similar to the start method above, we need to postpone it
                // because it is only expected to be called after start has
                // finished.
                this.requestCount.set(numMessages);
            }

            @Override
            public void sendMessage(final ReqT req) {
                request.set(req);
                simpleInterceptor.onStart(req, headers.get());

                Listener<ResT> listener = new SimpleForwardingClientCallListener<ResT>(
                        responseListener.get()) {
                    private final AtomicReference<ResT> res = new AtomicReference<>();
                    private final AtomicBoolean closed = new AtomicBoolean(false);

                    @Override
                    public void onMessage(ResT message) {
                        res.set(message);
                        super.onMessage(message);
                    }

                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        Status processedStatus = status;
                        if (closed.compareAndSet(false, true)) {
                            processedStatus = simpleInterceptor.onComplete(
                                    status,
                                    req,
                                    res.get(),
                                    trailers);
                        }
                        super.onClose(processedStatus, trailers);
                    }
                };

                super.start(listener, headers.get());
                super.request(requestCount.get());
                super.sendMessage(req);
            }

            @Override
            public void halfClose() {
                simpleInterceptor.onHalfClose(request.get(), headers.get());
                super.halfClose();
            }
        };
    }

    @Override
    public String toString() {
        return factory.toString();
    }
}
