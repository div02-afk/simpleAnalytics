package com.simpleAnalytics.Analytics.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "analytics")
public class AnalyticsProperties {

    private QueryProperties query = new QueryProperties();
    private CacheProperties cache = new CacheProperties();
    private RateLimitProperties rateLimit = new RateLimitProperties();

    @Data
    public static class QueryProperties {

        private int maxTimeRangeDays = 90;
        private int maxResultSize = 10000;
        private int defaultLimit = 100;
        private int defaultPageSize = 50;
    }

    @Data
    public static class CacheProperties {

        private boolean enabled = true;
        private int ttlSeconds = 300;
        private int dashboardTtlSeconds = 180;
        private int realtimeTtlSeconds = 30;
    }

    @Data
    public static class RateLimitProperties {

        private boolean enabled = true;
        private int requestsPerMinute = 60;
        private int requestsPerHour = 1000;
    }
}
