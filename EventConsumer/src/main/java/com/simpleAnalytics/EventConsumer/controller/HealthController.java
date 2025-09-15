package com.simpleAnalytics.EventConsumer.controller;

import com.simpleAnalytics.EventConsumer.service.EventProcessingMetrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final EventProcessingMetrics metrics;

    @GetMapping("/events")
    public Map<String, Object> getEventProcessingHealth() {
        Map<String, Object> health = new HashMap<>();

        health.put("totalEventsProcessed", metrics.getTotalEventsProcessed());
        health.put("totalBatchesProcessed", metrics.getTotalBatchesProcessed());
        health.put("totalFailedBatches", metrics.getTotalFailedBatches());
        health.put("currentBufferSize", metrics.getCurrentBufferSize());
        health.put("lastProcessingTimeMs", metrics.getLastProcessingTime());
        health.put("failureRate", metrics.getFailureRate());

        // Simple health check based on failure rate
        boolean isHealthy = metrics.getFailureRate() < 0.1; // Less than 10% failure rate
        health.put("status", isHealthy ? "HEALTHY" : "UNHEALTHY");

        return health;
    }
}
