package com.kyronic.riskengine.kri.application.dto;

import java.time.Instant;

public record TreatmentStrategyResponse(
        Long id,
        String code,
        String name,
        String status,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {
}
