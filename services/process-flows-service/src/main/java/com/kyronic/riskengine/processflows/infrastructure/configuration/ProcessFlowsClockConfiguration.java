package com.kyronic.riskengine.processflows.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ProcessFlowsClockConfiguration {

    @Bean
    Clock processFlowsClock() {
        return Clock.systemUTC();
    }
}
