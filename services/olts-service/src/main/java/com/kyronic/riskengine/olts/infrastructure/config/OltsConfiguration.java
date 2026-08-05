package com.kyronic.riskengine.olts.infrastructure.config;

import com.kyronic.riskengine.common.authorization.SegregationOfDutiesPolicy;
import com.kyronic.riskengine.common.authorization.ServerSideAuthorizerResolver;
import com.kyronic.riskengine.olts.application.service.AuthorizationDirectory;
import com.kyronic.riskengine.olts.application.service.EventPublisher;
import com.kyronic.riskengine.olts.application.service.IncidentIdGenerator;
import com.kyronic.riskengine.olts.application.service.OltsIncidentService;
import com.kyronic.riskengine.olts.application.service.OltsIncidentStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class OltsConfiguration {

    @Bean
    OltsIncidentService oltsIncidentService(IncidentIdGenerator incidentIdGenerator,
                                            OltsIncidentStore incidentStore,
                                            AuthorizationDirectory authorizationDirectory,
                                            EventPublisher eventPublisher) {
        return new OltsIncidentService(
                incidentIdGenerator,
                incidentStore,
                new ServerSideAuthorizerResolver(),
                new SegregationOfDutiesPolicy(),
                authorizationDirectory,
                eventPublisher,
                Clock.systemUTC()
        );
    }
}
