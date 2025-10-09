package com.simpleAnalytics.Analytics.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Simple configuration for JdbcTemplate DataSource and HikariCP are
 * auto-configured by Spring Boot from application.yml
 */
@Slf4j
@Configuration
public class ClickHouseConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setQueryTimeout(120); // 60 seconds
        log.info("JdbcTemplate configured with 60 second query timeout");
        return jdbcTemplate;
    }
}
