package com.kyronic.riskengine.olts.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidentJpaRepository extends JpaRepository<IncidentJpaEntity, UUID> {
    Optional<IncidentJpaEntity> findByIncidentId(String incidentId);

    List<IncidentJpaEntity> findAllByOrderByCreatedAtDesc();
}
