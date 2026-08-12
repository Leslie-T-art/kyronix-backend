package com.kyronic.riskengine.olts.infrastructure.persistence.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "olts_validation_result_config")
public class ValidationResultConfigurationEntity extends AbstractOltsConfigurationEntity {
}
