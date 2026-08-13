package com.kyronic.riskengine.kri.infrastructure.persistence;

import com.kyronic.riskengine.kri.domain.TreatmentStrategy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentStrategyRepository extends JpaRepository<TreatmentStrategy, Long> {
}
