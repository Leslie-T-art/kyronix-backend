package com.kyronic.riskengine.olts.infrastructure.persistence.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "olts_data_source_config")
public class DataSourceConfigurationEntity extends AbstractOltsConfigurationEntity {
}
