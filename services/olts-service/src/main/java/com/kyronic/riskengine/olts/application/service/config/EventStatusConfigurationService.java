package com.kyronic.riskengine.olts.application.service.config;

import com.kyronic.riskengine.olts.infrastructure.persistence.config.EventStatusConfigurationEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.EventStatusConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class EventStatusConfigurationService extends AbstractOltsConfigurationService<EventStatusConfigurationEntity> {

    public EventStatusConfigurationService(EventStatusConfigurationRepository repository, Clock clock) {
        super(repository, EventStatusConfigurationEntity::new, clock, "event status");
    }
}
