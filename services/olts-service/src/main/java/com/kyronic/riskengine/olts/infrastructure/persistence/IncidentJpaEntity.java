package com.kyronic.riskengine.olts.infrastructure.persistence;

import com.kyronic.riskengine.common.authorization.AuthorizationStatus;
import com.kyronic.riskengine.olts.domain.model.IncidentStatus;
import com.kyronic.riskengine.olts.domain.model.OltsIncident;
import com.kyronic.riskengine.olts.domain.model.Severity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "olts_incidents")
public class IncidentJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String incidentId;

    @Column(nullable = false)
    private Long inputterUserId;

    @Column(nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String eventTitle;

    @Column(nullable = false)
    private Long eventStatusId;

    @Column(nullable = false)
    private LocalDate incidentDate;

    private LocalDate incidentEndDate;

    @Column(name = "discovery_date", nullable = false)
    private LocalDate detectionDate;

    @Column(nullable = false)
    private Long departmentId;

    @Column(nullable = false)
    private Long branchId;

    @Column(nullable = false)
    private String processName;

    @Column(nullable = false)
    private String productService;

    @Column(nullable = false)
    private Long baselEventCategoryId;

    @Column(name = "description", nullable = false, length = 4000)
    private String eventDescription;

    @Column(length = 4000)
    private String immediateActionTaken;

    @Column(nullable = false)
    private Long rootCauseCategoryId;

    @Column(length = 4000)
    private String rootCauseDescription;

    private Long controlId;

    @Column(nullable = false)
    private Boolean failedMissingControl;

    @Column(nullable = false)
    private Long currencyId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal grossLoss;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal restitutionRemediationCost;

    private Long recoveryMethodId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal netLoss;

    private String accountingGlReference;

    private Long dataSourceId;

    private String nonFinancialImpactType;

    @Column(length = 4000)
    private String nonFinancialImpactDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity overallEventSeverity;

    @Column(length = 4000)
    private String correctiveAction;

    private String actionOwner;

    private LocalDate actionTargetDate;

    private Long actionStatusId;

    @Column(nullable = false)
    private Boolean preventiveControlImplemented;

    @Column(length = 4000)
    private String validationEvidence;

    private LocalDate closureValidationDate;

    @Column(length = 4000)
    private String closureComment;

    @Enumerated(EnumType.STRING)
    private AuthorizationStatus authorizationStatus;

    @Enumerated(EnumType.STRING)
    private IncidentStatus status;

    @Column(nullable = false)
    private Integer recordVersion;

    private String eventOwner;

    private String reportedBy;

    @Column(nullable = false)
    private String createdByUsername;

    @Column(nullable = false)
    private String lastUpdatedByUsername;

    private Long submittedBy;

    private Instant submittedAt;

    private Long authorizedBy;

    private Instant authorizedAt;

    @Column(nullable = false)
    private Long lastModifiedBy;

    @Column(nullable = false)
    private Instant updatedAt;

    protected IncidentJpaEntity() {
    }

    public static IncidentJpaEntity fromDomain(OltsIncident incident) {
        IncidentJpaEntity entity = new IncidentJpaEntity();
        entity.id = incident.getId();
        entity.incidentId = incident.getIncidentId();
        entity.inputterUserId = incident.getInputterUserId();
        entity.createdBy = incident.getCreatedBy();
        entity.createdAt = incident.getCreatedAt();
        entity.eventTitle = incident.getEventTitle();
        entity.eventStatusId = incident.getEventStatusId();
        entity.incidentDate = incident.getIncidentDate();
        entity.incidentEndDate = incident.getIncidentEndDate();
        entity.detectionDate = incident.getDetectionDate();
        entity.departmentId = incident.getDepartmentId();
        entity.branchId = incident.getBranchId();
        entity.processName = incident.getProcessName();
        entity.productService = incident.getProductService();
        entity.baselEventCategoryId = incident.getBaselEventCategoryId();
        entity.eventDescription = incident.getEventDescription();
        entity.immediateActionTaken = incident.getImmediateActionTaken();
        entity.rootCauseCategoryId = incident.getRootCauseCategoryId();
        entity.rootCauseDescription = incident.getRootCauseDescription();
        entity.controlId = incident.getControlId();
        entity.failedMissingControl = incident.getFailedMissingControl();
        entity.currencyId = incident.getCurrencyId();
        entity.grossLoss = incident.getGrossLoss();
        entity.restitutionRemediationCost = incident.getRestitutionRemediationCost();
        entity.recoveryMethodId = incident.getRecoveryMethodId();
        entity.netLoss = incident.getNetLoss();
        entity.accountingGlReference = incident.getAccountingGlReference();
        entity.dataSourceId = incident.getDataSourceId();
        entity.nonFinancialImpactType = incident.getNonFinancialImpactType();
        entity.nonFinancialImpactDetails = incident.getNonFinancialImpactDetails();
        entity.overallEventSeverity = incident.getOverallEventSeverity();
        entity.correctiveAction = incident.getCorrectiveAction();
        entity.actionOwner = incident.getActionOwner();
        entity.actionTargetDate = incident.getActionTargetDate();
        entity.actionStatusId = incident.getActionStatusId();
        entity.preventiveControlImplemented = incident.getPreventiveControlImplemented();
        entity.validationEvidence = incident.getValidationEvidence();
        entity.closureValidationDate = incident.getClosureValidationDate();
        entity.closureComment = incident.getClosureComment();
        entity.authorizationStatus = incident.getAuthorizationStatus();
        entity.status = incident.getStatus();
        entity.recordVersion = incident.getRecordVersion();
        entity.eventOwner = incident.getEventOwner();
        entity.reportedBy = incident.getReportedBy();
        entity.createdByUsername = incident.getCreatedByUsername();
        entity.lastUpdatedByUsername = incident.getLastUpdatedByUsername();
        entity.submittedBy = incident.getSubmittedBy();
        entity.submittedAt = incident.getSubmittedAt();
        entity.authorizedBy = incident.getAuthorizedBy();
        entity.authorizedAt = incident.getAuthorizedAt();
        entity.lastModifiedBy = incident.getLastModifiedBy();
        entity.updatedAt = incident.getUpdatedAt();
        return entity;
    }

    public OltsIncident toDomain() {
        return OltsIncident.rehydrate(
                id,
                incidentId,
                inputterUserId,
                createdBy,
                createdAt,
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
                netLoss,
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
                closureComment,
                recordVersion,
                authorizationStatus,
                status,
                eventOwner,
                reportedBy,
                createdByUsername,
                lastUpdatedByUsername,
                submittedBy,
                submittedAt,
                authorizedBy,
                authorizedAt,
                lastModifiedBy,
                updatedAt
        );
    }
}
