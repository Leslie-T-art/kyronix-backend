package com.kyronic.riskengine.gateway.config;

import com.kyronic.riskengine.common.observability.AuditPublisherProperties;
import com.kyronic.riskengine.common.observability.AuditTrailEntryRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import java.time.Instant;
import java.util.UUID;

@Configuration
@EnableConfigurationProperties(AuditPublisherProperties.class)
public class GatewayAuditConfiguration {

    @Bean
    WebClient gatewayAuditWebClient(WebClient.Builder builder, AuditPublisherProperties properties) {
        return builder.baseUrl(properties.getServiceUrl()).build();
    }

    @Bean
    GlobalFilter gatewayAuditFilter(WebClient gatewayAuditWebClient,
                                    AuditPublisherProperties properties,
                                    Environment environment) {
        String serviceName = environment.getProperty("spring.application.name", "api-gateway");
        return (exchange, chain) -> {
            if (!properties.isEnabled()) {
                return chain.filter(exchange);
            }
            String path = exchange.getRequest().getPath().value();
            if (path.startsWith("/actuator")) {
                return chain.filter(exchange);
            }
            String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-Id");
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }
            String finalCorrelationId = correlationId;
            exchange.getResponse().getHeaders().set("X-Correlation-Id", finalCorrelationId);
            return chain.filter(exchange)
                    .then(exchange.getPrincipal()
                            .map(principal -> principal.getName())
                            .defaultIfEmpty("")
                            .flatMap(username -> gatewayAuditWebClient.post()
                                    .uri("/api/v1/internal/audit/entries")
                                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                                    .bodyValue(new AuditTrailEntryRequest(
                                            serviceName,
                                            "HTTP_REQUEST",
                                            exchange.getRequest().getMethod().name() + " " + path,
                                            exchange.getRequest().getMethod().name(),
                                            path,
                                            exchange.getRequest().getURI().getQuery(),
                                            exchange.getResponse().getStatusCode() == null ? 200 : exchange.getResponse().getStatusCode().value(),
                                            outcome(exchange.getResponse().getStatusCode() == null ? 200 : exchange.getResponse().getStatusCode().value()),
                                            username.isBlank() ? null : username,
                                            null,
                                            exchange.getRequest().getRemoteAddress() == null ? null : String.valueOf(exchange.getRequest().getRemoteAddress().getAddress()),
                                            exchange.getRequest().getHeaders().getFirst(HttpHeaders.USER_AGENT),
                                            finalCorrelationId,
                                            Instant.now()
                                    ))
                                    .retrieve()
                                    .toBodilessEntity()
                                    .onErrorResume(exception -> reactor.core.publisher.Mono.empty())
                                    .then()));
        };
    }

    private static String outcome(int statusCode) {
        if (statusCode >= 500) {
            return "ERROR";
        }
        if (statusCode >= 400) {
            return "REJECTED";
        }
        return "SUCCESS";
    }
}
