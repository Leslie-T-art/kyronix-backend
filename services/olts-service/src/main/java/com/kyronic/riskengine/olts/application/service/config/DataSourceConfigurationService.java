package com.kyronic.riskengine.olts.application.service.config;

import com.kyronic.riskengine.olts.infrastructure.persistence.config.DataSourceConfigurationEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.config.DataSourceConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class DataSourceConfigurationService extends AbstractOltsConfigurationService<DataSourceConfigurationEntity> {

    public DataSourceConfigurationService(DataSourceConfigurationRepository repository, Clock clock) {
        super(repository, DataSourceConfigurationEntity::new, clock, "data source");
    }
}
