package io.token.rpc.client;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Context;
import io.grpc.Deadline;
import io.grpc.MethodDescriptor;

import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * There is currently no way to specify a gRPC timeout that would work for a
 * stub across all calls. The reasoning is that one wants to tweak the
 * deadlines per call. A suggested way to set the timeout is to use interceptor.
 * So this interceptor sets the timeout for every call if a custom deadline is
 * not set already.
 */
final class TimeoutInterceptor implements ClientInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TimeoutInterceptor.class);
    private static final long DEFAULT_TIMEOUT = 10000;
    @Nullable private final Long configTimeout;

    TimeoutInterceptor(@Nullable Long configTimeout) {
        this.configTimeout = configTimeout;
    }

    @Override
    public <ReqT, ResT> ClientCall<ReqT, ResT> interceptCall(
            MethodDescriptor<ReqT, ResT> method,
            CallOptions callOptions,
            Channel channel) {
        if (callOptions.getDeadline() != null) {
            // TODO(SUP-104): remove logs after debugging
            logger.trace(
                    "Client retaining timeout: {}",
                    callOptions.getDeadline().timeRemaining(TimeUnit.MILLISECONDS));
            return channel.newCall(method, callOptions);
        } else if (configTimeout != null) {
            CallOptions withDeadline = callOptions.withDeadline(Deadline.after(
                    configTimeout,
                    TimeUnit.MILLISECONDS));
            logger.trace(
                    "Client setting timeout from config {}",
                    withDeadline.getDeadline().timeRemaining(TimeUnit.MILLISECONDS));
            return channel.newCall(method, withDeadline);
        } else {
            long timeout = overrideWithParentTimeout(DEFAULT_TIMEOUT);
            CallOptions withDeadline = callOptions.withDeadline(Deadline.after(
                    timeout,
                    TimeUnit.MILLISECONDS));
            logger.trace(
                    "Client setting timeout from context {}",
                    withDeadline.getDeadline().timeRemaining(TimeUnit.MILLISECONDS));
            return channel.newCall(method, withDeadline);
        }
    }

    private static long overrideWithParentTimeout(long timeout) {
        final Deadline deadline = Context.current().getDeadline();
        if (deadline != null) {
            return Context.current()
                    .getDeadline()
                    .timeRemaining(TimeUnit.MILLISECONDS);
        } else {
            return timeout;
        }
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
