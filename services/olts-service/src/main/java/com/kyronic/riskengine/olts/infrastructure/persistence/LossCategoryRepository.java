package com.kyronic.riskengine.olts.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LossCategoryRepository extends JpaRepository<LossCategoryJpaEntity, UUID> {

    List<LossCategoryJpaEntity> findAllByOrderByCodeAsc();

    Optional<LossCategoryJpaEntity> findByCodeIgnoreCase(String code);
}
