package com.kyronic.riskengine.olts.application.service.config;

import com.kyronic.riskengine.olts.infrastructure.persistence.config.ValidationResultConfigurationEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.ValidationResultConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class ValidationResultConfigurationService extends AbstractOltsConfigurationService<ValidationResultConfigurationEntity> {

    public ValidationResultConfigurationService(ValidationResultConfigurationRepository repository, Clock clock) {
        super(repository, ValidationResultConfigurationEntity::new, clock, "validation result");
    }
}
