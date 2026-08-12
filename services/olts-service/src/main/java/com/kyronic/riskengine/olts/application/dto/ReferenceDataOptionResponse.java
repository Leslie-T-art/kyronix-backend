package com.kyronic.riskengine.olts.application.dto;

public record ReferenceDataOptionResponse(
        Long id,
        String code,
        String name,
        boolean active
) {
}
