package com.kyronic.riskengine.processflows.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "process_flows")
public class ProcessFlowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String flowReference;

    @Column(nullable = false, length = 180)
    private String processFlowName;

    @Column(nullable = false)
    private Long departmentId;

    @Column(nullable = false, length = 120)
    private String processOwner;

    @Column(length = 4000)
    private String description;

    @Column(nullable = false)
    private LocalDate validFromDate;

    @Column(nullable = false)
    private LocalDate validToDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ProcessFlowWorkflowStatus workflowStatus;

    @Column(nullable = false, length = 255)
    private String originalFileName;

    @Column(nullable = false, length = 255)
    private String contentType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, length = 120)
    private String bucketName;

    @Column(nullable = false, length = 500)
    private String objectKey;

    @Column(nullable = false)
    private Long inputterUserId;

    @Column(nullable = false, length = 120)
    private String inputterUsername;

    private Long authorizerUserId;

    @Column(length = 120)
    private String authorizerUsername;

    @Column(length = 1000)
    private String authorizerComment;

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
                             String processFlowName,
                             Long departmentId,
                             String processOwner,
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
                             String authorizerComment,
                             Instant createdAt,
                             String createdBy,
                             Instant updatedAt,
                             String updatedBy,
                             Long version) {
        this.id = id;
        this.flowReference = flowReference;
        this.processFlowName = processFlowName;
        this.departmentId = departmentId;
        this.processOwner = processOwner;
        this.description = description;
        this.validFromDate = validFromDate;
        this.validToDate = validToDate;
        this.workflowStatus = workflowStatus;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.inputterUserId = inputterUserId;
        this.inputterUsername = inputterUsername;
        this.authorizerUserId = authorizerUserId;
        this.authorizerUsername = authorizerUsername;
        this.authorizerComment = authorizerComment;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    public void updateDraft(String processFlowName,
                            Long departmentId,
                            String processOwner,
                            String description,
                            LocalDate validFromDate,
                            LocalDate validToDate,
                            String originalFileName,
                            String contentType,
                            Long fileSize,
                            String bucketName,
                            String objectKey,
                            Instant updatedAt,
                            String updatedBy) {
        this.processFlowName = processFlowName;
        this.departmentId = departmentId;
        this.processOwner = processOwner;
        this.description = description;
        this.validFromDate = validFromDate;
        this.validToDate = validToDate;
        if (originalFileName != null) {
            this.originalFileName = originalFileName;
            this.contentType = contentType;
            this.fileSize = fileSize;
            this.bucketName = bucketName;
            this.objectKey = objectKey;
        }
        this.workflowStatus = ProcessFlowWorkflowStatus.DRAFT;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public void submit(Instant updatedAt, String updatedBy) {
        this.workflowStatus = ProcessFlowWorkflowStatus.PENDING_APPROVAL;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public void approve(Long authorizerUserId, String authorizerUsername, String comment, Instant updatedAt) {
        this.workflowStatus = ProcessFlowWorkflowStatus.APPROVED;
        this.authorizerUserId = authorizerUserId;
        this.authorizerUsername = authorizerUsername;
        this.authorizerComment = comment;
        this.updatedAt = updatedAt;
        this.updatedBy = authorizerUsername;
    }

    public void reject(Long authorizerUserId, String authorizerUsername, String comment, Instant updatedAt) {
        this.workflowStatus = ProcessFlowWorkflowStatus.REJECTED;
        this.authorizerUserId = authorizerUserId;
        this.authorizerUsername = authorizerUsername;
        this.authorizerComment = comment;
        this.updatedAt = updatedAt;
        this.updatedBy = authorizerUsername;
    }

    public void returnForCorrection(Long authorizerUserId, String authorizerUsername, String comment, Instant updatedAt) {
        this.workflowStatus = ProcessFlowWorkflowStatus.RETURNED;
        this.authorizerUserId = authorizerUserId;
        this.authorizerUsername = authorizerUsername;
        this.authorizerComment = comment;
        this.updatedAt = updatedAt;
        this.updatedBy = authorizerUsername;
    }

    public Long getId() { return id; }
    public String getFlowReference() { return flowReference; }
    public String getProcessFlowName() { return processFlowName; }
    public Long getDepartmentId() { return departmentId; }
    public String getProcessOwner() { return processOwner; }
    public String getDescription() { return description; }
    public LocalDate getValidFromDate() { return validFromDate; }
    public LocalDate getValidToDate() { return validToDate; }
    public ProcessFlowWorkflowStatus getWorkflowStatus() { return workflowStatus; }
    public String getOriginalFileName() { return originalFileName; }
    public String getContentType() { return contentType; }
    public Long getFileSize() { return fileSize; }
    public String getBucketName() { return bucketName; }
    public String getObjectKey() { return objectKey; }
    public Long getInputterUserId() { return inputterUserId; }
    public String getInputterUsername() { return inputterUsername; }
    public Long getAuthorizerUserId() { return authorizerUserId; }
    public String getAuthorizerUsername() { return authorizerUsername; }
    public String getAuthorizerComment() { return authorizerComment; }
    public Instant getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
}
