package com.simpleAnalytics.Analytics.model.enums;

/**
 * Types of metrics that can be calculated
 */
public enum MetricType {
    COUNT, // count(*)
    UNIQUE_COUNT, // uniqExact(field)
    SUM, // sum(field)
    AVERAGE, // avg(field)
    MIN, // min(field)
    MAX, // max(field)
    MEDIAN, // median(field)
    PERCENTILE_95, // quantile(0.95)(field)
    PERCENTILE_99       // quantile(0.99)(field)
}
