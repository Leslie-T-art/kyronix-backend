package com.kyronic.riskengine.olts.infrastructure.persistence;

import com.kyronic.riskengine.olts.application.service.IncidentIdGenerator;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class DatabaseIncidentIdGenerator implements IncidentIdGenerator {

    private final IncidentSequenceRepository incidentSequenceRepository;

    public DatabaseIncidentIdGenerator(IncidentSequenceRepository incidentSequenceRepository) {
        this.incidentSequenceRepository = incidentSequenceRepository;
    }

    @Override
    public String nextIncidentId() {
        long next = incidentSequenceRepository.nextValue();
        return "OLTS-%d-%05d".formatted(Year.now().getValue(), next);
    }
}
