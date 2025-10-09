package com.simpleAnalytics.Analytics.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Overview dashboard response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverview {

    private MetricCard totalEvents;
    private MetricCard uniqueUsers;
    private MetricCard activeSessions;
    private MetricCard avgEventsPerUser;

    private List<TimeSeriesData> eventTimeSeries;
    private List<EventBreakdown> topEvents;
    private List<SourceBreakdown> sourceBreakdown;
}
