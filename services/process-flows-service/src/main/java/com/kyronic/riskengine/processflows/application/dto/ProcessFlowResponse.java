package com.kyronic.riskengine.processflows.application.dto;

import com.kyronic.riskengine.processflows.domain.ProcessFlowWorkflowStatus;

import java.time.Instant;
import java.time.LocalDate;

public record ProcessFlowResponse(
        Long id,
        String flowReference,
        String processFlowName,
        Long departmentId,
        String description,
        LocalDate validFromDate,
        LocalDate validToDate,
        ProcessFlowWorkflowStatus workflowStatus,
        String originalFileName,
        String contentType,
        Long fileSize,
        String bucketName,
        String objectKey,
        Long inputterUserId,
        String inputterUsername,
        Long authorizerUserId,
        String authorizerUsername,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {
}
