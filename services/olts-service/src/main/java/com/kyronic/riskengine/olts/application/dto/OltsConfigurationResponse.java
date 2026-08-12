package com.kyronic.riskengine.olts.application.dto;

import java.time.Instant;

public record OltsConfigurationResponse(
        Long id,
        String code,
        String name,
        String description,
        Integer displayOrder,
        Long createdBy,
        Instant createdAt,
        Long updatedBy,
        Instant updatedAt
) {
}
