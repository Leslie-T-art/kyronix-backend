package com.kyronic.riskengine.olts.application.service.config;

import com.kyronic.riskengine.olts.infrastructure.persistence.config.RecoveryMethodConfigurationEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.RecoveryMethodConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class RecoveryMethodConfigurationService extends AbstractOltsConfigurationService<RecoveryMethodConfigurationEntity> {

    public RecoveryMethodConfigurationService(RecoveryMethodConfigurationRepository repository, Clock clock) {
        super(repository, RecoveryMethodConfigurationEntity::new, clock, "recovery method");
    }
}
