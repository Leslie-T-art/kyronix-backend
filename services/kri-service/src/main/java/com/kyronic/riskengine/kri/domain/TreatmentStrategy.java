package com.kyronic.riskengine.kri.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;

@Entity
@Table(name = "treatment_strategies")
public class TreatmentStrategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false, length = 120)
    private String createdBy;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false, length = 120)
    private String updatedBy;

    @Version
    private Long version;

    protected TreatmentStrategy() {
    }

    public TreatmentStrategy(Long id,
                             String code,
                             String name,
                             String status,
                             Instant createdAt,
                             String createdBy,
                             Instant updatedAt,
                             String updatedBy,
                             Long version) {
        this.id = id;
        this.code = normalize(code);
        this.name = normalize(name);
        this.status = normalize(status);
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    public void update(String code, String name, String status, Instant updatedAt, String updatedBy) {
        this.code = normalize(code);
        this.name = normalize(name);
        this.status = normalize(status);
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
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

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
