package com.kyronic.riskengine.olts.application.service;

import com.kyronic.riskengine.common.events.EventEnvelope;

public interface EventPublisher {
    void publish(EventEnvelope eventEnvelope);
}
