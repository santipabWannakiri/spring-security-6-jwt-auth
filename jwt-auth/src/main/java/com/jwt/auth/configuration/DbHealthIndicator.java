package com.jwt.auth.configuration;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("h2b")
public class DbHealthIndicator implements HealthIndicator {
    private final JdbcTemplate jdbcTemplate;

    public DbHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            // Execute a simple query to check the database connectivity
            jdbcTemplate.execute("SELECT 1 FROM DUAL");

            // If the query executes successfully, mark the database as UP
            return Health.up().build();
        } catch (Exception e) {
            // If an exception occurs, mark the database as DOWN with the error message
            return Health.down(e).build();
        }
    }
}
