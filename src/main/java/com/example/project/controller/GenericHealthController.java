package com.example.project.controller;

import com.example.project.config.TraceIdProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class GenericHealthController {

    TraceIdProvider traceIdProvider = new TraceIdProvider();

    @SuppressWarnings("all")
    @GetMapping("/springboot")
    public ResponseEntity<String> checkSpringBoot(HttpServletResponse response) {
        String traceId = traceIdProvider.getCurrentTraceId();
        if (traceId != null) {
            response.setHeader("X-Trace-Id", traceId);
        }
        return ResponseEntity.ok("Spring Boot is healthy.");
    }
}