package com.kyronic.riskengine.olts.application.service.config;

import com.kyronic.riskengine.olts.infrastructure.persistence.config.BaselEventCategoryConfigurationEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.BaselEventCategoryConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class BaselEventCategoryConfigurationService extends AbstractOltsConfigurationService<BaselEventCategoryConfigurationEntity> {

    public BaselEventCategoryConfigurationService(BaselEventCategoryConfigurationRepository repository, Clock clock) {
        super(repository, BaselEventCategoryConfigurationEntity::new, clock, "Basel event category");
    }
}
