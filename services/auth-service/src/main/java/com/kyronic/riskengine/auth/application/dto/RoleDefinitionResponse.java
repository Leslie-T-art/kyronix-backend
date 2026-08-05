package com.kyronic.riskengine.auth.application.dto;

import java.util.UUID;

public record RoleDefinitionResponse(
        UUID id,
        String code,
        String name,
        String description,
        boolean active
) {
}
