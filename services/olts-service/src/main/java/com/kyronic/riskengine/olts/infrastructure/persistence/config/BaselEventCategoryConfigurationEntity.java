package com.kyronic.riskengine.olts.infrastructure.persistence.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "olts_basel_event_category_config")
public class BaselEventCategoryConfigurationEntity extends AbstractOltsConfigurationEntity {
}
