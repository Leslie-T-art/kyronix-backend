package com.kyronic.riskengine.processflows.application.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class ProcessFlowReferenceGenerator {

    private final EntityManager entityManager;

    public ProcessFlowReferenceGenerator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public String nextReference() {
        Number next = (Number) entityManager.createNativeQuery("select nextval('process_flows_service.process_flow_reference_seq')").getSingleResult();
        return "PF-" + Year.now().getValue() + "-" + String.format("%06d", next.longValue());
    }
}
