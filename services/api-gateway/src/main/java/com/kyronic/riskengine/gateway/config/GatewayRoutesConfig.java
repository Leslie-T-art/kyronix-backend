package com.kyronic.riskengine.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", route -> route.path("/api/v1/auth/**").uri("http://localhost:8081"))
                .route("olts-service", route -> route.path("/api/v1/olts/**").uri("http://localhost:8082"))
                .route("document-service", route -> route.path("/api/v1/documents/**").uri("http://localhost:8083"))
                .route("dashboard-service", route -> route.path("/api/v1/dashboard/**").uri("http://localhost:8084"))
                .build();
    }
}
