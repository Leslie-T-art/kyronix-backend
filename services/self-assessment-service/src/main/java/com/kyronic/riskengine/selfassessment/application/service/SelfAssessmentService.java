package com.kyronic.riskengine.selfassessment.application.service;

import com.kyronic.riskengine.selfassessment.application.dto.SelfAssessmentRequest;
import com.kyronic.riskengine.selfassessment.application.dto.SelfAssessmentResponse;
import com.kyronic.riskengine.selfassessment.domain.SelfAssessmentRecord;
import com.kyronic.riskengine.selfassessment.infrastructure.persistence.SelfAssessmentRepository;
import com.kyronic.riskengine.selfassessment.interfaces.SelfAssessmentNotFoundException;
import com.kyronic.riskengine.selfassessment.interfaces.SelfAssessmentValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;

@Service
@Transactional
public class SelfAssessmentService {

    private static final int MAX_PAGE_SIZE = 100;

    private final SelfAssessmentRepository repository;
    private final RcsaIdGenerator rcsaIdGenerator;
    private final CurrentUserProvider currentUserProvider;
    private final Clock clock;

    public SelfAssessmentService(SelfAssessmentRepository repository,
                                 RcsaIdGenerator rcsaIdGenerator,
                                 CurrentUserProvider currentUserProvider,
                                 Clock clock) {
        this.repository = repository;
        this.rcsaIdGenerator = rcsaIdGenerator;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    public SelfAssessmentResponse create(SelfAssessmentRequest request) {
        validate(request);
        Instant now = Instant.now(clock);
        String actor = currentUserProvider.currentUsername();
        int inherentScore = request.inherentImpact() * request.inherentLikelihood();
        int residualScore = request.residualImpact() * request.residualLikelihood();
        SelfAssessmentRecord record = new SelfAssessmentRecord(
                null,
                rcsaIdGenerator.nextId(),
                request.assessmentPeriod(),
                request.departmentId(),
                request.processName(),
                request.riskRegisterRisk(),
                request.riskScenario(),
                request.cause(),
                request.consequenceImpact(),
                request.inherentImpact(),
                request.inherentLikelihood(),
                inherentScore,
                rating(inherentScore),
                request.controlDesignEffectiveness(),
                request.controlOperatingEffectiveness(),
                request.overallControlEffectiveness(),
                request.residualImpact(),
                request.residualLikelihood(),
                residualScore,
                rating(residualScore),
                request.riskResponse(),
                request.actionRequired(),
                request.linkedAction(),
                request.businessReviewStatus(),
                request.riskReviewVerification(),
                request.riskReviewComment(),
                request.dateOfLastReview(),
                request.nextReviewDate(),
                request.linkedControls(),
                request.linkedKris(),
                request.linkedOltsEvents(),
                request.linkedIssuesFindings(),
                now,
                actor,
                now,
                actor,
                null
        );
        return toResponse(repository.save(record));
    }

    @Transactional(readOnly = true)
    public Page<SelfAssessmentResponse> list(int page, int size, String sortBy, String sortDirection) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return repository.findAll(PageRequest.of(Math.max(page, 0), safeSize, Sort.by(direction, normalizeSortBy(sortBy))))
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public SelfAssessmentResponse get(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public long count(Long departmentId) {
        if (departmentId == null) {
            return repository.count();
        }
        return repository.countByDepartmentId(departmentId);
    }

    public SelfAssessmentResponse update(Long id, SelfAssessmentRequest request) {
        validate(request);
        SelfAssessmentRecord record = findById(id);
        int inherentScore = request.inherentImpact() * request.inherentLikelihood();
        int residualScore = request.residualImpact() * request.residualLikelihood();
        record.update(
                request.assessmentPeriod(),
                request.departmentId(),
                request.processName(),
                request.riskRegisterRisk(),
                request.riskScenario(),
                request.cause(),
                request.consequenceImpact(),
                request.inherentImpact(),
                request.inherentLikelihood(),
                inherentScore,
                rating(inherentScore),
                request.controlDesignEffectiveness(),
                request.controlOperatingEffectiveness(),
                request.overallControlEffectiveness(),
                request.residualImpact(),
                request.residualLikelihood(),
                residualScore,
                rating(residualScore),
                request.riskResponse(),
                request.actionRequired(),
                request.linkedAction(),
                request.businessReviewStatus(),
                request.riskReviewVerification(),
                request.riskReviewComment(),
                request.dateOfLastReview(),
                request.nextReviewDate(),
                request.linkedControls(),
                request.linkedKris(),
                request.linkedOltsEvents(),
                request.linkedIssuesFindings(),
                Instant.now(clock),
                currentUserProvider.currentUsername()
        );
        return toResponse(repository.save(record));
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private SelfAssessmentRecord findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new SelfAssessmentNotFoundException(id));
    }

    private void validate(SelfAssessmentRequest request) {
        if (request.actionRequired() && (request.linkedAction() == null || request.linkedAction().isBlank())) {
            throw new SelfAssessmentValidationException("linkedAction", "Linked action is required when actionRequired is true");
        }
        if (request.nextReviewDate() != null
                && request.dateOfLastReview() != null
                && request.nextReviewDate().isBefore(request.dateOfLastReview())) {
            throw new SelfAssessmentValidationException("nextReviewDate", "Next review date must be on or after the date of last review");
        }
        validateReferences("linkedControls", request.linkedControls());
        validateReferences("linkedKris", request.linkedKris());
        validateReferences("linkedOltsEvents", request.linkedOltsEvents());
        validateReferences("linkedIssuesFindings", request.linkedIssuesFindings());
    }

    private void validateReferences(String field, Set<String> values) {
        boolean invalid = values.stream().anyMatch(value -> value == null || value.isBlank());
        if (invalid) {
            throw new SelfAssessmentValidationException(field, "Linked reference values must not be blank");
        }
    }

    private String normalizeSortBy(String sortBy) {
        return switch (sortBy == null ? "" : sortBy) {
            case "assessmentPeriod" -> "assessmentPeriod";
            case "updatedAt" -> "updatedAt";
            case "rcsaId" -> "rcsaId";
            default -> "createdAt";
        };
    }

    private String rating(int score) {
        if (score >= 16) return "HIGH";
        if (score >= 9) return "MEDIUM";
        return "LOW";
    }

    private SelfAssessmentResponse toResponse(SelfAssessmentRecord record) {
        return new SelfAssessmentResponse(
                record.getId(),
                record.getRcsaId(),
                record.getAssessmentPeriod(),
                record.getDepartmentId(),
                record.getProcessName(),
                record.getRiskRegisterRisk(),
                record.getRiskScenario(),
                record.getCause(),
                record.getConsequenceImpact(),
                record.getInherentImpact(),
                record.getInherentLikelihood(),
                record.getInherentRiskScore(),
                record.getInherentRiskRating(),
                record.getLinkedControls(),
                record.getControlDesignEffectiveness(),
                record.getControlOperatingEffectiveness(),
                record.getOverallControlEffectiveness(),
                record.getResidualImpact(),
                record.getResidualLikelihood(),
                record.getResidualRiskScore(),
                record.getResidualRiskRating(),
                record.getRiskResponse(),
                record.isActionRequired(),
                record.getLinkedAction(),
                record.getLinkedKris(),
                record.getLinkedOltsEvents(),
                record.getLinkedIssuesFindings(),
                record.getBusinessReviewStatus(),
                record.getRiskReviewVerification(),
                record.getRiskReviewComment(),
                record.getDateOfLastReview(),
                record.getNextReviewDate(),
                record.getCreatedAt(),
                record.getCreatedBy(),
                record.getUpdatedAt(),
                record.getUpdatedBy()
        );
    }
}
