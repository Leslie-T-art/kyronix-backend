package com.kyronic.riskengine.kri.infrastructure.persistence;

import com.kyronic.riskengine.kri.domain.KriRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KriRepository extends JpaRepository<KriRecord, UUID> {

    Optional<KriRecord> findByKriIdAndDeletedFalse(String kriId);

    List<KriRecord> findAllByDeletedFalse(Sort sort);
}
