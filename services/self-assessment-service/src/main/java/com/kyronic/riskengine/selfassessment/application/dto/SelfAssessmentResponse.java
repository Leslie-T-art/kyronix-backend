package com.kyronic.riskengine.selfassessment.application.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

public record SelfAssessmentResponse(
        Long id,
        String rcsaId,
        String assessmentPeriod,
        Long departmentId,
        String processName,
        String riskRegisterRisk,
        String riskScenario,
        String cause,
        String consequenceImpact,
        Integer inherentImpact,
        Integer inherentLikelihood,
        Integer inherentRiskScore,
        String inherentRiskRating,
        Set<String> linkedControls,
        String controlDesignEffectiveness,
        String controlOperatingEffectiveness,
        String overallControlEffectiveness,
        Integer residualImpact,
        Integer residualLikelihood,
        Integer residualRiskScore,
        String residualRiskRating,
        String riskResponse,
        boolean actionRequired,
        String linkedAction,
        Set<String> linkedKris,
        Set<String> linkedOltsEvents,
        Set<String> linkedIssuesFindings,
        String businessReviewStatus,
        String riskReviewVerification,
        String riskReviewComment,
        LocalDate dateOfLastReview,
        LocalDate nextReviewDate,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {
}
