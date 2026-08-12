package com.kyronic.riskengine.olts.infrastructure.persistence.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "olts_event_status_config")
public class EventStatusConfigurationEntity extends AbstractOltsConfigurationEntity {
}
