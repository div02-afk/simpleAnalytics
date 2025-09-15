package com.simpleAnalytics.EventConsumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.batch")
public class BatchProcessingConfig {

    /**
     * Maximum number of events to buffer before forcing a flush
     */
    private int maxBufferSize = 1000;

    /**
     * Maximum number of events to process in a single batch
     */
    private int maxBatchSize = 500;

    /**
     * Scheduled flush interval in milliseconds
     */
    private long flushIntervalMs = 5000;

    /**
     * Maximum retry attempts for failed batches
     */
    private int maxRetryAttempts = 3;

    /**
     * Initial retry delay in milliseconds
     */
    private long initialRetryDelay = 1000;

    /**
     * Retry delay multiplier for exponential backoff
     */
    private double retryMultiplier = 2.0;

    /**
     * Maximum retry delay in milliseconds
     */
    private long maxRetryDelay = 10000;
}
