package com.simpleAnalytics.Analytics.repository;

import com.simpleAnalytics.Analytics.exception.QueryTimeoutException;
import com.simpleAnalytics.Analytics.model.dto.FilterCriteria;
import com.simpleAnalytics.Analytics.model.dto.TimeRange;
import com.simpleAnalytics.Analytics.model.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of EventRepository for ClickHouse queries
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final JdbcTemplate clickHouseJdbcTemplate;
    private final AnalyticsQueryBuilder queryBuilder;

    private static final String BASE_WHERE = " WHERE appId = ? AND event.timestamp BETWEEN ? AND ? ";

    @Override
    public long getTotalEventCount(UUID appId, TimeRange timeRange, List<FilterCriteria> filters) {
        List<Object> params = new ArrayList<>();
        params.add(appId);
        params.add(timeRange.getStartDate());
        params.add(timeRange.getEndDate());

        String filterClause = queryBuilder.buildWhereClause(filters, params);

        String sql = "SELECT count(*) FROM event " + BASE_WHERE + filterClause;

        log.debug("Executing getTotalEventCount: {}", sql);

        try {
            Long count = clickHouseJdbcTemplate.queryForObject(sql, Long.class, params.toArray());
            return count != null ? count : 0L;
        } catch (org.springframework.dao.QueryTimeoutException e) {
            throw new QueryTimeoutException("Query timed out while fetching total event count", e);
        } catch (Exception e) {
            log.error("Error executing getTotalEventCount", e);
            throw new com.simpleAnalytics.Analytics.exception.AnalyticsException("Error fetching total event count", e);
        }
    }

    @Override
    public long getUniqueUserCount(UUID appId, TimeRange timeRange, List<FilterCriteria> filters) {
        List<Object> params = new ArrayList<>();
        params.add(appId);
        params.add(timeRange.getStartDate());
        params.add(timeRange.getEndDate());

        String filterClause = queryBuilder.buildWhereClause(filters, params);

        String sql = "SELECT uniqExact(COALESCE(userId, anonymousId)) FROM event " +
                     BASE_WHERE + filterClause;

        log.debug("Executing getUniqueUserCount: {}", sql);

        try {
            Long count = clickHouseJdbcTemplate.queryForObject(sql, Long.class, params.toArray());
            return count != null ? count : 0L;
        } catch (Exception e) {
            throw new QueryTimeoutException("Query timed out while fetching unique user count", e);
        }
    }

    @Override
    public long getUniqueSessionCount(UUID appId, TimeRange timeRange, List<FilterCriteria> filters) {
        List<Object> params = new ArrayList<>();
        params.add(appId);
        params.add(timeRange.getStartDate());
        params.add(timeRange.getEndDate());

        String filterClause = queryBuilder.buildWhereClause(filters, params);

        String sql = "SELECT uniqExact(sessionId) FROM event" + 
                     BASE_WHERE + " AND sessionId IS NOT NULL " + filterClause;

        log.debug("Executing getUniqueSessionCount: {}", sql);

        try {
            Long count = clickHouseJdbcTemplate.queryForObject(sql, Long.class, params.toArray());
            return count != null ? count : 0L;
        } catch (Exception e) {
            throw new QueryTimeoutException("Query timed out while fetching unique session count", e);
        }
    }

    @Override
    public List<Map<String, Object>> getEventBreakdown(UUID appId, TimeRange timeRange, 
                                                        List<FilterCriteria> filters) {
        List<Object> params = new ArrayList<>();
        params.add(appId);
        params.add(timeRange.getStartDate());
        params.add(timeRange.getEndDate());

        String filterClause = queryBuilder.buildWhereClause(filters, params);

        String sql = """
            SELECT 
                eventType,
                count(*) as count,
                uniqExact(COALESCE(userId, anonymousId)) as uniqueUsers
            FROM event
            """ + BASE_WHERE + filterClause + """
            \s
            GROUP BY eventType
            ORDER BY count DESC
            """;

        log.debug("Executing getEventBreakdown: {}", sql);

        try {
            return clickHouseJdbcTemplate.queryForList(sql, params.toArray());
        } catch (Exception e) {
            throw new QueryTimeoutException("Query timed out while fetching event breakdown", e);
        }
    }

    @Override
    public List<Map<String, Object>> getSourceBreakdown(UUID appId, TimeRange timeRange, 
                                                         List<FilterCriteria> filters) {
        List<Object> params = new ArrayList<>();
        params.add(appId);
        params.add(timeRange.getStartDate());
        params.add(timeRange.getEndDate());

        String filterClause = queryBuilder.buildWhereClause(filters, params);

        String sql = """
            SELECT 
                source,
                count(*) as count
            FROM event
            """ + BASE_WHERE + filterClause + """
            \s
            GROUP BY source
            ORDER BY count DESC
            """;

        log.debug("Executing getSourceBreakdown: {}", sql);

        try {
            return clickHouseJdbcTemplate.queryForList(sql, params.toArray());
        } catch (Exception e) {
            throw new QueryTimeoutException("Query timed out while fetching source breakdown", e);
        }
    }

    @Override
    public List<Map<String, Object>> getTimeSeriesData(UUID appId, TimeRange timeRange, 
                                                        String granularity, List<FilterCriteria> filters) {
        List<Object> params = new ArrayList<>();
        params.add(appId);
        params.add(timeRange.getStartDate());
        params.add(timeRange.getEndDate());

        String filterClause = queryBuilder.buildWhereClause(filters, params);

        // Build the time grouping expression based on granularity
        String timeExpression = String.format("%s(event.timestamp)", granularity);
        
        String sql = """
            SELECT\s
                %s as eventTimeStamp,
                count(*) as count
            FROM event
           \s""" + BASE_WHERE + filterClause + """
            \s
            GROUP BY eventTimeStamp
            ORDER BY eventTimeStamp ASC
            """;

        sql = String.format(sql, timeExpression);

        log.debug("Executing getTimeSeriesData with granularity {}: {}", granularity, sql);

        try {
            return clickHouseJdbcTemplate.queryForList(sql, params.toArray());
        } catch (Exception e) {
            log.error("Query Error: {}",e.getMessage());
            throw new QueryTimeoutException("Query timed out while fetching time series data", e);
        }
    }

    @Override
    public List<Map<String, Object>> getTopEvents(UUID appId, TimeRange timeRange, 
                                                   int limit, List<FilterCriteria> filters) {
        List<Object> params = new ArrayList<>();
        params.add(appId);
        params.add(timeRange.getStartDate());
        params.add(timeRange.getEndDate());

        String filterClause = queryBuilder.buildWhereClause(filters, params);

        String sql = """
            SELECT 
                eventType,
                count(*) as count
            FROM event
            """ + BASE_WHERE + filterClause + """
            \s
            GROUP BY eventType
            ORDER BY count DESC
            LIMIT ?
            """;

        params.add(limit);

        log.debug("Executing getTopEvents: {}", sql);

        try {
            return clickHouseJdbcTemplate.queryForList(sql, params.toArray());
        } catch (Exception e) {
            throw new QueryTimeoutException("Query timed out while fetching top events", e);
        }
    }

    @Override
    public List<Event> getEvents(UUID appId, TimeRange timeRange, 
                                 List<FilterCriteria> filters, int limit, int offset) {
        List<Object> params = new ArrayList<>();
        params.add(appId);
        params.add(timeRange.getStartDate());
        params.add(timeRange.getEndDate());

        String filterClause = queryBuilder.buildWhereClause(filters, params);
        String limitClause = queryBuilder.buildLimitClause(limit, offset);

        String sql = """
                 SELECT\s
                     id, appId, anonymousId, userId, sessionId, eventType,
                     receivedAt, timestamp, source, metadata,
                     context_ip, context_os, context_ua, context_device,
                     context_browser, context_locale, context_timezone, schemaVersion
                 FROM event
                \s""" + BASE_WHERE + filterClause + """
                \s
                ORDER BY timestamp DESC
                \s
                """ + limitClause;

        log.debug("Executing getEvents: {}", sql);

        try {
            return clickHouseJdbcTemplate.query(sql, params.toArray(), this::mapRowToEvent);
        } catch (Exception e) {
            throw new QueryTimeoutException("Query timed out while fetching events", e);
        }
    }
    //The query needs fixing
    @Override
    public List<Map<String, Object>> getUserRetention(UUID appId, TimeRange timeRange) {
        List<Object> params = new ArrayList<>();
        params.add(appId);
        params.add(timeRange.getStartDate());
        params.add(timeRange.getEndDate());

        String sql = """
            SELECT 
                toDate(first_seen) as cohortDate,
                dateDiff('day', first_seen, timestamp) as dayNumber,
                uniqExact(userId) as retainedUsers
            FROM (
                SELECT 
                    userId,
                    min(timestamp) OVER (PARTITION BY userId) as first_seen,
                    timestamp
                FROM event
                WHERE appId = ? 
                    AND timestamp BETWEEN ? AND ?
                    AND userId IS NOT NULL
            ) as uIfst
            GROUP BY cohortDate, dayNumber
            ORDER BY cohortDate, dayNumber
            """;

        log.debug("Executing getUserRetention: {}", sql);

        try {
            return clickHouseJdbcTemplate.queryForList(sql, params.toArray());
        } catch (Exception e) {
            throw new QueryTimeoutException("Query timed out while fetching user retention", e);
        }
    }

    @Override
    public List<Map<String, Object>> getFunnelData(UUID appId, List<String> steps, 
                                                    TimeRange timeRange, int timeWindowMinutes) {
        // Build dynamic funnel query based on number of steps
        // This is a simplified version - can be enhanced for more complex funnels
        
        List<Object> params = new ArrayList<>();
        params.add(appId);
        params.add(timeRange.getStartDate());
        params.add(timeRange.getEndDate());
        params.add(steps.get(0));

        String sql = """
            SELECT 
                eventType,
                count(DISTINCT COALESCE(userId, anonymousId)) as userCount
            FROM eventsdb.event
            WHERE appId = ? 
                AND timestamp BETWEEN ? AND ?
                AND eventType = ?
            \s
            GROUP BY eventType
            """;

        log.debug("Executing getFunnelData: {}", sql);

        try {
            return clickHouseJdbcTemplate.queryForList(sql, params.toArray());
        } catch (Exception e) {
            throw new QueryTimeoutException("Query timed out while fetching funnel data", e);
        }
    }

    @Override
    public List<Map<String, Object>> getDeviceBreakdown(UUID appId, TimeRange timeRange, 
                                                         List<FilterCriteria> filters) {
        return getContextBreakdown(appId, timeRange, filters, "context_device");
    }

    @Override
    public List<Map<String, Object>> getBrowserBreakdown(UUID appId, TimeRange timeRange, 
                                                          List<FilterCriteria> filters) {
        return getContextBreakdown(appId, timeRange, filters, "context_browser");
    }

    @Override
    public List<Map<String, Object>> getOsBreakdown(UUID appId, TimeRange timeRange, 
                                                     List<FilterCriteria> filters) {
        return getContextBreakdown(appId, timeRange, filters, "context_os");
    }

    @Override
    public List<Map<String, Object>> getLocaleBreakdown(UUID appId, TimeRange timeRange, 
                                                         List<FilterCriteria> filters) {
        return getContextBreakdown(appId, timeRange, filters, "context_locale");
    }

    private List<Map<String, Object>> getContextBreakdown(UUID appId, TimeRange timeRange, 
                                                           List<FilterCriteria> filters, String field) {
        List<Object> params = new ArrayList<>();
        params.add(appId);
        params.add(timeRange.getStartDate());
        params.add(timeRange.getEndDate());

        String filterClause = queryBuilder.buildWhereClause(filters, params);

        String sql = String.format("""
            SELECT\s
                %s as value,
                count(*) as count
            FROM event
           \s""" + BASE_WHERE + " AND %s IS NOT NULL" + filterClause + """
            \s
            GROUP BY value
            ORDER BY count DESC
            LIMIT 20
            """, field, field);

        log.debug("Executing getContextBreakdown for {}: {}", field, sql);

        try {
            return clickHouseJdbcTemplate.queryForList(sql, params.toArray());

        }

        catch (Exception e) {
            log.error("Error while getting context breakdown: {}", e.getMessage());
            throw new QueryTimeoutException("Query timed out while fetching " + field + " breakdown", e);
        }
    }

    @Override
    public List<Map<String, Object>> executeCustomQuery(String query, Object[] params) {
        log.debug("Executing custom query: {}", query);
        
        try {
            return clickHouseJdbcTemplate.queryForList(query, params);
        } catch (Exception e) {
            throw new QueryTimeoutException("Query timed out while executing custom query", e);
        }
    }

    /**
     * Map ResultSet row to Event entity
     */
    private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .id(UUID.fromString(rs.getString("id")))
                .appId(UUID.fromString(rs.getString("appId")))
                .anonymousId(rs.getString("anonymousId") != null ? 
                    UUID.fromString(rs.getString("anonymousId")) : null)
                .userId(rs.getString("userId") != null ? 
                    UUID.fromString(rs.getString("userId")) : null)
                .sessionId(rs.getString("sessionId") != null ? 
                    UUID.fromString(rs.getString("sessionId")) : null)
                .eventType(rs.getString("eventType"))
                .receivedAt(rs.getTimestamp("receivedAt").toLocalDateTime())
                .timestamp(rs.getTimestamp("timestamp").toLocalDateTime())
                .source(rs.getString("source"))
                .metadata(rs.getString("metadata"))
                .contextIp(rs.getString("context_ip"))
                .contextOs(rs.getString("context_os"))
                .contextUa(rs.getString("context_ua"))
                .contextDevice(rs.getString("context_device"))
                .contextBrowser(rs.getString("context_browser"))
                .contextLocale(rs.getString("context_locale"))
                .contextTimezone(rs.getString("context_timezone"))
                .schemaVersion(rs.getString("schemaVersion"))
                .build();
    }
}
