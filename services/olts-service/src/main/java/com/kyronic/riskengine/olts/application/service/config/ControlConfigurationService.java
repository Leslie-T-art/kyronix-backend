package com.kyronic.riskengine.olts.application.service.config;

import com.kyronic.riskengine.olts.infrastructure.persistence.config.ControlConfigurationEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.ControlConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class ControlConfigurationService extends AbstractOltsConfigurationService<ControlConfigurationEntity> {

    public ControlConfigurationService(ControlConfigurationRepository repository, Clock clock) {
        super(repository, ControlConfigurationEntity::new, clock, "control");
    }
}
