package com.kyronic.riskengine.kri.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "kri_records")
public class KriRecord {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String kriId;

    @Column(nullable = false, length = 150)
    private String indicatorName;

    @Column(nullable = false, length = 120)
    private String category;

    @Column(nullable = false, length = 150)
    private String owner;

    @Column(nullable = false, length = 150)
    private String businessUnit;

    @Column(nullable = false, length = 80)
    private String measurementFrequency;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 80)
    private String unitOfMeasure;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal target;

    @Column(nullable = false, length = 30)
    private String direction;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal greenUpperBound;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amberThreshold;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal redThreshold;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentValue;

    @Column(nullable = false, length = 255)
    private String dataSource;

    @Column(nullable = false)
    private LocalDate nextReviewDate;

    @Column(nullable = false, length = 120)
    private String linkedRisk;

    @Column(nullable = false, length = 150)
    private String escalateTo;

    @Column(nullable = false, length = 500)
    private String escalationTrigger;

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

    protected KriRecord() {
    }

    public KriRecord(UUID id,
                     String kriId,
                     String indicatorName,
                     String category,
                     String owner,
                     String businessUnit,
                     String measurementFrequency,
                     String description,
                     String unitOfMeasure,
                     BigDecimal target,
                     String direction,
                     BigDecimal greenUpperBound,
                     BigDecimal amberThreshold,
                     BigDecimal redThreshold,
                     BigDecimal currentValue,
                     String dataSource,
                     LocalDate nextReviewDate,
                     String linkedRisk,
                     String escalateTo,
                     String escalationTrigger,
                     Instant createdAt,
                     String createdBy,
                     Instant updatedAt,
                     String updatedBy,
                     boolean deleted,
                     Long version) {
        this.id = id;
        this.kriId = kriId;
        this.indicatorName = indicatorName;
        this.category = category;
        this.owner = owner;
        this.businessUnit = businessUnit;
        this.measurementFrequency = measurementFrequency;
        this.description = description;
        this.unitOfMeasure = unitOfMeasure;
        this.target = target;
        this.direction = direction;
        this.greenUpperBound = greenUpperBound;
        this.amberThreshold = amberThreshold;
        this.redThreshold = redThreshold;
        this.currentValue = currentValue;
        this.dataSource = dataSource;
        this.nextReviewDate = nextReviewDate;
        this.linkedRisk = linkedRisk;
        this.escalateTo = escalateTo;
        this.escalationTrigger = escalationTrigger;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.deleted = deleted;
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public String getKriId() {
        return kriId;
    }

    public String getIndicatorName() {
        return indicatorName;
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

    public String getMeasurementFrequency() {
        return measurementFrequency;
    }

    public String getDescription() {
        return description;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public BigDecimal getTarget() {
        return target;
    }

    public String getDirection() {
        return direction;
    }

    public BigDecimal getGreenUpperBound() {
        return greenUpperBound;
    }

    public BigDecimal getAmberThreshold() {
        return amberThreshold;
    }

    public BigDecimal getRedThreshold() {
        return redThreshold;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public String getDataSource() {
        return dataSource;
    }

    public LocalDate getNextReviewDate() {
        return nextReviewDate;
    }

    public String getLinkedRisk() {
        return linkedRisk;
    }

    public String getEscalateTo() {
        return escalateTo;
    }

    public String getEscalationTrigger() {
        return escalationTrigger;
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

    public void update(String indicatorName,
                       String category,
                       String owner,
                       String businessUnit,
                       String measurementFrequency,
                       String description,
                       String unitOfMeasure,
                       BigDecimal target,
                       String direction,
                       BigDecimal greenUpperBound,
                       BigDecimal amberThreshold,
                       BigDecimal redThreshold,
                       BigDecimal currentValue,
                       String dataSource,
                       LocalDate nextReviewDate,
                       String linkedRisk,
                       String escalateTo,
                       String escalationTrigger,
                       Instant updatedAt,
                       String updatedBy) {
        this.indicatorName = indicatorName;
        this.category = category;
        this.owner = owner;
        this.businessUnit = businessUnit;
        this.measurementFrequency = measurementFrequency;
        this.description = description;
        this.unitOfMeasure = unitOfMeasure;
        this.target = target;
        this.direction = direction;
        this.greenUpperBound = greenUpperBound;
        this.amberThreshold = amberThreshold;
        this.redThreshold = redThreshold;
        this.currentValue = currentValue;
        this.dataSource = dataSource;
        this.nextReviewDate = nextReviewDate;
        this.linkedRisk = linkedRisk;
        this.escalateTo = escalateTo;
        this.escalationTrigger = escalationTrigger;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public void markDeleted(Instant updatedAt, String updatedBy) {
        this.deleted = true;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }
}
