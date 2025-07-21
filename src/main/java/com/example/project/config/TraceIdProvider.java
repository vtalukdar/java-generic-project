package com.example.project.config;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import org.springframework.stereotype.Component;

@Component
public class TraceIdProvider {

    public String getCurrentTraceId() {
        Span currentSpan = Span.current();
        SpanContext context = currentSpan.getSpanContext();

        if (context.isValid()) {
            return context.getTraceId();
        } else {
            return null; // or generate a fallback ID if needed
        }
    }
}