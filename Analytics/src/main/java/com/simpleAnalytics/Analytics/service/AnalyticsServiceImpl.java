package com.simpleAnalytics.Analytics.service;

import com.simpleAnalytics.Analytics.config.AnalyticsProperties;
import com.simpleAnalytics.Analytics.exception.InvalidQueryException;
import com.simpleAnalytics.Analytics.model.dto.FilterCriteria;
import com.simpleAnalytics.Analytics.model.dto.TimeRange;
import com.simpleAnalytics.Analytics.model.dto.request.AnalyticsQueryRequest;
import com.simpleAnalytics.Analytics.model.dto.request.FunnelRequest;
import com.simpleAnalytics.Analytics.model.dto.response.*;
import com.simpleAnalytics.Analytics.model.entity.Event;
import com.simpleAnalytics.Analytics.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of AnalyticsService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final EventRepository eventRepository;
    private final AnalyticsProperties properties;

    @Override
    @Cacheable(value = "dashboard", key = "#appId + '_' + #timeRange.startDate + '_' + #timeRange.endDate")
    public DashboardOverview getDashboardOverview(UUID appId, TimeRange timeRange) {
        log.info("Getting dashboard overview for appId: {}", appId);

        validateTimeRange(timeRange);

        // Fetch all metrics in parallel (can be optimized with CompletableFuture)
        long totalEvents = getTotalEventCount(appId, timeRange, null);
        long uniqueUsers = getUniqueUserCount(appId, timeRange, null);
        long activeSessions = getActiveSessionCount(appId, timeRange, null);

        // Calculate previous period for comparison
        TimeRange previousPeriod = getPreviousPeriod(timeRange);
        long prevTotalEvents = getTotalEventCount(appId, previousPeriod, null);
        long prevUniqueUsers = getUniqueUserCount(appId, previousPeriod, null);
        long prevActiveSessions = getActiveSessionCount(appId, previousPeriod, null);

        // Build metric cards
        MetricCard totalEventsCard = buildMetricCard(
                "Total Events", totalEvents, prevTotalEvents
        );
        MetricCard uniqueUsersCard = buildMetricCard(
                "Unique Users", uniqueUsers, prevUniqueUsers
        );
        MetricCard activeSessionsCard = buildMetricCard(
                "Active Sessions", activeSessions, prevActiveSessions
        );

        double avgEventsPerUser = uniqueUsers > 0 ? (double) totalEvents / uniqueUsers : 0;
        MetricCard avgEventsPerUserCard = MetricCard.builder()
                .label("Avg Events/User")
                .value(avgEventsPerUser)
                .formattedValue(String.format("%.2f", avgEventsPerUser))
                .build();

        // Get event time series
        AnalyticsQueryRequest timeSeriesRequest = AnalyticsQueryRequest.builder()
                .appId(appId)
                .timeRange(timeRange)
                .build();
        List<TimeSeriesData> timeSeries = getTimeSeriesData(timeSeriesRequest);

        // Get top events and source breakdown
        List<EventBreakdown> topEvents = getTopEvents(appId, timeRange, 10, null);
        List<SourceBreakdown> sourceBreakdown = getSourceBreakdown(appId, timeRange, null);

        return DashboardOverview.builder()
                .totalEvents(totalEventsCard)
                .uniqueUsers(uniqueUsersCard)
                .activeSessions(activeSessionsCard)
                .avgEventsPerUser(avgEventsPerUserCard)
                .eventTimeSeries(timeSeries)
                .topEvents(topEvents)
                .sourceBreakdown(sourceBreakdown)
                .build();
    }

    @Override
    @Cacheable(value = "analytics", key = "'eventCount_' + #appId + '_' + #timeRange.startDate")
    public long getTotalEventCount(UUID appId, TimeRange timeRange, List<FilterCriteria> filters) {
        log.debug("Getting total event count for appId: {}", appId);
        validateTimeRange(timeRange);
        return eventRepository.getTotalEventCount(appId, timeRange, filters);
    }

    @Override
    @Cacheable(value = "analytics", key = "'userCount_' + #appId + '_' + #timeRange.startDate")
    public long getUniqueUserCount(UUID appId, TimeRange timeRange, List<FilterCriteria> filters) {
        log.debug("Getting unique user count for appId: {}", appId);
        validateTimeRange(timeRange);
        return eventRepository.getUniqueUserCount(appId, timeRange, filters);
    }

    @Override
    @Cacheable(value = "analytics", key = "'sessionCount_' + #appId + '_' + #timeRange.startDate")
    public long getActiveSessionCount(UUID appId, TimeRange timeRange, List<FilterCriteria> filters) {
        log.debug("Getting active session count for appId: {}", appId);
        validateTimeRange(timeRange);
        return eventRepository.getUniqueSessionCount(appId, timeRange, filters);
    }

    @Override
    @Cacheable(value = "analytics", key = "'eventBreakdown_' + #appId + '_' + #timeRange.startDate")
    public List<EventBreakdown> getEventBreakdown(UUID appId, TimeRange timeRange, List<FilterCriteria> filters) {
        log.debug("Getting event breakdown for appId: {}", appId);
        validateTimeRange(timeRange);

        List<Map<String, Object>> results = eventRepository.getEventBreakdown(appId, timeRange, filters);
        long totalCount = results.stream()
                .mapToLong(r -> ((Number) r.get("count")).longValue())
                .sum();

        return results.stream()
                .map(r -> EventBreakdown.builder()
                .eventType((String) r.get("eventType"))
                .count(((Number) r.get("count")).longValue())
                .uniqueUsers(((Number) r.get("uniqueUsers")).longValue())
                .percentage(totalCount > 0
                        ? (((Number) r.get("count")).doubleValue() / totalCount) * 100 : 0)
                .build())
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "analytics", key = "'sourceBreakdown_' + #appId + '_' + #timeRange.startDate")
    public List<SourceBreakdown> getSourceBreakdown(UUID appId, TimeRange timeRange, List<FilterCriteria> filters) {
        log.debug("Getting source breakdown for appId: {}", appId);
        validateTimeRange(timeRange);

        List<Map<String, Object>> results = eventRepository.getSourceBreakdown(appId, timeRange, filters);
        long totalCount = results.stream()
                .mapToLong(r -> ((Number) r.get("count")).longValue())
                .sum();

        return results.stream()
                .map(r -> SourceBreakdown.builder()
                .source((String) r.get("source"))
                .count(((Number) r.get("count")).longValue())
                .percentage(totalCount > 0
                        ? (((Number) r.get("count")).doubleValue() / totalCount) * 100 : 0)
                .build())
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "analytics", key = "'timeSeries_' + #request.appId + '_' + #request.granularity")
    public List<TimeSeriesData> getTimeSeriesData(AnalyticsQueryRequest request) {
        log.debug("Getting time series data for appId: {}", request.getAppId());
        validateTimeRange(request.getTimeRange());

        String granularity = determineGranularity(request);
        List<Map<String, Object>> results = eventRepository.getTimeSeriesData(
                request.getAppId(),
                request.getTimeRange(),
                granularity,
                request.getFilters()
        );

        return results.stream()
                .map(r -> TimeSeriesData.builder()
                .timestamp((LocalDateTime) r.get("timestamp"))
                .value(((Number) r.get("count")).longValue())
                .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<EventBreakdown> getTopEvents(UUID appId, TimeRange timeRange, int limit, List<FilterCriteria> filters) {
        log.debug("Getting top {} events for appId: {}", limit, appId);
        validateTimeRange(timeRange);

        List<Map<String, Object>> results = eventRepository.getTopEvents(appId, timeRange, limit, filters);

        return results.stream()
                .map(r -> EventBreakdown.builder()
                .eventType((String) r.get("eventType"))
                .count(((Number) r.get("count")).longValue())
                .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> getEvents(UUID appId, TimeRange timeRange, List<FilterCriteria> filters, int limit, int offset) {
        log.debug("Getting events for appId: {} with limit: {} offset: {}", appId, limit, offset);
        validateTimeRange(timeRange);
        validateLimit(limit);

        return eventRepository.getEvents(appId, timeRange, filters, limit, offset);
    }

    @Override
    public FunnelAnalysis getFunnelAnalysis(FunnelRequest request) {
        log.debug("Getting funnel analysis for appId: {}", request.getAppId());
        validateTimeRange(request.getTimeRange());

        List<Map<String, Object>> results = eventRepository.getFunnelData(
                request.getAppId(),
                request.getSteps(),
                request.getTimeRange(),
                request.getTimeWindowMinutes()
        );

        List<FunnelAnalysis.FunnelStep> steps = new ArrayList<>();
        // Build funnel steps from results
        // This is simplified - enhance based on actual funnel logic

        return FunnelAnalysis.builder()
                .steps(steps)
                .totalUsers(0L)
                .overallConversionRate(0.0)
                .build();
    }

    @Override
    public List<Map<String, Object>> getDeviceBreakdown(UUID appId, TimeRange timeRange, List<FilterCriteria> filters) {
        validateTimeRange(timeRange);
        return eventRepository.getDeviceBreakdown(appId, timeRange, filters);
    }

    @Override
    public List<Map<String, Object>> getBrowserBreakdown(UUID appId, TimeRange timeRange, List<FilterCriteria> filters) {
        validateTimeRange(timeRange);
        return eventRepository.getBrowserBreakdown(appId, timeRange, filters);
    }

    @Override
    public List<Map<String, Object>> getOsBreakdown(UUID appId, TimeRange timeRange, List<FilterCriteria> filters) {
        validateTimeRange(timeRange);
        return eventRepository.getOsBreakdown(appId, timeRange, filters);
    }

    /**
     * Validate time range
     */
    private void validateTimeRange(TimeRange timeRange) {
        if (timeRange == null || timeRange.getStartDate() == null || timeRange.getEndDate() == null) {
            throw new InvalidQueryException("Time range is required");
        }

        if (timeRange.getStartDate().isAfter(timeRange.getEndDate())) {
            throw new InvalidQueryException("Start date must be before end date");
        }

        long daysBetween = Duration.between(timeRange.getStartDate(), timeRange.getEndDate()).toDays();
        if (daysBetween > properties.getQuery().getMaxTimeRangeDays()) {
            throw new InvalidQueryException(
                    "Time range cannot exceed " + properties.getQuery().getMaxTimeRangeDays() + " days"
            );
        }
    }

    /**
     * Validate limit
     */
    private void validateLimit(int limit) {
        if (limit > properties.getQuery().getMaxResultSize()) {
            throw new InvalidQueryException(
                    "Limit cannot exceed " + properties.getQuery().getMaxResultSize()
            );
        }
    }

    /**
     * Determine granularity based on time range
     */
    private String determineGranularity(AnalyticsQueryRequest request) {
        if (request.getGranularity() != null) {
            return request.getGranularity().getClickHouseFunction();
        }

        // Auto-determine based on time range
        long hours = Duration.between(
                request.getTimeRange().getStartDate(),
                request.getTimeRange().getEndDate()
        ).toHours();

        if (hours <= 24) {
            return "toStartOfHour";
        }
        if (hours <= 168) {
            return "toDate"; // 7 days

                }if (hours <= 720) {
            return "toDate"; // 30 days

                }return "toStartOfMonth";
    }

    /**
     * Get previous period for comparison
     */
    private TimeRange getPreviousPeriod(TimeRange current) {
        long duration = Duration.between(current.getStartDate(), current.getEndDate()).toHours();
        return TimeRange.builder()
                .startDate(current.getStartDate().minusHours(duration))
                .endDate(current.getStartDate())
                .build();
    }

    /**
     * Build metric card with comparison
     */
    private MetricCard buildMetricCard(String label, long current, long previous) {
        double changePercentage = previous > 0
                ? ((double) (current - previous) / previous) * 100 : 0;

        String trend = "stable";
        if (changePercentage > 1) {
            trend = "up"; 
        }else if (changePercentage < -1) {
            trend = "down";
        }

        return MetricCard.builder()
                .label(label)
                .value(current)
                .formattedValue(String.format("%,d", current))
                .changePercentage(changePercentage)
                .trend(trend)
                .build();
    }
}
