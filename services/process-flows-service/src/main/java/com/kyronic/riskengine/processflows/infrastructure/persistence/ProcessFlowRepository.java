package com.kyronic.riskengine.processflows.infrastructure.persistence;

import com.kyronic.riskengine.processflows.domain.ProcessFlowRecord;
import com.kyronic.riskengine.processflows.domain.ProcessFlowWorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessFlowRepository extends JpaRepository<ProcessFlowRecord, Long> {

    long countByDepartmentId(Long departmentId);

    long countByWorkflowStatus(ProcessFlowWorkflowStatus workflowStatus);

    long countByDepartmentIdAndWorkflowStatus(Long departmentId, ProcessFlowWorkflowStatus workflowStatus);
}
