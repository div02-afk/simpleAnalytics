package com.simpleAnalytics.Analytics.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Time range for analytics queries
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeRange {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static TimeRange last24Hours() {
        return TimeRange.builder()
                .startDate(LocalDateTime.now().minusHours(24))
                .endDate(LocalDateTime.now())
                .build();
    }

    public static TimeRange last7Days() {
        return TimeRange.builder()
                .startDate(LocalDateTime.now().minusDays(7))
                .endDate(LocalDateTime.now())
                .build();
    }

    public static TimeRange last30Days() {
        return TimeRange.builder()
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now())
                .build();
    }

    public static TimeRange lastNDays(int days) {
        return TimeRange.builder()
                .startDate(LocalDateTime.now().minusDays(days))
                .endDate(LocalDateTime.now())
                .build();
    }
}
