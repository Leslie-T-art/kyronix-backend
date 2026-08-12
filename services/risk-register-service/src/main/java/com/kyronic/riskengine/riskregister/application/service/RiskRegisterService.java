package com.kyronic.riskengine.riskregister.application.service;

import com.kyronic.riskengine.riskregister.application.dto.RiskRecordRequest;
import com.kyronic.riskengine.riskregister.application.dto.RiskRecordResponse;
import com.kyronic.riskengine.riskregister.domain.RiskRecord;
import com.kyronic.riskengine.riskregister.infrastructure.persistence.RiskRecordRepository;
import com.kyronic.riskengine.riskregister.interfaces.RiskRecordNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class RiskRegisterService {

    private final RiskRecordRepository riskRecordRepository;
    private final RiskIdGenerator riskIdGenerator;
    private final CurrentUserProvider currentUserProvider;
    private final Clock clock;

    public RiskRegisterService(RiskRecordRepository riskRecordRepository,
                               RiskIdGenerator riskIdGenerator,
                               CurrentUserProvider currentUserProvider,
                               Clock clock) {
        this.riskRecordRepository = riskRecordRepository;
        this.riskIdGenerator = riskIdGenerator;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    public RiskRecordResponse create(RiskRecordRequest request) {
        Instant now = Instant.now(clock);
        String actor = currentUserProvider.currentUsername();
        RiskRecord riskRecord = new RiskRecord(
                null,
                riskIdGenerator.nextId(),
                request.riskTitle(),
                request.category(),
                request.owner(),
                request.businessUnit(),
                request.description(),
                request.likelihood(),
                request.impact(),
                request.inherentRating(),
                request.controlsMapped(),
                request.controlEffectiveness(),
                request.residualRating(),
                request.treatmentStrategy(),
                request.status(),
                request.nextReviewDate(),
                request.linkedProcess(),
                request.linkedKri(),
                request.actionPlan(),
                now,
                actor,
                now,
                actor,
                null
        );
        return toResponse(riskRecordRepository.save(riskRecord));
    }

    @Transactional(readOnly = true)
    public List<RiskRecordResponse> list() {
        return riskRecordRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RiskRecordResponse get(String riskId) {
        return toResponse(findActiveByRiskId(riskId));
    }

    public RiskRecordResponse update(String riskId, RiskRecordRequest request) {
        RiskRecord riskRecord = findActiveByRiskId(riskId);
        riskRecord.update(
                request.riskTitle(),
                request.category(),
                request.owner(),
                request.businessUnit(),
                request.description(),
                request.likelihood(),
                request.impact(),
                request.inherentRating(),
                request.controlsMapped(),
                request.controlEffectiveness(),
                request.residualRating(),
                request.treatmentStrategy(),
                request.status(),
                request.nextReviewDate(),
                request.linkedProcess(),
                request.linkedKri(),
                request.actionPlan(),
                Instant.now(clock),
                currentUserProvider.currentUsername()
        );
        return toResponse(riskRecordRepository.save(riskRecord));
    }

    public void delete(String riskId) {
        RiskRecord riskRecord = findActiveByRiskId(riskId);
        riskRecordRepository.delete(riskRecord);
    }

    private RiskRecord findActiveByRiskId(String riskId) {
        return riskRecordRepository.findByRiskId(riskId)
                .orElseThrow(() -> new RiskRecordNotFoundException(riskId));
    }

    private RiskRecordResponse toResponse(RiskRecord riskRecord) {
        return new RiskRecordResponse(
                riskRecord.getId(),
                riskRecord.getRiskId(),
                riskRecord.getRiskTitle(),
                riskRecord.getCategory(),
                riskRecord.getOwner(),
                riskRecord.getBusinessUnit(),
                riskRecord.getDescription(),
                riskRecord.getLikelihood(),
                riskRecord.getImpact(),
                riskRecord.getInherentRating(),
                riskRecord.getControlsMapped(),
                riskRecord.getControlEffectiveness(),
                riskRecord.getResidualRating(),
                riskRecord.getTreatmentStrategy(),
                riskRecord.getStatus(),
                riskRecord.getNextReviewDate(),
                riskRecord.getLinkedProcess(),
                riskRecord.getLinkedKri(),
                riskRecord.getActionPlan(),
                riskRecord.getCreatedAt(),
                riskRecord.getCreatedBy(),
                riskRecord.getUpdatedAt(),
                riskRecord.getUpdatedBy()
        );
    }
}
