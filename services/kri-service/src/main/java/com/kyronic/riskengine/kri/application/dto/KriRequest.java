package com.kyronic.riskengine.kri.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record KriRequest(
        @NotBlank String indicatorName,
        @NotBlank String category,
        @NotBlank String owner,
        @NotBlank String businessUnit,
        @NotBlank String measurementFrequency,
        @NotBlank String description,
        @NotBlank String unitOfMeasure,
        @NotNull @DecimalMin("0.0") BigDecimal target,
        @NotBlank String direction,
        @NotNull BigDecimal greenUpperBound,
        @NotNull BigDecimal amberThreshold,
        @NotNull BigDecimal redThreshold,
        @NotNull BigDecimal currentValue,
        @NotBlank String dataSource,
        @NotNull @FutureOrPresent LocalDate nextReviewDate,
        @NotBlank String linkedRisk,
        @NotBlank String escalateTo,
        @NotBlank String escalationTrigger
) {
}
