package com.kyronic.riskengine.kri.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class KriClockConfiguration {

    @Bean
    Clock kriClock() {
        return Clock.systemUTC();
    }
}
