package com.kyronic.riskengine.olts.application.service.config;

import com.kyronic.riskengine.olts.infrastructure.persistence.config.UnitOfMeasureConfigurationEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.UnitOfMeasureConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class UnitOfMeasureConfigurationService extends AbstractOltsConfigurationService<UnitOfMeasureConfigurationEntity> {

    public UnitOfMeasureConfigurationService(UnitOfMeasureConfigurationRepository repository, Clock clock) {
        super(repository, UnitOfMeasureConfigurationEntity::new, clock, "unit of measure");
    }
}
