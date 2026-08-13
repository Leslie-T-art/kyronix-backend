package com.kyronic.riskengine.kri.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TreatmentStrategyRequest(
        @NotBlank @Size(max = 100) String code,
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 50) String status
) {
}
