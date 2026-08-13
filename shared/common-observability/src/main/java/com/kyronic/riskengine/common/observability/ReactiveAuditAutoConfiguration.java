package com.kyronic.riskengine.common.observability;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.WebFilter;

import java.time.Clock;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass({RestClient.class, WebFilter.class})
@EnableConfigurationProperties(AuditPublisherProperties.class)
@ConditionalOnProperty(prefix = "kyronic.audit", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ReactiveAuditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    Clock platformAuditClock() {
        return Clock.systemUTC();
    }

    @Bean
    @ConditionalOnMissingBean(name = "platformAuditRestClient")
    RestClient platformAuditRestClient(RestClient.Builder builder, AuditPublisherProperties properties) {
        return builder.baseUrl(properties.getServiceUrl()).build();
    }

    @Bean
    PlatformAuditPublisher platformAuditPublisher(RestClient platformAuditRestClient) {
        return new PlatformAuditPublisher(platformAuditRestClient);
    }

    @Bean
    ReactiveAuditTrailFilter reactiveAuditTrailFilter(PlatformAuditPublisher auditPublisher,
                                                      Clock platformAuditClock,
                                                      Environment environment) {
        return new ReactiveAuditTrailFilter(
                auditPublisher,
                platformAuditClock,
                environment.getProperty("spring.application.name", "unknown-service")
        );
    }
}
