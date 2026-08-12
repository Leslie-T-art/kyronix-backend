package com.kyronic.riskengine.processflows.infrastructure.persistence;

import com.kyronic.riskengine.processflows.domain.ProcessFlowRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessFlowRepository extends JpaRepository<ProcessFlowRecord, Long> {

    long countByDepartmentId(Long departmentId);

    long countByStatusIgnoreCase(String status);

    long countByDepartmentIdAndStatusIgnoreCase(Long departmentId, String status);
}
