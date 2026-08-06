package com.kyronic.riskengine.olts.infrastructure.persistence;

import com.kyronic.riskengine.common.authorization.AuthorizationStatus;
import com.kyronic.riskengine.olts.domain.model.IncidentStatus;
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
    private UUID departmentId;
    @Column(nullable = false)
    private UUID branchId;
    @Column(nullable = false)
    private UUID inputterUserId;
    @Column(nullable = false)
    private LocalDate incidentDate;
    @Column(nullable = false)
    private LocalDate discoveryDate;
    @Column(nullable = false, length = 50)
    private String lossCategory;
    @Column(nullable = false, length = 50)
    private String eventType;
    @Enumerated(EnumType.STRING)
    private Severity severity;
    @Column(nullable = false, length = 4000)
    private String description;
    @Column(nullable = false, length = 3)
    private String currencyCode;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal grossLoss;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal recoveries;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal netLoss;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal potentialLoss;
    @Column(nullable = false)
    private UUID responsiblePersonId;
    @Column(nullable = false)
    private String responsiblePersonName;
    @Enumerated(EnumType.STRING)
    private AuthorizationStatus authorizationStatus;
    @Enumerated(EnumType.STRING)
    private IncidentStatus status;
    @Column(nullable = false)
    private Integer recordVersion;
    @Column(nullable = false)
    private boolean activeVersion;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private UUID createdBy;
    private UUID submittedBy;
    private Instant submittedAt;
    private UUID authorizedBy;
    private Instant authorizedAt;
    @Column(nullable = false)
    private UUID lastModifiedBy;
    @Column(nullable = false)
    private Instant updatedAt;
    @Column(nullable = false)
    private boolean deleted;
    private UUID deletedBy;
    private Instant deletedAt;

    protected IncidentJpaEntity() {
    }

    public static IncidentJpaEntity fromDomain(com.kyronic.riskengine.olts.domain.model.OltsIncident incident) {
        IncidentJpaEntity entity = new IncidentJpaEntity();
        entity.id = incident.getId();
        entity.incidentId = incident.getIncidentId();
        entity.departmentId = incident.getDepartmentId();
        entity.branchId = incident.getBranchId();
        entity.inputterUserId = incident.getInputterUserId();
        entity.incidentDate = incident.getIncidentDate();
        entity.discoveryDate = incident.getDiscoveryDate();
        entity.lossCategory = incident.getLossCategory();
        entity.eventType = incident.getEventType();
        entity.severity = incident.getSeverity();
        entity.description = incident.getDescription();
        entity.currencyCode = incident.getCurrencyCode();
        entity.grossLoss = incident.getGrossLoss();
        entity.recoveries = incident.getRecoveries();
        entity.netLoss = incident.getNetLoss();
        entity.potentialLoss = incident.getPotentialLoss();
        entity.responsiblePersonId = incident.getResponsiblePersonId();
        entity.responsiblePersonName = incident.getResponsiblePersonName();
        entity.authorizationStatus = incident.getAuthorizationStatus();
        entity.status = incident.getStatus();
        entity.recordVersion = incident.getRecordVersion();
        entity.activeVersion = incident.isActiveVersion();
        entity.createdAt = incident.getCreatedAt();
        entity.createdBy = incident.getCreatedBy();
        entity.submittedBy = incident.getSubmittedBy();
        entity.submittedAt = incident.getSubmittedAt();
        entity.authorizedBy = incident.getAuthorizedBy();
        entity.authorizedAt = incident.getAuthorizedAt();
        entity.lastModifiedBy = incident.getLastModifiedBy();
        entity.updatedAt = incident.getUpdatedAt();
        entity.deleted = incident.isDeleted();
        entity.deletedBy = incident.getDeletedBy();
        entity.deletedAt = incident.getDeletedAt();
        return entity;
    }

    public com.kyronic.riskengine.olts.domain.model.OltsIncident toDomain() {
        return com.kyronic.riskengine.olts.domain.model.OltsIncident.rehydrate(
                id,
                incidentId,
                departmentId,
                branchId,
                inputterUserId,
                incidentDate,
                discoveryDate,
                lossCategory,
                eventType,
                severity,
                description,
                currencyCode,
                grossLoss,
                recoveries,
                potentialLoss,
                responsiblePersonId,
                responsiblePersonName,
                recordVersion,
                activeVersion,
                createdAt,
                createdBy,
                authorizationStatus,
                status,
                netLoss,
                submittedBy,
                submittedAt,
                authorizedBy,
                authorizedAt,
                lastModifiedBy,
                updatedAt,
                deleted,
                deletedBy,
                deletedAt
        );
    }
}
