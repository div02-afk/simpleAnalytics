package com.simpleAnalytics.Analytics.model.dto.request;

import com.simpleAnalytics.Analytics.model.dto.FilterCriteria;
import com.simpleAnalytics.Analytics.model.dto.TimeRange;
import com.simpleAnalytics.Analytics.model.enums.MetricType;
import com.simpleAnalytics.Analytics.model.enums.TimeGranularity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Generic analytics query request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsQueryRequest {

    @NotNull(message = "App ID is required")
    private UUID appId;

    @NotNull(message = "Time range is required")
    private TimeRange timeRange;

    private TimeGranularity granularity;

    private List<FilterCriteria> filters;

    private List<String> groupBy;

    private List<MetricType> metrics;

    @Min(1)
    @Max(10000)
    private Integer limit = 100;

    @Min(0)
    private Integer offset = 0;

    private String orderBy;

    private String orderDirection = "DESC"; // ASC or DESC
}
