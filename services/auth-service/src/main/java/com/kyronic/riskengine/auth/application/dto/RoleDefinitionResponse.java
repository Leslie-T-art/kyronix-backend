package com.kyronic.riskengine.auth.application.dto;

public record RoleDefinitionResponse(
        Long id,
        String code,
        String name,
        String description,
        boolean active
) {
}
