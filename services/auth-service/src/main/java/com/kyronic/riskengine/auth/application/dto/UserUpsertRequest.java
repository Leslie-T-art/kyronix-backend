package com.kyronic.riskengine.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record UserUpsertRequest(
        @NotBlank String username,
        @NotBlank String fullName,
        @NotBlank String password,
        @NotNull Boolean active,
        @NotNull Boolean locked,
        UUID departmentId,
        UUID branchId,
        @NotEmpty Set<String> roles,
        @NotNull Set<String> permissions
) {
}
