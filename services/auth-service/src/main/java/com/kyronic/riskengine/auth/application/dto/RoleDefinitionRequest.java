package com.kyronic.riskengine.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoleDefinitionRequest(
        @NotBlank String code,
        @NotBlank String name,
        @NotBlank String description,
        @NotNull Boolean active
) {
}
