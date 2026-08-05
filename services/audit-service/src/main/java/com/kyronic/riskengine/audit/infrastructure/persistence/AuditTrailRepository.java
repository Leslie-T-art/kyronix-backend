package com.kyronic.riskengine.audit.infrastructure.persistence;

import com.kyronic.riskengine.audit.domain.AuditTrailEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuditTrailRepository extends JpaRepository<AuditTrailEntry, UUID> {
}
