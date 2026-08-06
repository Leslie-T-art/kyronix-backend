package com.kyronic.riskengine.riskregister.application.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RiskRecordRequest(
        @NotBlank String riskTitle,
        @NotBlank String category,
        @NotBlank String owner,
        @NotBlank String businessUnit,
        @NotBlank String description,
        @NotNull @Min(1) @Max(5) Integer likelihood,
        @NotNull @Min(1) @Max(5) Integer impact,
        @NotBlank String inherentRating,
        @NotBlank String controlsMapped,
        @NotBlank String controlEffectiveness,
        @NotBlank String residualRating,
        @NotBlank String treatmentStrategy,
        @NotBlank String status,
        @NotNull @FutureOrPresent LocalDate nextReviewDate,
        @NotBlank String linkedProcess,
        @NotBlank String linkedKri,
        @NotBlank String actionPlan
) {
}
