package com.simpleAnalytics.Analytics.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic analytics response wrapper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyticsResponse<T> {

    private T data;

    private ResponseMetadata metadata;

    private PaginationInfo pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseMetadata {

        private LocalDateTime queryTime;
        private Long executionTimeMs;
        private Boolean cacheHit;
        private String query;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {

        private Integer page;
        private Integer pageSize;
        private Long totalRecords;
        private Integer totalPages;
        private Boolean hasNext;
        private Boolean hasPrevious;
    }
}
