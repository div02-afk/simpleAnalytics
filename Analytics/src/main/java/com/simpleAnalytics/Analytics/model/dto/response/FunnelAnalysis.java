package com.simpleAnalytics.Analytics.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Funnel analysis response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunnelAnalysis {

    private List<FunnelStep> steps;
    private Long totalUsers;
    private Double overallConversionRate;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FunnelStep {

        private Integer stepNumber;
        private String eventType;
        private Long userCount;
        private Double conversionRate;
        private Double dropoffRate;
    }
}
