package com.kyronic.riskengine.auth.infrastructure.persistence;

import com.kyronic.riskengine.auth.domain.RoleDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface RoleDefinitionRepository extends JpaRepository<RoleDefinition, UUID> {

    List<RoleDefinition> findByCodeIn(Collection<String> codes);
}
