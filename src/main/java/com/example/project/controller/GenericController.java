package com.example.project.controller;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.StringRedisTemplate;

@RestController
@RequestMapping("/api")
public class GenericController {

    private final Tracer tracer;
    private final StringRedisTemplate redisTemplate;
    private final JdbcTemplate jdbcTemplate;

    public GenericController(Tracer tracer, StringRedisTemplate redisTemplate, JdbcTemplate jdbcTemplate) {
        this.tracer = tracer;
        this.redisTemplate = redisTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/redis/{key}")
    public ResponseEntity<String> getFromRedis(@PathVariable String key) {
        Span span = tracer.spanBuilder("redis-get").startSpan();
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(value);
        } finally {
            span.end();
        }
    }

    @GetMapping("/postgres/customer/{id}")
    public ResponseEntity<String> getCustomerFromPostgres(@PathVariable Long id) {
        Span span = tracer.spanBuilder("postgres-get-customer").startSpan();
        try {
            String sql = "SELECT name FROM customer WHERE id = ?";
            String name = jdbcTemplate.queryForObject(sql, new Object[]{id}, String.class);
            if (name == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(name);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        } finally {
            span.end();
        }
    }
}