package com.kyronic.riskengine.kri.application.service;

import com.kyronic.riskengine.kri.application.dto.KriRequest;
import com.kyronic.riskengine.kri.application.dto.KriResponse;
import com.kyronic.riskengine.kri.domain.KriRecord;
import com.kyronic.riskengine.kri.infrastructure.persistence.KriRepository;
import com.kyronic.riskengine.kri.interfaces.KriNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class KriService {

    private final KriRepository kriRepository;
    private final KriIdGenerator kriIdGenerator;
    private final CurrentUserProvider currentUserProvider;
    private final Clock clock;

    public KriService(KriRepository kriRepository,
                      KriIdGenerator kriIdGenerator,
                      CurrentUserProvider currentUserProvider,
                      Clock clock) {
        this.kriRepository = kriRepository;
        this.kriIdGenerator = kriIdGenerator;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    public KriResponse create(KriRequest request) {
        Instant now = Instant.now(clock);
        String actor = currentUserProvider.currentUsername();
        KriRecord record = new KriRecord(
                UUID.randomUUID(),
                kriIdGenerator.nextId(),
                request.indicatorName(),
                request.category(),
                request.owner(),
                request.businessUnit(),
                request.measurementFrequency(),
                request.description(),
                request.unitOfMeasure(),
                request.target(),
                request.direction(),
                request.greenUpperBound(),
                request.amberThreshold(),
                request.redThreshold(),
                request.currentValue(),
                request.dataSource(),
                request.nextReviewDate(),
                request.linkedRisk(),
                request.escalateTo(),
                request.escalationTrigger(),
                now,
                actor,
                now,
                actor,
                false,
                null
        );
        return toResponse(kriRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<KriResponse> list() {
        return kriRepository.findAllByDeletedFalse(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public KriResponse get(String kriId) {
        return toResponse(findActiveByKriId(kriId));
    }

    public KriResponse update(String kriId, KriRequest request) {
        KriRecord record = findActiveByKriId(kriId);
        record.update(
                request.indicatorName(),
                request.category(),
                request.owner(),
                request.businessUnit(),
                request.measurementFrequency(),
                request.description(),
                request.unitOfMeasure(),
                request.target(),
                request.direction(),
                request.greenUpperBound(),
                request.amberThreshold(),
                request.redThreshold(),
                request.currentValue(),
                request.dataSource(),
                request.nextReviewDate(),
                request.linkedRisk(),
                request.escalateTo(),
                request.escalationTrigger(),
                Instant.now(clock),
                currentUserProvider.currentUsername()
        );
        return toResponse(kriRepository.save(record));
    }

    public void delete(String kriId) {
        KriRecord record = findActiveByKriId(kriId);
        record.markDeleted(Instant.now(clock), currentUserProvider.currentUsername());
        kriRepository.save(record);
    }

    private KriRecord findActiveByKriId(String kriId) {
        return kriRepository.findByKriIdAndDeletedFalse(kriId)
                .orElseThrow(() -> new KriNotFoundException(kriId));
    }

    private KriResponse toResponse(KriRecord record) {
        return new KriResponse(
                record.getId(),
                record.getKriId(),
                record.getIndicatorName(),
                record.getCategory(),
                record.getOwner(),
                record.getBusinessUnit(),
                record.getMeasurementFrequency(),
                record.getDescription(),
                record.getUnitOfMeasure(),
                record.getTarget(),
                record.getDirection(),
                record.getGreenUpperBound(),
                record.getAmberThreshold(),
                record.getRedThreshold(),
                record.getCurrentValue(),
                record.getDataSource(),
                record.getNextReviewDate(),
                record.getLinkedRisk(),
                record.getEscalateTo(),
                record.getEscalationTrigger(),
                record.getCreatedAt(),
                record.getCreatedBy(),
                record.getUpdatedAt(),
                record.getUpdatedBy()
        );
    }
}
