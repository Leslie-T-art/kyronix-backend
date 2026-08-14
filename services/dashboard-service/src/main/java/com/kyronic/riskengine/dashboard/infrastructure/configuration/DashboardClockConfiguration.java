package com.kyronic.riskengine.dashboard.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class DashboardClockConfiguration {

    @Bean
    Clock dashboardClock() {
        return Clock.systemUTC();
    }
}
