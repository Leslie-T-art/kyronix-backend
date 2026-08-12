package com.kyronic.riskengine.olts.application.service;

import com.kyronic.riskengine.olts.domain.model.OltsIncident;

import java.util.List;
import java.util.Optional;

public interface OltsIncidentStore {

    OltsIncident save(OltsIncident incident);

    Optional<OltsIncident> findByIncidentId(String incidentId);

    List<OltsIncident> findAll();

    void delete(OltsIncident incident);
}
