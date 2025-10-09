package com.simpleAnalytics.Analytics.repository;

import com.simpleAnalytics.Analytics.model.dto.FilterCriteria;
import com.simpleAnalytics.Analytics.model.enums.FilterOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for constructing ClickHouse SQL queries dynamically
 */
@Slf4j
@Component
public class AnalyticsQueryBuilder {

    private static final String TABLE_NAME = "event";

    /**
     * Build WHERE clause with filters
     */
    public String buildWhereClause(List<FilterCriteria> filters, List<Object> params) {
        if (filters == null || filters.isEmpty()) {
            return "";
        }

        StringBuilder whereClause = new StringBuilder(" AND ");
        List<String> conditions = new ArrayList<>();

        for (FilterCriteria filter : filters) {
            String condition = buildFilterCondition(filter, params);
            if (condition != null && !condition.isEmpty()) {
                conditions.add(condition);
            }
        }

        if (conditions.isEmpty()) {
            return "";
        }

        whereClause.append(String.join(" AND ", conditions));
        return whereClause.toString();
    }

    /**
     * Build individual filter condition
     */
    private String buildFilterCondition(FilterCriteria filter, List<Object> params) {
        String field = sanitizeFieldName(filter.getField());
        FilterOperator operator = filter.getOperator();

        switch (operator) {
            case EQUALS:
            case NOT_EQUALS:
            case GREATER_THAN:
            case LESS_THAN:
            case GREATER_THAN_OR_EQUAL:
            case LESS_THAN_OR_EQUAL:
                params.add(filter.getValue());
                return field + " " + operator.getSqlOperator() + " ?";

            case LIKE:
            case NOT_LIKE:
                params.add("%" + filter.getValue() + "%");
                return field + " " + operator.getSqlOperator() + " ?";

            case IN:
                if (filter.getValues() != null && !filter.getValues().isEmpty()) {
                    StringBuilder inClause = new StringBuilder(field + " IN (");
                    for (int i = 0; i < filter.getValues().size(); i++) {
                        inClause.append("?");
                        if (i < filter.getValues().size() - 1) {
                            inClause.append(", ");
                        }
                        params.add(filter.getValues().get(i));
                    }
                    inClause.append(")");
                    return inClause.toString();
                }
                break;

            case NOT_IN:
                if (filter.getValues() != null && !filter.getValues().isEmpty()) {
                    StringBuilder notInClause = new StringBuilder(field + " NOT IN (");
                    for (int i = 0; i < filter.getValues().size(); i++) {
                        notInClause.append("?");
                        if (i < filter.getValues().size() - 1) {
                            notInClause.append(", ");
                        }
                        params.add(filter.getValues().get(i));
                    }
                    notInClause.append(")");
                    return notInClause.toString();
                }
                break;

            case IS_NULL:
                return field + " IS NULL";

            case IS_NOT_NULL:
                return field + " IS NOT NULL";
        }

        return null;
    }

    /**
     * Sanitize field name to prevent SQL injection
     */
    private String sanitizeFieldName(String fieldName) {
        // Map user-friendly names to actual column names
        switch (fieldName.toLowerCase()) {
            case "eventtype":
            case "event_type":
                return "eventType";
            case "userid":
            case "user_id":
                return "userId";
            case "sessionid":
            case "session_id":
                return "sessionId";
            case "anonymousid":
            case "anonymous_id":
                return "anonymousId";
            case "appid":
            case "app_id":
                return "appId";
            case "source":
                return "source";
            case "metadata":
                return "metadata";
            case "context_ip":
            case "ip":
                return "context_ip";
            case "context_os":
            case "os":
                return "context_os";
            case "context_ua":
            case "user_agent":
                return "context_ua";
            case "context_device":
            case "device":
                return "context_device";
            case "context_browser":
            case "browser":
                return "context_browser";
            case "context_locale":
            case "locale":
                return "context_locale";
            case "context_timezone":
            case "timezone":
                return "context_timezone";
            default:
                // Only allow alphanumeric and underscore
                if (fieldName.matches("^[a-zA-Z0-9_]+$")) {
                    return fieldName;
                }
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }

    /**
     * Build ORDER BY clause
     */
    public String buildOrderByClause(String orderBy, String direction) {
        if (orderBy == null || orderBy.isEmpty()) {
            return "";
        }

        String field = sanitizeFieldName(orderBy);
        String dir = "DESC".equalsIgnoreCase(direction) ? "DESC" : "ASC";
        return " ORDER BY " + field + " " + dir;
    }

    /**
     * Build LIMIT clause
     */
    public String buildLimitClause(int limit, int offset) {
        if (offset > 0) {
            return " LIMIT " + limit + " OFFSET " + offset;
        }
        return " LIMIT " + limit;
    }
}
