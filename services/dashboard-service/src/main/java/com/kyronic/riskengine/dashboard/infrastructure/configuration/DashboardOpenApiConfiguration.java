package com.kyronic.riskengine.dashboard.infrastructure.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DashboardOpenApiConfiguration {

    public static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    OpenAPI dashboardOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kyronic Dashboard Service API")
                        .version("v1")
                        .description("Role-aware dashboard analytics APIs with JWT-protected access."))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
