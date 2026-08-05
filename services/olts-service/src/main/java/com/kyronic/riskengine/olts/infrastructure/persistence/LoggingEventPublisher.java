package com.kyronic.riskengine.olts.infrastructure.persistence;

import com.kyronic.riskengine.common.events.EventEnvelope;
import com.kyronic.riskengine.olts.application.service.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingEventPublisher.class);

    @Override
    public void publish(EventEnvelope eventEnvelope) {
        log.info("kyronic_event type={} aggregate={} businessReference={} correlationId={}",
                eventEnvelope.eventType(),
                eventEnvelope.aggregateType(),
                eventEnvelope.businessReference(),
                eventEnvelope.correlationId());
    }
}
