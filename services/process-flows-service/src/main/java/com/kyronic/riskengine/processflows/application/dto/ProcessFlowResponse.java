package com.kyronic.riskengine.processflows.application.dto;

import java.time.Instant;

public record ProcessFlowResponse(
        Long id,
        String flowReference,
        String name,
        Long departmentId,
        String processOwner,
        String status,
        String description,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {
}
