package io.token.rpc.client.retry;

import static com.google.common.base.Preconditions.checkState;
import static io.grpc.MethodDescriptor.MethodType.UNARY;
import static io.grpc.Status.Code.CANCELLED;
import static io.grpc.Status.Code.DEADLINE_EXCEEDED;
import static io.grpc.internal.GrpcUtil.TIMER_SERVICE;
import static io.token.rpc.Endpoint.CLIENT;
import static io.token.rpc.RpcLogger.loggerFor;
import static io.token.rpc.RpcMetrics.metricsFor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.codahale.metrics.MetricRegistry;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Context;
import io.grpc.Deadline;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.internal.SharedResourceHolder;
import io.token.rpc.RpcLogConfig;
import io.token.rpc.RpcLogger;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.annotation.Nullable;

public class RetryInterceptor implements ClientInterceptor {
    private final RetryPolicy retryPolicy;
    private final MetricRegistry metrics;
    private final RpcLogConfig rpcLogConfig;

    /**
     * Create new retry interceptor.
     *
     * @param retryPolicy retry policy
     * @param metrics metrics
     * @param rpcLogConfig logging config
     */
    public RetryInterceptor(
            RetryPolicy retryPolicy,
            MetricRegistry metrics,
            RpcLogConfig rpcLogConfig) {
        this.retryPolicy = retryPolicy;
        this.metrics = metrics;
        this.rpcLogConfig = rpcLogConfig;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {
        // Only UNARY calls are retried, streaming call errors are handled by the user
        if (method.getType() != UNARY) {
            return next.newCall(method, callOptions);
        }
        return new RetryingCall<>(
                retryPolicy,
                method,
                callOptions,
                next,
                Context.current());
    }

    private class RetryingCall<ReqT, RespT> extends ClientCall<ReqT, RespT> {
        private final MethodDescriptor<ReqT, RespT> method;
        private final CallOptions callOptions;
        private final Channel channel;
        private final Context context;
        private final ScheduledExecutorService scheduledExecutor;
        private final RetryController retryController;

        private Listener<RespT> responseListener;
        private Metadata requestHeaders;
        private ReqT requestMessage;
        private boolean compressionEnabled;
        private ClientCall<ReqT, RespT> call;
        private ScheduledFuture<?> scheduledRetry;

        RetryingCall(
                RetryPolicy retryPolicy,
                MethodDescriptor<ReqT, RespT> method,
                CallOptions callOptions,
                Channel channel,
                Context context) {
            this.method = method;
            this.callOptions = callOptions;
            this.channel = channel;
            this.context = context;
            this.scheduledExecutor = SharedResourceHolder.get(TIMER_SERVICE);
            this.retryController = retryPolicy.retryController(method, callOptions);
        }

        @Override
        public void start(
                Listener<RespT> listener,
                Metadata headers) {
            checkState(responseListener == null);
            checkState(requestHeaders == null);

            responseListener = listener;
            requestHeaders = headers;
            call = channel.newCall(method, callOptions);
            call.start(new AttemptListener(), requestHeaders);
        }

        @Override
        public void request(int numMessages) {
            call.request(numMessages);
        }

        @Override
        public void halfClose() {
            call.halfClose();
        }

        @Override
        public void sendMessage(ReqT message) {
            checkState(requestMessage == null); // to ensure unary
            requestMessage = message;
            call.sendMessage(message);
        }

        @Override
        public void setMessageCompression(boolean enabled) {
            compressionEnabled = enabled;
            call.setMessageCompression(enabled);
        }

        @Override
        public void cancel(
                @Nullable String message,
                @Nullable Throwable cause) {
            call.cancel(message, cause);
            if (scheduledRetry != null) {
                scheduledRetry.cancel(true);
            }
        }

        @Override
        public boolean isReady() {
            return false; // for UNARY calls, isReady can be always false
        }

        private void maybeRetry(AttemptListener attempt) {
            final Status status = attempt.responseStatus;
            final Long backoffMs = retryController.recordRetryAttempt(status);

            Deadline contextDeadline = context.getDeadline(); // for entire request (server)
            Deadline callDeadline = callOptions.getDeadline(); // set by client for each call

            // We do not retry if success, cancelled, deadline exceeded or policy says not to
            if (status.isOk()
                    || backoffMs == null
                    || status.getCode() == CANCELLED
                    || status.getCode() == DEADLINE_EXCEEDED
                    || (contextDeadline == null && callDeadline == null) // require a deadline
                    || !withinDeadline(contextDeadline, backoffMs)
                    || !withinDeadline(callDeadline, backoffMs)) {
                responseListener.onHeaders(attempt.responseHeaders);
                if (attempt.responseMessage != null) {
                    responseListener.onMessage(attempt.responseMessage);
                }
                responseListener.onClose(attempt.responseStatus, attempt.responseTrailers);
                return;
            }

            final RpcLogger logger = loggerFor(CLIENT, method.getFullMethodName(), rpcLogConfig);
            logger.logResponse(status, attempt.responseMessage, attempt.responseHeaders);
            scheduledRetry = scheduledExecutor.schedule(context.wrap(new Runnable() {
                @Override
                public void run() {
                    logger.logRetry(requestMessage, requestHeaders, backoffMs);
                    metricsFor(metrics, CLIENT, method.getFullMethodName()).recordRetry(status);

                    call = channel.newCall(method, callOptions);
                    call.start(new AttemptListener(), requestHeaders);

                    // replay the actions that have been handled already
                    call.setMessageCompression(compressionEnabled);
                    call.sendMessage(requestMessage);
                    // Ask for 2 messages the same way Grpc does in ClientCalls.startCall
                    call.request(2);
                    call.halfClose();
                }
            }), backoffMs, MILLISECONDS);
        }

        private boolean withinDeadline(Deadline deadline, long timeMs) {
            return deadline == null || deadline.timeRemaining(MILLISECONDS) > timeMs;
        }

        private class AttemptListener extends Listener<RespT> {
            Metadata responseHeaders;
            RespT responseMessage;
            Status responseStatus;
            Metadata responseTrailers;

            @Override
            public void onHeaders(Metadata headers) {
                responseHeaders = headers;
            }

            @Override
            public void onMessage(RespT message) {
                responseMessage = message;
            }

            @Override
            public void onClose(Status status, Metadata trailers) {
                responseStatus = status;
                responseTrailers = trailers;
                maybeRetry(this);
            }
        }
    }
}
