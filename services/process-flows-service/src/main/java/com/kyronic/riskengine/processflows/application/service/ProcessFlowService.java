package com.kyronic.riskengine.processflows.application.service;

import com.kyronic.riskengine.processflows.application.dto.ProcessFlowRequest;
import com.kyronic.riskengine.processflows.application.dto.ProcessFlowResponse;
import com.kyronic.riskengine.processflows.domain.ProcessFlowRecord;
import com.kyronic.riskengine.processflows.infrastructure.persistence.ProcessFlowRepository;
import com.kyronic.riskengine.processflows.interfaces.ProcessFlowNotFoundException;
import com.kyronic.riskengine.processflows.interfaces.ProcessFlowValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class ProcessFlowService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ProcessFlowRepository repository;
    private final ProcessFlowReferenceGenerator referenceGenerator;
    private final CurrentUserProvider currentUserProvider;
    private final Clock clock;

    public ProcessFlowService(ProcessFlowRepository repository,
                              ProcessFlowReferenceGenerator referenceGenerator,
                              CurrentUserProvider currentUserProvider,
                              Clock clock) {
        this.repository = repository;
        this.referenceGenerator = referenceGenerator;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    public ProcessFlowResponse create(ProcessFlowRequest request) {
        validate(request);
        Instant now = Instant.now(clock);
        String actor = currentUserProvider.currentUsername();
        ProcessFlowRecord record = new ProcessFlowRecord(
                null,
                referenceGenerator.nextReference(),
                request.name(),
                request.departmentId(),
                request.processOwner(),
                request.status(),
                request.description(),
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
    public long count(Long departmentId, String status) {
        if (departmentId != null && status != null && !status.isBlank()) {
            return repository.countByDepartmentIdAndStatusIgnoreCase(departmentId, status);
        }
        if (departmentId != null) {
            return repository.countByDepartmentId(departmentId);
        }
        if (status != null && !status.isBlank()) {
            return repository.countByStatusIgnoreCase(status);
        }
        return repository.count();
    }

    public ProcessFlowResponse update(Long id, ProcessFlowRequest request) {
        validate(request);
        ProcessFlowRecord record = findById(id);
        record.update(
                request.name(),
                request.departmentId(),
                request.processOwner(),
                request.status(),
                request.description(),
                Instant.now(clock),
                currentUserProvider.currentUsername()
        );
        return toResponse(repository.save(record));
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void validate(ProcessFlowRequest request) {
        if (request.departmentId() <= 0) {
            throw new ProcessFlowValidationException("departmentId", "Department id must be positive");
        }
    }

    private ProcessFlowRecord findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProcessFlowNotFoundException(id));
    }

    private String normalizeSortBy(String sortBy) {
        return switch (sortBy == null ? "" : sortBy) {
            case "name" -> "name";
            case "status" -> "status";
            case "updatedAt" -> "updatedAt";
            default -> "createdAt";
        };
    }

    private ProcessFlowResponse toResponse(ProcessFlowRecord record) {
        return new ProcessFlowResponse(
                record.getId(),
                record.getFlowReference(),
                record.getName(),
                record.getDepartmentId(),
                record.getProcessOwner(),
                record.getStatus(),
                record.getDescription(),
                record.getCreatedAt(),
                record.getCreatedBy(),
                record.getUpdatedAt(),
                record.getUpdatedBy()
        );
    }
}
