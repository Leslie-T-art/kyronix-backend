package com.kyronic.riskengine.selfassessment.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class SelfAssessmentClockConfiguration {

    @Bean
    Clock selfAssessmentClock() {
        return Clock.systemUTC();
    }
}
