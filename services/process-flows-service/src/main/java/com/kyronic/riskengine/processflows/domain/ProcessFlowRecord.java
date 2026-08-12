package com.kyronic.riskengine.processflows.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;

@Entity
@Table(name = "process_flows")
public class ProcessFlowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String flowReference;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(nullable = false)
    private Long departmentId;

    @Column(nullable = false, length = 120)
    private String processOwner;

    @Column(nullable = false, length = 80)
    private String status;

    @Column(length = 4000)
    private String description;

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

    protected ProcessFlowRecord() {
    }

    public ProcessFlowRecord(Long id,
                             String flowReference,
                             String name,
                             Long departmentId,
                             String processOwner,
                             String status,
                             String description,
                             Instant createdAt,
                             String createdBy,
                             Instant updatedAt,
                             String updatedBy,
                             Long version) {
        this.id = id;
        this.flowReference = flowReference;
        this.name = name;
        this.departmentId = departmentId;
        this.processOwner = processOwner;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    public void update(String name,
                       Long departmentId,
                       String processOwner,
                       String status,
                       String description,
                       Instant updatedAt,
                       String updatedBy) {
        this.name = name;
        this.departmentId = departmentId;
        this.processOwner = processOwner;
        this.status = status;
        this.description = description;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public Long getId() { return id; }
    public String getFlowReference() { return flowReference; }
    public String getName() { return name; }
    public Long getDepartmentId() { return departmentId; }
    public String getProcessOwner() { return processOwner; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
}
