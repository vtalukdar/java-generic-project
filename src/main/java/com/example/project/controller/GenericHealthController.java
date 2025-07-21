package com.example.javagenericproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GenericHealthController {

    private final StringRedisTemplate redisTemplate;
    private final JdbcTemplate jdbcTemplate;

    public GenericHealthController(StringRedisTemplate redisTemplate, JdbcTemplate jdbcTemplate) {
        this.redisTemplate = redisTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health/redis")
    public ResponseEntity<String> checkRedis() {
        try {
            redisTemplate.opsForValue().set("healthcheck", "OK");
            String value = redisTemplate.opsForValue().get("healthcheck");
            if ("OK".equals(value)) {
                return ResponseEntity.ok("Redis is OK");
            } else {
                return ResponseEntity.status(500).body("Redis check failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Redis error: " + e.getMessage());
        }
    }

    @GetMapping("/health/postgres")
    public ResponseEntity<String> checkPostgres() {
        try {
            jdbcTemplate.execute("SELECT 1");
            return ResponseEntity.ok("Postgres is OK");
        } catch (DataAccessException e) {
            return ResponseEntity.status(500).body("Postgres error: " + e.getMessage());
        }
    }
}