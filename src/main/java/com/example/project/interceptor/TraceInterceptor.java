package com.example.project.interceptor;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TraceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Span currentSpan = Span.current();
        SpanContext context = currentSpan.getSpanContext();

        if (context.isValid()) {
            response.setHeader("X-Trace-Id", context.getTraceId());
        }

        return true;
    }
}