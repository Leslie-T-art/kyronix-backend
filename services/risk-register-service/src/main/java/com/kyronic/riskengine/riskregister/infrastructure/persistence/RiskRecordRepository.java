package com.kyronic.riskengine.riskregister.infrastructure.persistence;

import com.kyronic.riskengine.riskregister.domain.RiskRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RiskRecordRepository extends JpaRepository<RiskRecord, UUID> {

    Optional<RiskRecord> findByRiskIdAndDeletedFalse(String riskId);

    List<RiskRecord> findAllByDeletedFalse(Sort sort);
}
