package com.kyronic.riskengine.olts.domain.model;

import com.kyronic.riskengine.common.authorization.AuthorizationException;
import com.kyronic.riskengine.common.authorization.AuthorizationStatus;
import com.kyronic.riskengine.common.authorization.SegregationOfDutiesPolicy;
import com.kyronic.riskengine.common.api.ErrorCodes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class OltsIncident {

    private final UUID id;
    private final String incidentId;
    private final Long inputterUserId;
    private final Long createdBy;
    private final Instant createdAt;

    private String eventTitle;
    private Long eventStatusId;
    private LocalDate incidentDate;
    private LocalDate incidentEndDate;
    private LocalDate detectionDate;
    private Long departmentId;
    private Long branchId;
    private String processName;
    private String productService;
    private Long baselEventCategoryId;
    private String eventDescription;
    private String immediateActionTaken;
    private Long rootCauseCategoryId;
    private String rootCauseDescription;
    private Long controlId;
    private Boolean failedMissingControl;
    private Long currencyId;
    private BigDecimal grossLoss;
    private BigDecimal restitutionRemediationCost;
    private Long recoveryMethodId;
    private BigDecimal netLoss;
    private String accountingGlReference;
    private Long dataSourceId;
    private String nonFinancialImpactType;
    private String nonFinancialImpactDetails;
    private Severity overallEventSeverity;
    private String correctiveAction;
    private String actionOwner;
    private LocalDate actionTargetDate;
    private Long actionStatusId;
    private Boolean preventiveControlImplemented;
    private String validationEvidence;
    private LocalDate closureValidationDate;
    private String closureComment;
    private Integer recordVersion;
    private AuthorizationStatus authorizationStatus;
    private IncidentStatus status;
    private String eventOwner;
    private String reportedBy;
    private String createdByUsername;
    private String lastUpdatedByUsername;
    private Long submittedBy;
    private Instant submittedAt;
    private Long authorizedBy;
    private Instant authorizedAt;
    private Long lastModifiedBy;
    private Instant updatedAt;

    private OltsIncident(UUID id,
                         String incidentId,
                         Long inputterUserId,
                         Long createdBy,
                         Instant createdAt) {
        this.id = id;
        this.incidentId = incidentId;
        this.inputterUserId = inputterUserId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public static OltsIncident create(String incidentId,
                                      Long inputterUserId,
                                      String actorUsername,
                                      Long eventStatusId,
                                      LocalDate incidentDate,
                                      LocalDate incidentEndDate,
                                      LocalDate detectionDate,
                                      Long departmentId,
                                      Long branchId,
                                      String eventTitle,
                                      String processName,
                                      String productService,
                                      Long baselEventCategoryId,
                                      String eventDescription,
                                      String immediateActionTaken,
                                      Long rootCauseCategoryId,
                                      String rootCauseDescription,
                                      Long controlId,
                                      Boolean failedMissingControl,
                                      Long currencyId,
                                      BigDecimal grossLoss,
                                      BigDecimal restitutionRemediationCost,
                                      Long recoveryMethodId,
                                      String accountingGlReference,
                                      Long dataSourceId,
                                      String nonFinancialImpactType,
                                      String nonFinancialImpactDetails,
                                      Severity overallEventSeverity,
                                      String correctiveAction,
                                      String actionOwner,
                                      LocalDate actionTargetDate,
                                      Long actionStatusId,
                                      Boolean preventiveControlImplemented,
                                      String validationEvidence,
                                      LocalDate closureValidationDate,
                                      String closureComment,
                                      Instant now) {
        validate(incidentDate, incidentEndDate, detectionDate, grossLoss, restitutionRemediationCost, actionTargetDate, closureValidationDate);
        OltsIncident incident = new OltsIncident(UUID.randomUUID(), incidentId, inputterUserId, inputterUserId, now);
        incident.applyEditableFields(
                eventTitle,
                eventStatusId,
                incidentDate,
                incidentEndDate,
                detectionDate,
                departmentId,
                branchId,
                processName,
                productService,
                baselEventCategoryId,
                eventDescription,
                immediateActionTaken,
                rootCauseCategoryId,
                rootCauseDescription,
                controlId,
                failedMissingControl,
                currencyId,
                grossLoss,
                restitutionRemediationCost,
                recoveryMethodId,
                accountingGlReference,
                dataSourceId,
                nonFinancialImpactType,
                nonFinancialImpactDetails,
                overallEventSeverity,
                correctiveAction,
                actionOwner,
                actionTargetDate,
                actionStatusId,
                preventiveControlImplemented,
                validationEvidence,
                closureValidationDate,
                closureComment
        );
        incident.recordVersion = 1;
        incident.authorizationStatus = AuthorizationStatus.DRAFT;
        incident.status = IncidentStatus.DRAFT;
        incident.eventOwner = firstNonBlank(actionOwner, actorUsername);
        incident.reportedBy = actorUsername;
        incident.createdByUsername = actorUsername;
        incident.lastUpdatedByUsername = actorUsername;
        incident.lastModifiedBy = inputterUserId;
        incident.updatedAt = now;
        return incident;
    }

    public void updateDraft(Long actorUserId,
                            String actorUsername,
                            String eventTitle,
                            Long eventStatusId,
                            LocalDate incidentDate,
                            LocalDate incidentEndDate,
                            LocalDate detectionDate,
                            Long departmentId,
                            Long branchId,
                            String processName,
                            String productService,
                            Long baselEventCategoryId,
                            String eventDescription,
                            String immediateActionTaken,
                            Long rootCauseCategoryId,
                            String rootCauseDescription,
                            Long controlId,
                            Boolean failedMissingControl,
                            Long currencyId,
                            BigDecimal grossLoss,
                            BigDecimal restitutionRemediationCost,
                            Long recoveryMethodId,
                            String accountingGlReference,
                            Long dataSourceId,
                            String nonFinancialImpactType,
                            String nonFinancialImpactDetails,
                            Severity overallEventSeverity,
                            String correctiveAction,
                            String actionOwner,
                            LocalDate actionTargetDate,
                            Long actionStatusId,
                            Boolean preventiveControlImplemented,
                            String validationEvidence,
                            LocalDate closureValidationDate,
                            String closureComment,
                            Instant timestamp) {
        if (authorizationStatus != AuthorizationStatus.DRAFT && authorizationStatus != AuthorizationStatus.RETURNED_FOR_CORRECTION) {
            throw new AuthorizationException("Only draft or returned incidents may be updated", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        validate(incidentDate, incidentEndDate, detectionDate, grossLoss, restitutionRemediationCost, actionTargetDate, closureValidationDate);
        applyEditableFields(
                eventTitle,
                eventStatusId,
                incidentDate,
                incidentEndDate,
                detectionDate,
                departmentId,
                branchId,
                processName,
                productService,
                baselEventCategoryId,
                eventDescription,
                immediateActionTaken,
                rootCauseCategoryId,
                rootCauseDescription,
                controlId,
                failedMissingControl,
                currencyId,
                grossLoss,
                restitutionRemediationCost,
                recoveryMethodId,
                accountingGlReference,
                dataSourceId,
                nonFinancialImpactType,
                nonFinancialImpactDetails,
                overallEventSeverity,
                correctiveAction,
                actionOwner,
                actionTargetDate,
                actionStatusId,
                preventiveControlImplemented,
                validationEvidence,
                closureValidationDate,
                closureComment
        );
        this.recordVersion = this.recordVersion + 1;
        this.eventOwner = firstNonBlank(actionOwner, this.eventOwner, actorUsername);
        this.lastModifiedBy = actorUserId;
        this.lastUpdatedByUsername = actorUsername;
        this.updatedAt = timestamp;
        this.authorizationStatus = AuthorizationStatus.DRAFT;
        this.status = IncidentStatus.DRAFT;
    }

    public void submit(Long actorUserId, Instant timestamp) {
        if (authorizationStatus != AuthorizationStatus.DRAFT && authorizationStatus != AuthorizationStatus.RETURNED_FOR_CORRECTION) {
            throw new AuthorizationException("Only draft or returned incidents may be submitted", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        this.authorizationStatus = AuthorizationStatus.PENDING_AUTHORIZATION;
        this.submittedBy = actorUserId;
        this.submittedAt = timestamp;
        this.lastModifiedBy = actorUserId;
        this.updatedAt = timestamp;
    }

    public void beginAuthorizationReview(Long actorUserId, Instant timestamp) {
        if (authorizationStatus != AuthorizationStatus.PENDING_AUTHORIZATION) {
            throw new AuthorizationException("Incident is not pending authorization", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        this.authorizationStatus = AuthorizationStatus.UNDER_AUTHORIZATION_REVIEW;
        this.lastModifiedBy = actorUserId;
        this.updatedAt = timestamp;
    }

    public void authorize(Long authorizerUserId, Instant timestamp, SegregationOfDutiesPolicy policy) {
        if (authorizationStatus != AuthorizationStatus.UNDER_AUTHORIZATION_REVIEW) {
            throw new AuthorizationException("Incident is not under authorization review", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        policy.validate(inputterUserId, lastModifiedBy, authorizerUserId);
        this.authorizationStatus = AuthorizationStatus.AUTHORIZED;
        this.status = IncidentStatus.AUTHORIZED;
        this.authorizedBy = authorizerUserId;
        this.authorizedAt = timestamp;
        this.lastModifiedBy = authorizerUserId;
        this.updatedAt = timestamp;
    }

    public void reject(Long authorizerUserId, String reason, Instant timestamp, SegregationOfDutiesPolicy policy) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("rejection reason is required");
        }
        if (authorizationStatus != AuthorizationStatus.UNDER_AUTHORIZATION_REVIEW) {
            throw new AuthorizationException("Incident is not under authorization review", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        policy.validate(inputterUserId, lastModifiedBy, authorizerUserId);
        this.authorizationStatus = AuthorizationStatus.REJECTED;
        this.lastModifiedBy = authorizerUserId;
        this.updatedAt = timestamp;
    }

    public void returnForCorrection(Long authorizerUserId, String reason, Instant timestamp, SegregationOfDutiesPolicy policy) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("return reason is required");
        }
        if (authorizationStatus != AuthorizationStatus.UNDER_AUTHORIZATION_REVIEW) {
            throw new AuthorizationException("Incident is not under authorization review", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        policy.validate(inputterUserId, lastModifiedBy, authorizerUserId);
        this.authorizationStatus = AuthorizationStatus.RETURNED_FOR_CORRECTION;
        this.lastModifiedBy = authorizerUserId;
        this.updatedAt = timestamp;
    }

    public static OltsIncident rehydrate(UUID id,
                                         String incidentId,
                                         Long inputterUserId,
                                         Long createdBy,
                                         Instant createdAt,
                                         String eventTitle,
                                         Long eventStatusId,
                                         LocalDate incidentDate,
                                         LocalDate incidentEndDate,
                                         LocalDate detectionDate,
                                         Long departmentId,
                                         Long branchId,
                                         String processName,
                                         String productService,
                                         Long baselEventCategoryId,
                                         String eventDescription,
                                         String immediateActionTaken,
                                         Long rootCauseCategoryId,
                                         String rootCauseDescription,
                                         Long controlId,
                                         Boolean failedMissingControl,
                                         Long currencyId,
                                         BigDecimal grossLoss,
                                         BigDecimal restitutionRemediationCost,
                                         Long recoveryMethodId,
                                         BigDecimal netLoss,
                                         String accountingGlReference,
                                         Long dataSourceId,
                                         String nonFinancialImpactType,
                                         String nonFinancialImpactDetails,
                                         Severity overallEventSeverity,
                                         String correctiveAction,
                                         String actionOwner,
                                         LocalDate actionTargetDate,
                                         Long actionStatusId,
                                         Boolean preventiveControlImplemented,
                                         String validationEvidence,
                                         LocalDate closureValidationDate,
                                         String closureComment,
                                         Integer recordVersion,
                                         AuthorizationStatus authorizationStatus,
                                         IncidentStatus status,
                                         String eventOwner,
                                         String reportedBy,
                                         String createdByUsername,
                                         String lastUpdatedByUsername,
                                         Long submittedBy,
                                         Instant submittedAt,
                                         Long authorizedBy,
                                         Instant authorizedAt,
                                         Long lastModifiedBy,
                                         Instant updatedAt) {
        OltsIncident incident = new OltsIncident(id, incidentId, inputterUserId, createdBy, createdAt);
        incident.eventTitle = eventTitle;
        incident.eventStatusId = eventStatusId;
        incident.incidentDate = incidentDate;
        incident.incidentEndDate = incidentEndDate;
        incident.detectionDate = detectionDate;
        incident.departmentId = departmentId;
        incident.branchId = branchId;
        incident.processName = processName;
        incident.productService = productService;
        incident.baselEventCategoryId = baselEventCategoryId;
        incident.eventDescription = eventDescription;
        incident.immediateActionTaken = immediateActionTaken;
        incident.rootCauseCategoryId = rootCauseCategoryId;
        incident.rootCauseDescription = rootCauseDescription;
        incident.controlId = controlId;
        incident.failedMissingControl = failedMissingControl;
        incident.currencyId = currencyId;
        incident.grossLoss = grossLoss;
        incident.restitutionRemediationCost = restitutionRemediationCost;
        incident.recoveryMethodId = recoveryMethodId;
        incident.netLoss = netLoss;
        incident.accountingGlReference = accountingGlReference;
        incident.dataSourceId = dataSourceId;
        incident.nonFinancialImpactType = nonFinancialImpactType;
        incident.nonFinancialImpactDetails = nonFinancialImpactDetails;
        incident.overallEventSeverity = overallEventSeverity;
        incident.correctiveAction = correctiveAction;
        incident.actionOwner = actionOwner;
        incident.actionTargetDate = actionTargetDate;
        incident.actionStatusId = actionStatusId;
        incident.preventiveControlImplemented = preventiveControlImplemented;
        incident.validationEvidence = validationEvidence;
        incident.closureValidationDate = closureValidationDate;
        incident.closureComment = closureComment;
        incident.recordVersion = recordVersion;
        incident.authorizationStatus = authorizationStatus;
        incident.status = status;
        incident.eventOwner = eventOwner;
        incident.reportedBy = reportedBy;
        incident.createdByUsername = createdByUsername;
        incident.lastUpdatedByUsername = lastUpdatedByUsername;
        incident.submittedBy = submittedBy;
        incident.submittedAt = submittedAt;
        incident.authorizedBy = authorizedBy;
        incident.authorizedAt = authorizedAt;
        incident.lastModifiedBy = lastModifiedBy;
        incident.updatedAt = updatedAt;
        return incident;
    }

    public static BigDecimal calculateNetLoss(BigDecimal grossLoss, BigDecimal restitutionRemediationCost) {
        if (grossLoss == null || restitutionRemediationCost == null) {
            throw new IllegalArgumentException("financial amounts are required");
        }
        if (grossLoss.signum() < 0 || restitutionRemediationCost.signum() < 0) {
            throw new IllegalArgumentException("financial amounts cannot be negative");
        }
        return grossLoss.add(restitutionRemediationCost).setScale(2, RoundingMode.HALF_UP);
    }

    private void applyEditableFields(String eventTitle,
                                     Long eventStatusId,
                                     LocalDate incidentDate,
                                     LocalDate incidentEndDate,
                                     LocalDate detectionDate,
                                     Long departmentId,
                                     Long branchId,
                                     String processName,
                                     String productService,
                                     Long baselEventCategoryId,
                                     String eventDescription,
                                     String immediateActionTaken,
                                     Long rootCauseCategoryId,
                                     String rootCauseDescription,
                                     Long controlId,
                                     Boolean failedMissingControl,
                                     Long currencyId,
                                     BigDecimal grossLoss,
                                     BigDecimal restitutionRemediationCost,
                                     Long recoveryMethodId,
                                     String accountingGlReference,
                                     Long dataSourceId,
                                     String nonFinancialImpactType,
                                     String nonFinancialImpactDetails,
                                     Severity overallEventSeverity,
                                     String correctiveAction,
                                     String actionOwner,
                                     LocalDate actionTargetDate,
                                     Long actionStatusId,
                                     Boolean preventiveControlImplemented,
                                     String validationEvidence,
                                     LocalDate closureValidationDate,
                                     String closureComment) {
        this.eventTitle = requireText(eventTitle, "eventTitle");
        this.eventStatusId = eventStatusId;
        this.incidentDate = incidentDate;
        this.incidentEndDate = incidentEndDate;
        this.detectionDate = detectionDate;
        this.departmentId = departmentId;
        this.branchId = branchId;
        this.processName = requireText(processName, "processName");
        this.productService = requireText(productService, "productService");
        this.baselEventCategoryId = baselEventCategoryId;
        this.eventDescription = requireText(eventDescription, "eventDescription");
        this.immediateActionTaken = trimToNull(immediateActionTaken);
        this.rootCauseCategoryId = rootCauseCategoryId;
        this.rootCauseDescription = trimToNull(rootCauseDescription);
        this.controlId = controlId;
        this.failedMissingControl = failedMissingControl;
        this.currencyId = currencyId;
        this.grossLoss = grossLoss.setScale(2, RoundingMode.HALF_UP);
        this.restitutionRemediationCost = restitutionRemediationCost.setScale(2, RoundingMode.HALF_UP);
        this.recoveryMethodId = recoveryMethodId;
        this.netLoss = calculateNetLoss(grossLoss, restitutionRemediationCost);
        this.accountingGlReference = trimToNull(accountingGlReference);
        this.dataSourceId = dataSourceId;
        this.nonFinancialImpactType = trimToNull(nonFinancialImpactType);
        this.nonFinancialImpactDetails = trimToNull(nonFinancialImpactDetails);
        this.overallEventSeverity = overallEventSeverity;
        this.correctiveAction = trimToNull(correctiveAction);
        this.actionOwner = trimToNull(actionOwner);
        this.actionTargetDate = actionTargetDate;
        this.actionStatusId = actionStatusId;
        this.preventiveControlImplemented = preventiveControlImplemented;
        this.validationEvidence = trimToNull(validationEvidence);
        this.closureValidationDate = closureValidationDate;
        this.closureComment = trimToNull(closureComment);
    }

    private static void validate(LocalDate incidentDate,
                                 LocalDate incidentEndDate,
                                 LocalDate detectionDate,
                                 BigDecimal grossLoss,
                                 BigDecimal restitutionRemediationCost,
                                 LocalDate actionTargetDate,
                                 LocalDate closureValidationDate) {
        if (incidentDate == null || detectionDate == null) {
            throw new IllegalArgumentException("incidentDate and detectionDate are required");
        }
        if (incidentEndDate != null && incidentEndDate.isBefore(incidentDate)) {
            throw new IllegalArgumentException("incidentEndDate must be on or after incidentDate");
        }
        if (detectionDate.isBefore(incidentDate)) {
            throw new IllegalArgumentException("detectionDate must be on or after incidentDate");
        }
        if (closureValidationDate != null && actionTargetDate != null && closureValidationDate.isBefore(incidentDate)) {
            throw new IllegalArgumentException("closureValidationDate must be valid");
        }
        if (grossLoss == null || restitutionRemediationCost == null) {
            throw new IllegalArgumentException("financial amounts are required");
        }
        if (grossLoss.signum() < 0 || restitutionRemediationCost.signum() < 0) {
            throw new IllegalArgumentException("financial amounts cannot be negative");
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    public UUID getId() { return id; }
    public String getIncidentId() { return incidentId; }
    public String getEventId() { return incidentId; }
    public Long getInputterUserId() { return inputterUserId; }
    public Long getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public String getEventTitle() { return eventTitle; }
    public Long getEventStatusId() { return eventStatusId; }
    public LocalDate getIncidentDate() { return incidentDate; }
    public LocalDate getIncidentEndDate() { return incidentEndDate; }
    public LocalDate getDetectionDate() { return detectionDate; }
    public Long getDepartmentId() { return departmentId; }
    public Long getBranchId() { return branchId; }
    public String getProcessName() { return processName; }
    public String getProductService() { return productService; }
    public Long getBaselEventCategoryId() { return baselEventCategoryId; }
    public String getEventDescription() { return eventDescription; }
    public String getImmediateActionTaken() { return immediateActionTaken; }
    public Long getRootCauseCategoryId() { return rootCauseCategoryId; }
    public String getRootCauseDescription() { return rootCauseDescription; }
    public Long getControlId() { return controlId; }
    public Boolean getFailedMissingControl() { return failedMissingControl; }
    public Long getCurrencyId() { return currencyId; }
    public BigDecimal getGrossLoss() { return grossLoss; }
    public BigDecimal getRestitutionRemediationCost() { return restitutionRemediationCost; }
    public Long getRecoveryMethodId() { return recoveryMethodId; }
    public BigDecimal getNetLoss() { return netLoss; }
    public String getAccountingGlReference() { return accountingGlReference; }
    public Long getDataSourceId() { return dataSourceId; }
    public String getNonFinancialImpactType() { return nonFinancialImpactType; }
    public String getNonFinancialImpactDetails() { return nonFinancialImpactDetails; }
    public Severity getOverallEventSeverity() { return overallEventSeverity; }
    public String getCorrectiveAction() { return correctiveAction; }
    public String getActionOwner() { return actionOwner; }
    public LocalDate getActionTargetDate() { return actionTargetDate; }
    public Long getActionStatusId() { return actionStatusId; }
    public Boolean getPreventiveControlImplemented() { return preventiveControlImplemented; }
    public String getValidationEvidence() { return validationEvidence; }
    public LocalDate getClosureValidationDate() { return closureValidationDate; }
    public String getClosureComment() { return closureComment; }
    public Integer getRecordVersion() { return recordVersion; }
    public AuthorizationStatus getAuthorizationStatus() { return authorizationStatus; }
    public IncidentStatus getStatus() { return status; }
    public String getEventOwner() { return eventOwner; }
    public String getReportedBy() { return reportedBy; }
    public String getCreatedByUsername() { return createdByUsername; }
    public String getLastUpdatedByUsername() { return lastUpdatedByUsername; }
    public Long getSubmittedBy() { return submittedBy; }
    public Instant getSubmittedAt() { return submittedAt; }
    public Long getAuthorizedBy() { return authorizedBy; }
    public Instant getAuthorizedAt() { return authorizedAt; }
    public Long getLastModifiedBy() { return lastModifiedBy; }
    public Instant getUpdatedAt() { return updatedAt; }
}
