package com.kyronic.riskengine.processflows.application.service;

import com.kyronic.riskengine.processflows.application.dto.ProcessFlowRequest;
import com.kyronic.riskengine.processflows.application.dto.ProcessFlowResponse;
import com.kyronic.riskengine.processflows.domain.ProcessFlowRecord;
import com.kyronic.riskengine.processflows.domain.ProcessFlowWorkflowStatus;
import com.kyronic.riskengine.processflows.infrastructure.persistence.ProcessFlowRepository;
import com.kyronic.riskengine.processflows.infrastructure.storage.MinioProcessFlowDocumentStorage;
import com.kyronic.riskengine.processflows.infrastructure.storage.StoredDocument;
import com.kyronic.riskengine.processflows.interfaces.ProcessFlowConflictException;
import com.kyronic.riskengine.processflows.interfaces.ProcessFlowNotFoundException;
import com.kyronic.riskengine.processflows.interfaces.ProcessFlowValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class ProcessFlowService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ProcessFlowRepository repository;
    private final ProcessFlowReferenceGenerator referenceGenerator;
    private final CurrentUserProvider currentUserProvider;
    private final MinioProcessFlowDocumentStorage documentStorage;
    private final Clock clock;

    public ProcessFlowService(ProcessFlowRepository repository,
                              ProcessFlowReferenceGenerator referenceGenerator,
                              CurrentUserProvider currentUserProvider,
                              MinioProcessFlowDocumentStorage documentStorage,
                              Clock clock) {
        this.repository = repository;
        this.referenceGenerator = referenceGenerator;
        this.currentUserProvider = currentUserProvider;
        this.documentStorage = documentStorage;
        this.clock = clock;
    }

    public ProcessFlowResponse create(ProcessFlowRequest request) {
        validate(request, true);
        Instant now = Instant.now(clock);
        Long actorUserId = currentUserProvider.currentUserId();
        String actor = currentUserProvider.currentUsername();
        String flowReference = referenceGenerator.nextReference();
        StoredDocument storedDocument = documentStorage.store(request.getDepartmentId(), flowReference, request.getDocument());
        ProcessFlowRecord record = new ProcessFlowRecord(
                null,
                flowReference,
                request.getProcessFlowName().trim(),
                request.getDepartmentId(),
                trimToNull(request.getDescription()),
                request.getValidFromDate(),
                request.getValidToDate(),
                ProcessFlowWorkflowStatus.DRAFT,
                storedDocument.originalFileName(),
                storedDocument.contentType(),
                storedDocument.fileSize(),
                storedDocument.bucketName(),
                storedDocument.objectKey(),
                actorUserId,
                actor,
                null,
                null,
                null,
                now,
                actor,
                now,
                actor,
                null
        );
        return toResponse(repository.save(record));
    }

    @Transactional(readOnly = true)
    public Page<ProcessFlowResponse> list(int page, int size, String sortBy, String sortDirection) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return repository.findAll(PageRequest.of(Math.max(page, 0), safeSize, Sort.by(direction, normalizeSortBy(sortBy))))
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProcessFlowResponse get(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public byte[] downloadDocument(Long id) {
        ProcessFlowRecord record = findById(id);
        return documentStorage.read(record.getBucketName(), record.getObjectKey());
    }

    @Transactional(readOnly = true)
    public long count(Long departmentId, String workflowStatus) {
        ProcessFlowWorkflowStatus status = parseStatus(workflowStatus);
        if (departmentId != null && status != null) {
            return repository.countByDepartmentIdAndWorkflowStatus(departmentId, status);
        }
        if (departmentId != null) {
            return repository.countByDepartmentId(departmentId);
        }
        if (status != null) {
            return repository.countByWorkflowStatus(status);
        }
        return repository.count();
    }

    public ProcessFlowResponse update(Long id, ProcessFlowRequest request) {
        validate(request, false);
        ProcessFlowRecord record = findById(id);
        if (record.getWorkflowStatus() != ProcessFlowWorkflowStatus.DRAFT
                && record.getWorkflowStatus() != ProcessFlowWorkflowStatus.RETURNED) {
            throw new ProcessFlowConflictException("Only draft or returned process flows can be updated");
        }
        StoredDocument replacement = null;
        MultipartFile file = request.getDocument();
        if (file != null && !file.isEmpty()) {
            replacement = documentStorage.store(request.getDepartmentId(), record.getFlowReference(), file);
            documentStorage.delete(record.getBucketName(), record.getObjectKey());
        }
        record.updateDraft(
                request.getProcessFlowName().trim(),
                request.getDepartmentId(),
                trimToNull(request.getDescription()),
                request.getValidFromDate(),
                request.getValidToDate(),
                replacement == null ? null : replacement.originalFileName(),
                replacement == null ? null : replacement.contentType(),
                replacement == null ? null : replacement.fileSize(),
                replacement == null ? null : replacement.bucketName(),
                replacement == null ? null : replacement.objectKey(),
                Instant.now(clock),
                currentUserProvider.currentUsername()
        );
        return toResponse(repository.save(record));
    }

    public void delete(Long id) {
        ProcessFlowRecord record = findById(id);
        documentStorage.delete(record.getBucketName(), record.getObjectKey());
        repository.delete(record);
    }

    public ProcessFlowResponse submit(Long id) {
        ProcessFlowRecord record = findById(id);
        if (record.getWorkflowStatus() != ProcessFlowWorkflowStatus.DRAFT
                && record.getWorkflowStatus() != ProcessFlowWorkflowStatus.RETURNED) {
            throw new ProcessFlowConflictException("Only draft or returned process flows can be submitted");
        }
        record.submit(Instant.now(clock), currentUserProvider.currentUsername());
        return toResponse(repository.save(record));
    }

    public ProcessFlowResponse approve(Long id, String comment) {
        ProcessFlowRecord record = findById(id);
        validateReviewer(record);
        if (record.getWorkflowStatus() != ProcessFlowWorkflowStatus.PENDING_APPROVAL) {
            throw new ProcessFlowConflictException("Only pending approval process flows can be approved");
        }
        record.approve(currentUserProvider.currentUserId(), currentUserProvider.currentUsername(), trimToNull(comment), Instant.now(clock));
        return toResponse(repository.save(record));
    }

    public ProcessFlowResponse reject(Long id, String comment) {
        if (comment == null || comment.isBlank()) {
            throw new ProcessFlowValidationException("comment", "Comment is required when rejecting a process flow");
        }
        ProcessFlowRecord record = findById(id);
        validateReviewer(record);
        if (record.getWorkflowStatus() != ProcessFlowWorkflowStatus.PENDING_APPROVAL) {
            throw new ProcessFlowConflictException("Only pending approval process flows can be rejected");
        }
        record.reject(currentUserProvider.currentUserId(), currentUserProvider.currentUsername(), comment.trim(), Instant.now(clock));
        return toResponse(repository.save(record));
    }

    public ProcessFlowResponse returnForCorrection(Long id, String comment) {
        if (comment == null || comment.isBlank()) {
            throw new ProcessFlowValidationException("comment", "Comment is required when returning a process flow");
        }
        ProcessFlowRecord record = findById(id);
        validateReviewer(record);
        if (record.getWorkflowStatus() != ProcessFlowWorkflowStatus.PENDING_APPROVAL) {
            throw new ProcessFlowConflictException("Only pending approval process flows can be returned");
        }
        record.returnForCorrection(currentUserProvider.currentUserId(), currentUserProvider.currentUsername(), comment.trim(), Instant.now(clock));
        return toResponse(repository.save(record));
    }

    private void validate(ProcessFlowRequest request, boolean requireDocument) {
        if (request.getDepartmentId() == null || request.getDepartmentId() <= 0) {
            throw new ProcessFlowValidationException("departmentId", "Department id must be positive");
        }
        if (request.getValidToDate() != null && request.getValidToDate().isBefore(request.getValidFromDate())) {
            throw new ProcessFlowValidationException("validToDate", "Valid to date must be on or after valid from date");
        }
        MultipartFile document = request.getDocument();
        if (requireDocument && (document == null || document.isEmpty())) {
            throw new ProcessFlowValidationException("document", "Document upload is required");
        }
    }

    private ProcessFlowRecord findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProcessFlowNotFoundException(id));
    }

    private void validateReviewer(ProcessFlowRecord record) {
        Long actorUserId = currentUserProvider.currentUserId();
        if (actorUserId != null && actorUserId.equals(record.getInputterUserId())) {
            throw new ProcessFlowConflictException("The inputter cannot authorize the same process flow");
        }
        if (!currentUserProvider.hasAnyRole("DEPARTMENT_HEAD", "AUTHORIZER", "SYSTEM_ADMIN")) {
            throw new ProcessFlowConflictException("Only an authorizer, department head, or system administrator can review process flows");
        }
    }

    private String normalizeSortBy(String sortBy) {
        return switch (sortBy == null ? "" : sortBy) {
            case "processFlowName" -> "processFlowName";
            case "validToDate" -> "validToDate";
            case "workflowStatus" -> "workflowStatus";
            case "updatedAt" -> "updatedAt";
            default -> "createdAt";
        };
    }

    private ProcessFlowWorkflowStatus parseStatus(String workflowStatus) {
        if (workflowStatus == null || workflowStatus.isBlank()) {
            return null;
        }
        try {
            return ProcessFlowWorkflowStatus.valueOf(workflowStatus.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ProcessFlowValidationException("workflowStatus", "Unsupported workflow status");
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private ProcessFlowResponse toResponse(ProcessFlowRecord record) {
        return new ProcessFlowResponse(
                record.getId(),
                record.getFlowReference(),
                record.getProcessFlowName(),
                record.getDepartmentId(),
                record.getDescription(),
                record.getValidFromDate(),
                record.getValidToDate(),
                record.getWorkflowStatus(),
                record.getOriginalFileName(),
                record.getContentType(),
                record.getFileSize(),
                record.getBucketName(),
                record.getObjectKey(),
                record.getInputterUserId(),
                record.getInputterUsername(),
                record.getAuthorizerUserId(),
                record.getAuthorizerUsername(),
                record.getCreatedAt(),
                record.getCreatedBy(),
                record.getUpdatedAt(),
                record.getUpdatedBy()
        );
    }
}
