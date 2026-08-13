package com.kyronic.riskengine.olts.application.service.config;

import com.kyronic.riskengine.olts.infrastructure.persistence.config.KriCategoryConfigurationEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.KriCategoryConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class KriCategoryConfigurationService extends AbstractOltsConfigurationService<KriCategoryConfigurationEntity> {

    public KriCategoryConfigurationService(KriCategoryConfigurationRepository repository, Clock clock) {
        super(repository, KriCategoryConfigurationEntity::new, clock, "KRI category");
    }
}
