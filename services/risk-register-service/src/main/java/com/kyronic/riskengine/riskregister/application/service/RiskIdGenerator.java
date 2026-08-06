package com.kyronic.riskengine.riskregister.application.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Component
public class RiskIdGenerator {

    private final EntityManager entityManager;

    public RiskIdGenerator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public String nextId() {
        Number sequenceValue = (Number) entityManager.createNativeQuery(
                        "select nextval('risk_register_service.risk_reference_seq')")
                .getSingleResult();
        return "RISK-" + Year.now().getValue() + "-" + String.format("%05d", sequenceValue.longValue());
    }
}
