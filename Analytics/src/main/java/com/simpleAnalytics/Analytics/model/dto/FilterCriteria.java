package com.simpleAnalytics.Analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.simpleAnalytics.Analytics.model.enums.FilterOperator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Filter criteria for analytics queries
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterCriteria {

    @NotBlank(message = "Field name is required")
    private String field;

    @NotNull(message = "Operator is required")
    private FilterOperator operator;

    private Object value;

    private List<Object> values; // For IN, NOT IN operators
}
