package com.kyronic.riskengine.auth.infrastructure.persistence;

import com.kyronic.riskengine.auth.domain.RoleDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleDefinitionRepository extends JpaRepository<RoleDefinition, UUID> {
}
