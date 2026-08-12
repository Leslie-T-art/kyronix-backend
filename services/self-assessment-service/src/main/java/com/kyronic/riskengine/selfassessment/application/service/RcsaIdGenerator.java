package com.kyronic.riskengine.selfassessment.application.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class RcsaIdGenerator {

    private final EntityManager entityManager;

    public RcsaIdGenerator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public String nextId() {
        Number next = (Number) entityManager.createNativeQuery("select nextval('self_assessment_service.rcsa_reference_seq')").getSingleResult();
        return "RCSA-" + Year.now().getValue() + "-" + String.format("%06d", next.longValue());
    }
}
