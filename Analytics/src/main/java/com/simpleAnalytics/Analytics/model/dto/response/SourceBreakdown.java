package com.simpleAnalytics.Analytics.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Source breakdown
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceBreakdown {

    private String source;
    private Long count;
    private Double percentage;
}
