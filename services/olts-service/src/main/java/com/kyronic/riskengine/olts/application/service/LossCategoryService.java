package com.kyronic.riskengine.olts.application.service;

import com.kyronic.riskengine.olts.application.dto.LossCategoryRequest;
import com.kyronic.riskengine.olts.application.dto.LossCategoryResponse;
import com.kyronic.riskengine.olts.infrastructure.persistence.LossCategoryJpaEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.LossCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LossCategoryService implements LossCategoryCatalog {

    private final LossCategoryRepository repository;

    public LossCategoryService(LossCategoryRepository repository) {
        this.repository = repository;
    }

    public LossCategoryResponse create(LossCategoryRequest request) {
        LossCategoryJpaEntity entity = new LossCategoryJpaEntity(
                UUID.randomUUID(),
                request.code().trim(),
                request.name().trim(),
                request.description().trim()
        );
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<LossCategoryResponse> list() {
        return repository.findAllByOrderByCodeAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LossCategoryResponse get(UUID id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("loss category not found")));
    }

    public LossCategoryResponse update(UUID id, LossCategoryRequest request) {
        LossCategoryJpaEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("loss category not found"));
        entity.update(request.code().trim(), request.name().trim(), request.description().trim());
        return toResponse(repository.save(entity));
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("loss category not found");
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public String requireValidCode(String code) {
        String normalized = normalize(code);
        repository.findByCodeIgnoreCase(normalized)
                .orElseThrow(() -> new IllegalArgumentException("loss category not found"));
        return normalized;
    }

    private LossCategoryResponse toResponse(LossCategoryJpaEntity entity) {
        return new LossCategoryResponse(entity.getId(), entity.getCode(), entity.getName(), entity.getDescription());
    }

    private String normalize(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("loss category is required");
        }
        return code.trim().toUpperCase();
    }
}
