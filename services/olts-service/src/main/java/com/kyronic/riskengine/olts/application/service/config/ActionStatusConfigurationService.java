package com.kyronic.riskengine.olts.application.service.config;

import com.kyronic.riskengine.olts.infrastructure.persistence.config.ActionStatusConfigurationEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.ActionStatusConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class ActionStatusConfigurationService extends AbstractOltsConfigurationService<ActionStatusConfigurationEntity> {

    public ActionStatusConfigurationService(ActionStatusConfigurationRepository repository, Clock clock) {
        super(repository, ActionStatusConfigurationEntity::new, clock, "action status");
    }
}
