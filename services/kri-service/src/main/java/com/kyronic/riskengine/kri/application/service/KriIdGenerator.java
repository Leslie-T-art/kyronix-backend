package com.kyronic.riskengine.kri.application.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Component
public class KriIdGenerator {

    private final EntityManager entityManager;

    public KriIdGenerator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public String nextId() {
        Number sequenceValue = (Number) entityManager.createNativeQuery("select nextval('kri_service.kri_reference_seq')")
                .getSingleResult();
        return "KRI-" + Year.now().getValue() + "-" + String.format("%05d", sequenceValue.longValue());
    }
}
