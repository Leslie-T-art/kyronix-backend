package com.kyronic.riskengine.riskregister.application.dto;

import java.time.Instant;
import java.time.LocalDate;

public record RiskRecordResponse(
        Long id,
        String riskId,
        String riskTitle,
        String category,
        String owner,
        String businessUnit,
        String description,
        Integer likelihood,
        Integer impact,
        String inherentRating,
        String controlsMapped,
        String controlEffectiveness,
        String residualRating,
        String treatmentStrategy,
        String status,
        LocalDate nextReviewDate,
        String linkedProcess,
        String linkedKri,
        String actionPlan,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {
}
