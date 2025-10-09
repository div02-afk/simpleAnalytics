package com.simpleAnalytics.Analytics.model.dto.request;

import com.simpleAnalytics.Analytics.model.dto.FilterCriteria;
import com.simpleAnalytics.Analytics.model.dto.TimeRange;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Funnel analysis request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunnelRequest {

    @NotNull(message = "App ID is required")
    private UUID appId;

    @NotEmpty(message = "At least one step is required")
    private List<String> steps; // Event types for each step

    @NotNull(message = "Time range is required")
    private TimeRange timeRange;

    private Integer timeWindowMinutes = 60; // Time window between steps

    private List<FilterCriteria> filters;
}
