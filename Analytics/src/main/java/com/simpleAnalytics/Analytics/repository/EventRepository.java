package com.simpleAnalytics.Analytics.repository;

import com.simpleAnalytics.Analytics.model.dto.FilterCriteria;
import com.simpleAnalytics.Analytics.model.dto.TimeRange;
import com.simpleAnalytics.Analytics.model.entity.Event;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Repository interface for Event analytics queries
 */
public interface EventRepository {

    /**
     * Get total event count for an app
     */
    long getTotalEventCount(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Get unique user count for an app
     */
    long getUniqueUserCount(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Get unique session count for an app
     */
    long getUniqueSessionCount(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Get event breakdown by event type
     */
    List<Map<String, Object>> getEventBreakdown(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Get source breakdown
     */
    List<Map<String, Object>> getSourceBreakdown(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Get time series data
     */
    List<Map<String, Object>> getTimeSeriesData(UUID appId, TimeRange timeRange,
            String granularity, List<FilterCriteria> filters);

    /**
     * Get top events by count
     */
    List<Map<String, Object>> getTopEvents(UUID appId, TimeRange timeRange,
            int limit, List<FilterCriteria> filters);

    /**
     * Get events with pagination
     */
    List<Event> getEvents(UUID appId, TimeRange timeRange,
            List<FilterCriteria> filters, int limit, int offset);

    /**
     * Get user retention data
     */
    List<Map<String, Object>> getUserRetention(UUID appId, TimeRange timeRange);

    /**
     * Get funnel data for multiple steps
     */
    List<Map<String, Object>> getFunnelData(UUID appId, List<String> steps,
            TimeRange timeRange, int timeWindowMinutes);

    /**
     * Get device breakdown
     */
    List<Map<String, Object>> getDeviceBreakdown(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Get browser breakdown
     */
    List<Map<String, Object>> getBrowserBreakdown(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Get OS breakdown
     */
    List<Map<String, Object>> getOsBreakdown(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Get locale breakdown
     */
    List<Map<String, Object>> getLocaleBreakdown(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Execute custom analytics query
     */
    List<Map<String, Object>> executeCustomQuery(String query, Object[] params);
}
