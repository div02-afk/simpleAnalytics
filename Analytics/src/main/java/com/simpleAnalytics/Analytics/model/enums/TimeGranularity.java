package com.simpleAnalytics.Analytics.model.enums;

/**
 * Time granularity for time series data aggregation
 */
public enum TimeGranularity {
    HOUR("toStartOfHour", "Hour"),
    DAY("toDate", "Day"),
    WEEK("toMonday", "Week"),
    MONTH("toStartOfMonth", "Month");

    private final String clickHouseFunction;
    private final String displayName;

    TimeGranularity(String clickHouseFunction, String displayName) {
        this.clickHouseFunction = clickHouseFunction;
        this.displayName = displayName;
    }

    public String getClickHouseFunction() {
        return clickHouseFunction;
    }

    public String getDisplayName() {
        return displayName;
    }
}
