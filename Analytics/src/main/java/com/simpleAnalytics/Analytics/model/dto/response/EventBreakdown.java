package com.simpleAnalytics.Analytics.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event breakdown by type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventBreakdown {

    private String eventType;
    private Long count;
    private Double percentage;
    private Long uniqueUsers;
}
