package com.kyronic.riskengine.kri.infrastructure.persistence;

import com.kyronic.riskengine.kri.domain.KriRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KriRepository extends JpaRepository<KriRecord, Long> {

    Optional<KriRecord> findByKriId(String kriId);

    List<KriRecord> findAll(Sort sort);
}
