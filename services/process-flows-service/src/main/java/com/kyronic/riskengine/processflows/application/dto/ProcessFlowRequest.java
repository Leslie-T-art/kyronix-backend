package com.kyronic.riskengine.processflows.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProcessFlowRequest(
        @NotBlank String name,
        @NotNull Long departmentId,
        @NotBlank String processOwner,
        @NotBlank String status,
        @Size(max = 4000) String description
) {
}
