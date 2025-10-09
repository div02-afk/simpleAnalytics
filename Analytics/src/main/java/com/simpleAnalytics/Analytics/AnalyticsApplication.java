package com.simpleAnalytics.Analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;

/**
 * Analytics Service Application
 *
 * Uses Spring Boot's auto-configured DataSource and HikariCP Excludes Spring
 * Data JDBC since we're using plain JdbcTemplate
 */
@SpringBootApplication(exclude = {
    JdbcRepositoriesAutoConfiguration.class
})
public class AnalyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticsApplication.class, args);
    }

}
