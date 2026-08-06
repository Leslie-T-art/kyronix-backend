package com.kyronic.riskengine.olts.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LossCategoryRequest(
        @NotBlank @Size(max = 50) String code,
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 1000) String description
) {
}
