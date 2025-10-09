package com.simpleAnalytics.Analytics.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metric card for dashboard display
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricCard {

    private String label;
    private Object value;
    private String formattedValue;
    private Double changePercentage;
    private String trend; // "up", "down", "stable"
    private String icon;
    private String color;
}
