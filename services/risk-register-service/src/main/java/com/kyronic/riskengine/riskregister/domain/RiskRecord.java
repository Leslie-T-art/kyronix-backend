package com.kyronic.riskengine.riskregister.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "risk_records")
public class RiskRecord {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String riskId;

    @Column(nullable = false, length = 180)
    private String riskTitle;

    @Column(nullable = false, length = 120)
    private String category;

    @Column(nullable = false, length = 150)
    private String owner;

    @Column(nullable = false, length = 150)
    private String businessUnit;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(nullable = false)
    private Integer likelihood;

    @Column(nullable = false)
    private Integer impact;

    @Column(nullable = false, length = 40)
    private String inherentRating;

    @Column(nullable = false, length = 2000)
    private String controlsMapped;

    @Column(nullable = false, length = 80)
    private String controlEffectiveness;

    @Column(nullable = false, length = 40)
    private String residualRating;

    @Column(nullable = false, length = 80)
    private String treatmentStrategy;

    @Column(nullable = false, length = 80)
    private String status;

    @Column(nullable = false)
    private LocalDate nextReviewDate;

    @Column(nullable = false, length = 180)
    private String linkedProcess;

    @Column(nullable = false, length = 120)
    private String linkedKri;

    @Column(nullable = false, length = 4000)
    private String actionPlan;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false, length = 120)
    private String createdBy;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false, length = 120)
    private String updatedBy;

    @Column(nullable = false)
    private boolean deleted;

    @Version
    private Long version;

    protected RiskRecord() {
    }

    public RiskRecord(UUID id,
                      String riskId,
                      String riskTitle,
                      String category,
                      String owner,
                      String businessUnit,
                      String description,
                      Integer likelihood,
                      Integer impact,
                      String inherentRating,
                      String controlsMapped,
                      String controlEffectiveness,
                      String residualRating,
                      String treatmentStrategy,
                      String status,
                      LocalDate nextReviewDate,
                      String linkedProcess,
                      String linkedKri,
                      String actionPlan,
                      Instant createdAt,
                      String createdBy,
                      Instant updatedAt,
                      String updatedBy,
                      boolean deleted,
                      Long version) {
        this.id = id;
        this.riskId = riskId;
        this.riskTitle = riskTitle;
        this.category = category;
        this.owner = owner;
        this.businessUnit = businessUnit;
        this.description = description;
        this.likelihood = likelihood;
        this.impact = impact;
        this.inherentRating = inherentRating;
        this.controlsMapped = controlsMapped;
        this.controlEffectiveness = controlEffectiveness;
        this.residualRating = residualRating;
        this.treatmentStrategy = treatmentStrategy;
        this.status = status;
        this.nextReviewDate = nextReviewDate;
        this.linkedProcess = linkedProcess;
        this.linkedKri = linkedKri;
        this.actionPlan = actionPlan;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.deleted = deleted;
        this.version = version;
    }

    public void update(String riskTitle,
                       String category,
                       String owner,
                       String businessUnit,
                       String description,
                       Integer likelihood,
                       Integer impact,
                       String inherentRating,
                       String controlsMapped,
                       String controlEffectiveness,
                       String residualRating,
                       String treatmentStrategy,
                       String status,
                       LocalDate nextReviewDate,
                       String linkedProcess,
                       String linkedKri,
                       String actionPlan,
                       Instant updatedAt,
                       String updatedBy) {
        this.riskTitle = riskTitle;
        this.category = category;
        this.owner = owner;
        this.businessUnit = businessUnit;
        this.description = description;
        this.likelihood = likelihood;
        this.impact = impact;
        this.inherentRating = inherentRating;
        this.controlsMapped = controlsMapped;
        this.controlEffectiveness = controlEffectiveness;
        this.residualRating = residualRating;
        this.treatmentStrategy = treatmentStrategy;
        this.status = status;
        this.nextReviewDate = nextReviewDate;
        this.linkedProcess = linkedProcess;
        this.linkedKri = linkedKri;
        this.actionPlan = actionPlan;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public void markDeleted(Instant updatedAt, String updatedBy) {
        this.deleted = true;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public UUID getId() {
        return id;
    }

    public String getRiskId() {
        return riskId;
    }

    public String getRiskTitle() {
        return riskTitle;
    }

    public String getCategory() {
        return category;
    }

    public String getOwner() {
        return owner;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public String getDescription() {
        return description;
    }

    public Integer getLikelihood() {
        return likelihood;
    }

    public Integer getImpact() {
        return impact;
    }

    public String getInherentRating() {
        return inherentRating;
    }

    public String getControlsMapped() {
        return controlsMapped;
    }

    public String getControlEffectiveness() {
        return controlEffectiveness;
    }

    public String getResidualRating() {
        return residualRating;
    }

    public String getTreatmentStrategy() {
        return treatmentStrategy;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getNextReviewDate() {
        return nextReviewDate;
    }

    public String getLinkedProcess() {
        return linkedProcess;
    }

    public String getLinkedKri() {
        return linkedKri;
    }

    public String getActionPlan() {
        return actionPlan;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
