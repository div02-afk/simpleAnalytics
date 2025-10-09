package com.simpleAnalytics.Analytics.service;

import com.simpleAnalytics.Analytics.model.dto.FilterCriteria;
import com.simpleAnalytics.Analytics.model.dto.TimeRange;
import com.simpleAnalytics.Analytics.model.dto.request.AnalyticsQueryRequest;
import com.simpleAnalytics.Analytics.model.dto.request.FunnelRequest;
import com.simpleAnalytics.Analytics.model.dto.response.*;
import com.simpleAnalytics.Analytics.model.entity.Event;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for analytics operations
 */
public interface AnalyticsService {

    /**
     * Get dashboard overview with key metrics
     */
    DashboardOverview getDashboardOverview(UUID appId, TimeRange timeRange);

    /**
     * Get total event count
     */
    long getTotalEventCount(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Get unique user count
     */
    long getUniqueUserCount(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Get active session count
     */
    long getActiveSessionCount(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Get event breakdown by type
     */
    List<EventBreakdown> getEventBreakdown(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Get source breakdown
     */
    List<SourceBreakdown> getSourceBreakdown(UUID appId, TimeRange timeRange, List<FilterCriteria> filters);

    /**
     * Get time series data
     */
    List<TimeSeriesData> getTimeSeriesData(AnalyticsQueryRequest request);

    /**
     * Get top events
     */
    List<EventBreakdown> getTopEvents(UUID appId, TimeRange timeRange, int limit, List<FilterCriteria> filters);

    /**
     * Get events with pagination
     */
    List<Event> getEvents(UUID appId, TimeRange timeRange, List<FilterCriteria> filters, int limit, int offset);

    /**
     * Get funnel analysis
     */
    FunnelAnalysis getFunnelAnalysis(FunnelRequest request);

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
}
