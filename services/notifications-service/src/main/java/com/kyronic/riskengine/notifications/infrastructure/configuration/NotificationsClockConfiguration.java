package com.kyronic.riskengine.notifications.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class NotificationsClockConfiguration {

    @Bean
    Clock notificationsClock() {
        return Clock.systemUTC();
    }
}
