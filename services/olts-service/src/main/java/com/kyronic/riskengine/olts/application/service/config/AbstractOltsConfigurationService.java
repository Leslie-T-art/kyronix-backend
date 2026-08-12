package com.kyronic.riskengine.olts.application.service.config;

import com.kyronic.riskengine.olts.application.dto.OltsConfigurationRequest;
import com.kyronic.riskengine.olts.application.dto.OltsConfigurationResponse;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.AbstractOltsConfigurationEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.OltsConfigurationRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

@Transactional
public abstract class AbstractOltsConfigurationService<T extends AbstractOltsConfigurationEntity> {

    private final OltsConfigurationRepository<T> repository;
    private final Supplier<T> entityFactory;
    private final Clock clock;
    private final String entityLabel;

    protected AbstractOltsConfigurationService(OltsConfigurationRepository<T> repository,
                                               Supplier<T> entityFactory,
                                               Clock clock,
                                               String entityLabel) {
        this.repository = repository;
        this.entityFactory = entityFactory;
        this.clock = clock;
        this.entityLabel = entityLabel;
    }

    public OltsConfigurationResponse create(OltsConfigurationRequest request, Long actorUserId) {
        T entity = entityFactory.get();
        entity.initialize(request.code(), request.name(), request.description(), request.displayOrder(), actorUserId, Instant.now(clock));
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<OltsConfigurationResponse> list() {
        return repository.findAllByOrderByDisplayOrderAscCodeAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OltsConfigurationResponse get(Long id) {
        return toResponse(require(id));
    }

    public OltsConfigurationResponse update(Long id, OltsConfigurationRequest request, Long actorUserId) {
        T entity = require(id);
        entity.update(request.code(), request.name(), request.description(), request.displayOrder(), actorUserId, Instant.now(clock));
        return toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        T entity = require(id);
        repository.delete(entity);
    }

    protected T require(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(entityLabel + " not found"));
    }

    private OltsConfigurationResponse toResponse(T entity) {
        return new OltsConfigurationResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getDisplayOrder(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt()
        );
    }
}
