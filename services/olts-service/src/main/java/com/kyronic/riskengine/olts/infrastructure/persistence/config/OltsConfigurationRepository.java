package com.kyronic.riskengine.olts.infrastructure.persistence.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface OltsConfigurationRepository<T extends AbstractOltsConfigurationEntity> extends JpaRepository<T, Long> {

    List<T> findAllByOrderByDisplayOrderAscCodeAsc();

    Optional<T> findByCodeIgnoreCase(String code);
}
