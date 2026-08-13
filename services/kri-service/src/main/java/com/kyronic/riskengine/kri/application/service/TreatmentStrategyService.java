package com.kyronic.riskengine.kri.application.service;

import com.kyronic.riskengine.kri.application.dto.TreatmentStrategyRequest;
import com.kyronic.riskengine.kri.application.dto.TreatmentStrategyResponse;
import com.kyronic.riskengine.kri.domain.TreatmentStrategy;
import com.kyronic.riskengine.kri.infrastructure.persistence.TreatmentStrategyRepository;
import com.kyronic.riskengine.kri.interfaces.TreatmentStrategyNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class TreatmentStrategyService {

    private final TreatmentStrategyRepository repository;
    private final CurrentUserProvider currentUserProvider;
    private final Clock clock;

    public TreatmentStrategyService(TreatmentStrategyRepository repository,
                                    CurrentUserProvider currentUserProvider,
                                    Clock clock) {
        this.repository = repository;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    public TreatmentStrategyResponse create(TreatmentStrategyRequest request) {
        Instant now = Instant.now(clock);
        String actor = currentUserProvider.currentUsername();
        TreatmentStrategy strategy = new TreatmentStrategy(
                null,
                request.code(),
                request.name(),
                request.status(),
                now,
                actor,
                now,
                actor,
                null
        );
        return toResponse(repository.save(strategy));
    }

    @Transactional(readOnly = true)
    public List<TreatmentStrategyResponse> list() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TreatmentStrategyResponse get(Long id) {
        return toResponse(require(id));
    }

    public TreatmentStrategyResponse update(Long id, TreatmentStrategyRequest request) {
        TreatmentStrategy strategy = require(id);
        strategy.update(
                request.code(),
                request.name(),
                request.status(),
                Instant.now(clock),
                currentUserProvider.currentUsername()
        );
        return toResponse(repository.save(strategy));
    }

    public void delete(Long id) {
        repository.delete(require(id));
    }

    private TreatmentStrategy require(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TreatmentStrategyNotFoundException(id));
    }

    private TreatmentStrategyResponse toResponse(TreatmentStrategy strategy) {
        return new TreatmentStrategyResponse(
                strategy.getId(),
                strategy.getCode(),
                strategy.getName(),
                strategy.getStatus(),
                strategy.getCreatedAt(),
                strategy.getCreatedBy(),
                strategy.getUpdatedAt(),
                strategy.getUpdatedBy()
        );
    }
}
