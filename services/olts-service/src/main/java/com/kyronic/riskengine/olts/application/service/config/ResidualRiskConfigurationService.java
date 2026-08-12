package com.kyronic.riskengine.olts.application.service.config;

import com.kyronic.riskengine.olts.infrastructure.persistence.config.ResidualRiskConfigurationEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.ResidualRiskConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class ResidualRiskConfigurationService extends AbstractOltsConfigurationService<ResidualRiskConfigurationEntity> {

    public ResidualRiskConfigurationService(ResidualRiskConfigurationRepository repository, Clock clock) {
        super(repository, ResidualRiskConfigurationEntity::new, clock, "residual risk");
    }
}
