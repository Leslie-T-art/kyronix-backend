package com.kyronic.riskengine.olts.application.service.config;

import com.kyronic.riskengine.olts.infrastructure.persistence.config.CurrencyConfigurationEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.CurrencyConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class CurrencyConfigurationService extends AbstractOltsConfigurationService<CurrencyConfigurationEntity> {

    public CurrencyConfigurationService(CurrencyConfigurationRepository repository, Clock clock) {
        super(repository, CurrencyConfigurationEntity::new, clock, "currency");
    }
}
