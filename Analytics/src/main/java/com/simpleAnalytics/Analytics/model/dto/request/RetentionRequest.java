package com.simpleAnalytics.Analytics.model.dto.request;

import com.simpleAnalytics.Analytics.model.dto.TimeRange;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Retention analysis request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetentionRequest {

    @NotNull(message = "App ID is required")
    private UUID appId;

    @NotNull(message = "Cohort date is required")
    private LocalDate cohortDate;

    @NotNull(message = "Time range is required")
    private TimeRange timeRange;

    private List<Integer> retentionPeriods; // Days to check (e.g., [1, 7, 14, 30])
}
