package com.kyronic.riskengine.auth.infrastructure.persistence;

import com.kyronic.riskengine.auth.domain.ReferenceDataEntry;
import com.kyronic.riskengine.auth.domain.ReferenceDataType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReferenceDataEntryRepository extends JpaRepository<ReferenceDataEntry, UUID> {

    List<ReferenceDataEntry> findByTypeOrderByCodeAsc(ReferenceDataType type);
}
