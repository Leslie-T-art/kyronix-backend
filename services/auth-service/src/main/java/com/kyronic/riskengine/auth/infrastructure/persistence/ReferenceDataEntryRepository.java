package com.kyronic.riskengine.auth.infrastructure.persistence;

import com.kyronic.riskengine.auth.domain.ReferenceDataEntry;
import com.kyronic.riskengine.auth.domain.ReferenceDataType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenceDataEntryRepository extends JpaRepository<ReferenceDataEntry, Long> {

    List<ReferenceDataEntry> findByTypeOrderByCodeAsc(ReferenceDataType type);
}
