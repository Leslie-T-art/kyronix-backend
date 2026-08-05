package com.kyronic.riskengine.olts.infrastructure.persistence;

import com.kyronic.riskengine.olts.application.service.OltsIncidentStore;
import com.kyronic.riskengine.olts.domain.model.OltsIncident;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaOltsIncidentStore implements OltsIncidentStore {

    private final IncidentJpaRepository repository;

    public JpaOltsIncidentStore(IncidentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public OltsIncident save(OltsIncident incident) {
        return repository.save(IncidentJpaEntity.fromDomain(incident)).toDomain();
    }

    @Override
    public Optional<OltsIncident> findByIncidentId(String incidentId) {
        return repository.findByIncidentIdAndDeletedFalse(incidentId).map(IncidentJpaEntity::toDomain);
    }

    @Override
    public List<OltsIncident> findAllActive() {
        return repository.findAllByDeletedFalseOrderByCreatedAtDesc().stream()
                .map(IncidentJpaEntity::toDomain)
                .toList();
    }
}
