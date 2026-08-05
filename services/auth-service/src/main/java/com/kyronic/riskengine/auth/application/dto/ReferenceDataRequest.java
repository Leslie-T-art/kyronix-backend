package com.kyronic.riskengine.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReferenceDataRequest(
        @NotBlank String code,
        @NotBlank String name,
        @NotNull Boolean active
) {
}
