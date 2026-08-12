package com.kyronic.riskengine.selfassessment.infrastructure.persistence;

import com.kyronic.riskengine.selfassessment.domain.SelfAssessmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelfAssessmentRepository extends JpaRepository<SelfAssessmentRecord, Long> {

    long countByDepartmentId(Long departmentId);
}
