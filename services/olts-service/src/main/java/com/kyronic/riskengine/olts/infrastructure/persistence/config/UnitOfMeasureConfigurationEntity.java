package com.kyronic.riskengine.olts.infrastructure.persistence.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "olts_unit_of_measure_config")
public class UnitOfMeasureConfigurationEntity extends AbstractOltsConfigurationEntity {
}
