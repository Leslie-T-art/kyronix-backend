package com.kyronic.riskengine.selfassessment.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record SelfAssessmentRequest(
        @NotBlank String assessmentPeriod,
        @NotNull Long departmentId,
        @NotBlank String processName,
        @NotBlank String riskRegisterRisk,
        @NotBlank String riskScenario,
        @NotBlank String cause,
        @NotBlank String consequenceImpact,
        @NotNull @Min(1) @Max(5) Integer inherentImpact,
        @NotNull @Min(1) @Max(5) Integer inherentLikelihood,
        @NotEmpty Set<String> linkedControls,
        @NotBlank String controlDesignEffectiveness,
        @NotBlank String controlOperatingEffectiveness,
        @NotBlank String overallControlEffectiveness,
        @NotNull @Min(1) @Max(5) Integer residualImpact,
        @NotNull @Min(1) @Max(5) Integer residualLikelihood,
        @NotBlank String riskResponse,
        boolean actionRequired,
        @Size(max = 500)
        String linkedAction,
        @NotEmpty Set<String> linkedKris,
        @NotEmpty Set<String> linkedOltsEvents,
        @NotEmpty Set<String> linkedIssuesFindings,
        @NotBlank String businessReviewStatus,
        @NotBlank String riskReviewVerification,
        @Size(max = 2000)
        String riskReviewComment,
        LocalDate dateOfLastReview,
        LocalDate nextReviewDate
) {
}
