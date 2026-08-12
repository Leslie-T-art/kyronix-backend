package com.kyronic.riskengine.olts.application.service.config;

import com.kyronic.riskengine.olts.infrastructure.persistence.config.RootCauseConfigurationEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.RootCauseConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class RootCauseConfigurationService extends AbstractOltsConfigurationService<RootCauseConfigurationEntity> {

    public RootCauseConfigurationService(RootCauseConfigurationRepository repository, Clock clock) {
        super(repository, RootCauseConfigurationEntity::new, clock, "root cause");
    }
}
