package com.kyronic.riskengine.auth.application.dto;

public record ReferenceDataResponse(
        Long id,
        String code,
        String name,
        boolean active
) {
}
