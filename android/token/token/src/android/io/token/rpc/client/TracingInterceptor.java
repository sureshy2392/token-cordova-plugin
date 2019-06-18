package io.token.rpc.client;

import static io.opencensus.trace.AttributeValue.stringAttributeValue;
import static io.token.rpc.util.Tracing.CONTEXT_TRACE_ID;
import static io.token.rpc.util.Tracing.METADATA_TRACE_ID;
import static io.token.rpc.util.Tracing.TRACE_ID_KEY_SHORT;
import static io.token.rpc.util.Tracing.TRACE_ORIGINATOR_KEY;
import static io.token.rpc.util.Tracing.TraceOriginator.CLIENT;
import static io.token.rpc.util.Tracing.newTraceId;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.opencensus.trace.Span;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;

/**
 * Propagates trace id to the remote server using metadata. Generates one on
 * the fly if needed.
 */
final class TracingInterceptor implements ClientInterceptor {
    private static final Tracer tracer = Tracing.getTracer();

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> methodDescriptor,
            CallOptions callOptions,
            Channel channel) {
        ClientCall<ReqT, RespT> call = channel.newCall(methodDescriptor, callOptions);
        return new SimpleForwardingClientCall<ReqT, RespT>(call) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Span span = tracer.getCurrentSpan();
                String traceId = getTraceId(CONTEXT_TRACE_ID.get(), span);
                span.putAttribute(TRACE_ID_KEY_SHORT, stringAttributeValue(traceId));
                headers.put(METADATA_TRACE_ID, traceId);
                super.start(responseListener, headers);
            }
        };
    }

    private static String getTraceId(String contextTraceId, Span span) {
        if (contextTraceId != null) {
            return contextTraceId;
        } else {
            if (span.getContext().getTraceOptions().isSampled()) {
                // TODO (RD-598): need to be more specific here
                span.putAttribute(TRACE_ORIGINATOR_KEY, CLIENT);
                return span.getContext().getTraceId().toLowerBase16();
            } else {
                return newTraceId();
            }
        }
    }
}

