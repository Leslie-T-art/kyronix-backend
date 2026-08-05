package com.kyronic.riskengine.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "department_head_assignments")
public class DepartmentHeadAssignment {

    @Id
    private UUID assignmentId;
    private UUID departmentId;
    private UUID userId;
    private Instant effectiveFrom;
    private Instant effectiveTo;
    private boolean active;
    private boolean delegated;
    private UUID delegatedBy;
    @Column(length = 500)
    private String delegationReason;
    private Instant createdAt;
    private UUID createdBy;

    protected DepartmentHeadAssignment() {
    }

    public DepartmentHeadAssignment(UUID assignmentId, UUID departmentId, UUID userId, Instant effectiveFrom, Instant effectiveTo,
                                    boolean active, boolean delegated, UUID delegatedBy, String delegationReason,
                                    Instant createdAt, UUID createdBy) {
        this.assignmentId = assignmentId;
        this.departmentId = departmentId;
        this.userId = userId;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.active = active;
        this.delegated = delegated;
        this.delegatedBy = delegatedBy;
        this.delegationReason = delegationReason;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }
}
