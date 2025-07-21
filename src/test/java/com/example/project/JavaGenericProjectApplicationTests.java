package com.example.project;

import com.example.project.controller.GenericController;
import io.opentelemetry.api.trace.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(GenericController.class)
public class JavaGenericProjectApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Tracer tracer;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        // Setup default behavior for tracer mock if needed
    }

    @Test
    public void contextLoads() throws Exception {
        // Just check that the context loads the controller
        mockMvc.perform(get("/api/redis/test"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFromRedisFound() throws Exception {
        when(redisTemplate.opsForValue().get("testkey")).thenReturn("testvalue");

        mockMvc.perform(get("/api/redis/testkey"))
                .andExpect(status().isOk())
                .andExpect(content().string("testvalue"));
    }

    @Test
    public void testGetFromRedisNotFound() throws Exception {
        when(redisTemplate.opsForValue().get("missingkey")).thenReturn(null);

        mockMvc.perform(get("/api/redis/missingkey"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetFromPostgresFound() throws Exception {
        when(jdbcTemplate.queryForObject(Mockito.anyString(), Mockito.any(Object[].class), eq(String.class)))
                .thenReturn("John Doe");

        mockMvc.perform(get("/api/postgres/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("John Doe"));
    }

    @Test
    public void testGetFromPostgresNotFound() throws Exception {
        when(jdbcTemplate.queryForObject(Mockito.anyString(), Mockito.any(Object[].class), eq(String.class)))
                .thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/postgres/customer/999"))
                .andExpect(status().isNotFound());
    }
}