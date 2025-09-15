package com.simpleAnalytics.EventConsumer.service;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class EventProcessingMetrics {

    private final AtomicLong totalEventsProcessed = new AtomicLong(0);
    private final AtomicLong totalBatchesProcessed = new AtomicLong(0);
    private final AtomicLong totalFailedBatches = new AtomicLong(0);
    private final AtomicInteger currentBufferSize = new AtomicInteger(0);
    private final AtomicLong lastProcessingTime = new AtomicLong(0);

    public void recordEventsProcessed(int count) {
        totalEventsProcessed.addAndGet(count);
    }

    public void recordBatchProcessed() {
        totalBatchesProcessed.incrementAndGet();
    }

    public void recordBatchFailed() {
        totalFailedBatches.incrementAndGet();
    }

    public void updateBufferSize(int size) {
        currentBufferSize.set(size);
    }

    public void recordProcessingTime(long durationMs) {
        lastProcessingTime.set(durationMs);
    }

    public void logMetrics() {
        log.info("Event Processing Metrics - Total Events: {}, Total Batches: {}, Failed Batches: {}, "
                + "Current Buffer Size: {}, Last Processing Time: {}ms",
                totalEventsProcessed.get(),
                totalBatchesProcessed.get(),
                totalFailedBatches.get(),
                currentBufferSize.get(),
                lastProcessingTime.get());
    }

    // Getters for monitoring endpoints
    public long getTotalEventsProcessed() {
        return totalEventsProcessed.get();
    }

    public long getTotalBatchesProcessed() {
        return totalBatchesProcessed.get();
    }

    public long getTotalFailedBatches() {
        return totalFailedBatches.get();
    }

    public int getCurrentBufferSize() {
        return currentBufferSize.get();
    }

    public long getLastProcessingTime() {
        return lastProcessingTime.get();
    }

    public double getFailureRate() {
        long total = totalBatchesProcessed.get();
        return total > 0 ? (double) totalFailedBatches.get() / total : 0.0;
    }
}
