package com.simpleAnalytics.Analytics.controller;

import com.simpleAnalytics.Analytics.model.dto.TimeRange;
import com.simpleAnalytics.Analytics.model.dto.request.AnalyticsQueryRequest;
import com.simpleAnalytics.Analytics.model.dto.response.*;
import com.simpleAnalytics.Analytics.model.enums.TimeGranularity;
import com.simpleAnalytics.Analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Analytics endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics API for event data")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/{appId}/overview")
    @Operation(summary = "Get dashboard overview", description = "Get comprehensive dashboard overview with key metrics")
    public ResponseEntity<AnalyticsResponse<DashboardOverview>> getOverview(
            @Parameter(description = "Application ID") @PathVariable UUID appId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("GET /api/v1/analytics/{}/overview - startDate: {}, endDate: {}", appId, startDate, endDate);

        LocalDateTime queryStart = LocalDateTime.now();
        TimeRange timeRange = TimeRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        DashboardOverview overview = analyticsService.getDashboardOverview(appId, timeRange);

        AnalyticsResponse<DashboardOverview> response = AnalyticsResponse.<DashboardOverview>builder()
                .data(overview)
                .metadata(AnalyticsResponse.ResponseMetadata.builder()
                        .queryTime(queryStart)
                        .executionTimeMs(java.time.Duration.between(queryStart, LocalDateTime.now()).toMillis())
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{appId}/events/count")
    @Operation(summary = "Get total event count", description = "Get total count of events for an application")
    public ResponseEntity<AnalyticsResponse<Long>> getEventCount(
            @PathVariable UUID appId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("GET /api/v1/analytics/{}/events/count", appId);

        TimeRange timeRange = TimeRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        long count = analyticsService.getTotalEventCount(appId, timeRange, null);

        AnalyticsResponse<Long> response = AnalyticsResponse.<Long>builder()
                .data(count)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{appId}/users/count")
    @Operation(summary = "Get unique user count", description = "Get count of unique users")
    public ResponseEntity<AnalyticsResponse<Long>> getUserCount(
            @PathVariable UUID appId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("GET /api/v1/analytics/{}/users/count", appId);

        TimeRange timeRange = TimeRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        long count = analyticsService.getUniqueUserCount(appId, timeRange, null);

        AnalyticsResponse<Long> response = AnalyticsResponse.<Long>builder()
                .data(count)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{appId}/events/breakdown")
    @Operation(summary = "Get event breakdown", description = "Get breakdown of events by event type")
    public ResponseEntity<AnalyticsResponse<List<EventBreakdown>>> getEventBreakdown(
            @PathVariable UUID appId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("GET /api/v1/analytics/{}/events/breakdown", appId);

        TimeRange timeRange = TimeRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        List<EventBreakdown> breakdown = analyticsService.getEventBreakdown(appId, timeRange, null);

        AnalyticsResponse<List<EventBreakdown>> response = AnalyticsResponse.<List<EventBreakdown>>builder()
                .data(breakdown)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{appId}/sources/breakdown")
    @Operation(summary = "Get source breakdown", description = "Get breakdown of events by source")
    public ResponseEntity<AnalyticsResponse<List<SourceBreakdown>>> getSourceBreakdown(
            @PathVariable UUID appId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("GET /api/v1/analytics/{}/sources/breakdown", appId);

        TimeRange timeRange = TimeRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        List<SourceBreakdown> breakdown = analyticsService.getSourceBreakdown(appId, timeRange, null);

        AnalyticsResponse<List<SourceBreakdown>> response = AnalyticsResponse.<List<SourceBreakdown>>builder()
                .data(breakdown)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{appId}/timeseries?granularity")
    @Operation(summary = "Get time series data", description = "Get time series event data with custom granularity")
    public ResponseEntity<AnalyticsResponse<List<TimeSeriesData>>> getTimeSeries(
            @PathVariable UUID appId,
            @RequestParam TimeGranularity granularity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        AnalyticsQueryRequest request = AnalyticsQueryRequest.builder().appId(appId).granularity(granularity).timeRange(TimeRange.builder().startDate(startDate).endDate(endDate).build()).build();
        log.info("POST /api/v1/analytics/{}/timeseries", appId);

        List<TimeSeriesData> timeSeries = analyticsService.getTimeSeriesData(request);
        log.info("Time series data retrieved: {}", timeSeries.size());
        AnalyticsResponse<List<TimeSeriesData>> response = AnalyticsResponse.<List<TimeSeriesData>>builder()
                .data(timeSeries)
                .build();
        log.info("Response: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{appId}/context/devices")
    @Operation(summary = "Get device breakdown", description = "Get breakdown by device type")
    public ResponseEntity<AnalyticsResponse<List<Map<String, Object>>>> getDeviceBreakdown(
            @PathVariable UUID appId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        TimeRange timeRange = TimeRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        List<Map<String, Object>> breakdown = analyticsService.getDeviceBreakdown(appId, timeRange, null);

        AnalyticsResponse<List<Map<String, Object>>> response = AnalyticsResponse.<List<Map<String, Object>>>builder()
                .data(breakdown)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{appId}/context/browsers")
    @Operation(summary = "Get browser breakdown", description = "Get breakdown by browser")
    public ResponseEntity<AnalyticsResponse<List<Map<String, Object>>>> getBrowserBreakdown(
            @PathVariable UUID appId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        TimeRange timeRange = TimeRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        List<Map<String, Object>> breakdown = analyticsService.getBrowserBreakdown(appId, timeRange, null);

        AnalyticsResponse<List<Map<String, Object>>> response = AnalyticsResponse.<List<Map<String, Object>>>builder()
                .data(breakdown)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{appId}/context/os")
    @Operation(summary = "Get OS breakdown", description = "Get breakdown by operating system")
    public ResponseEntity<AnalyticsResponse<List<Map<String, Object>>>> getOsBreakdown(
            @PathVariable UUID appId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        TimeRange timeRange = TimeRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        List<Map<String, Object>> breakdown = analyticsService.getOsBreakdown(appId, timeRange, null);

        AnalyticsResponse<List<Map<String, Object>>> response = AnalyticsResponse.<List<Map<String, Object>>>builder()
                .data(breakdown)
                .build();

        return ResponseEntity.ok(response);
    }
}
