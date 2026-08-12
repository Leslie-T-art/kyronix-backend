package com.kyronic.riskengine.kri.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record KriResponse(
        Long id,
        String kriId,
        String indicatorName,
        String category,
        String owner,
        String businessUnit,
        String measurementFrequency,
        String description,
        String unitOfMeasure,
        BigDecimal target,
        String direction,
        BigDecimal greenUpperBound,
        BigDecimal amberThreshold,
        BigDecimal redThreshold,
        BigDecimal currentValue,
        String dataSource,
        LocalDate nextReviewDate,
        String linkedRisk,
        String escalateTo,
        String escalationTrigger,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {
}
