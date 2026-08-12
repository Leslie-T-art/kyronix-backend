package com.kyronic.riskengine.olts.infrastructure.persistence.config;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.time.Instant;

@MappedSuperclass
public abstract class AbstractOltsConfigurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column
    private Integer displayOrder;

    @Column(nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Long updatedBy;

    @Column(nullable = false)
    private Instant updatedAt;

    public void initialize(String code,
                           String name,
                           String description,
                           Integer displayOrder,
                           Long actorUserId,
                           Instant timestamp) {
        this.code = normalize(code);
        this.name = normalize(name);
        this.description = normalizeNullable(description);
        this.displayOrder = displayOrder;
        this.createdBy = actorUserId;
        this.createdAt = timestamp;
        this.updatedBy = actorUserId;
        this.updatedAt = timestamp;
    }

    public void update(String code,
                       String name,
                       String description,
                       Integer displayOrder,
                       Long actorUserId,
                       Instant timestamp) {
        this.code = normalize(code);
        this.name = normalize(name);
        this.description = normalizeNullable(description);
        this.displayOrder = displayOrder;
        this.updatedBy = actorUserId;
        this.updatedAt = timestamp;
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

    public String getDescription() {
        return description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
