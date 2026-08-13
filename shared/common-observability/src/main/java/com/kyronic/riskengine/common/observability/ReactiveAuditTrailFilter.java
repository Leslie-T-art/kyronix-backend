package com.kyronic.riskengine.common.observability;

import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class ReactiveAuditTrailFilter implements WebFilter {

    private final PlatformAuditPublisher auditPublisher;
    private final Clock clock;
    private final String serviceName;

    public ReactiveAuditTrailFilter(PlatformAuditPublisher auditPublisher, Clock clock, String serviceName) {
        this.auditPublisher = auditPublisher;
        this.clock = clock;
        this.serviceName = serviceName;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (shouldSkip(path)) {
            return chain.filter(exchange);
        }

        String correlationId = exchange.getRequest().getHeaders().getFirst(ServletAuditTrailFilter.CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        exchange.getResponse().getHeaders().set(ServletAuditTrailFilter.CORRELATION_ID_HEADER, correlationId);

        String finalCorrelationId = correlationId;
        return chain.filter(exchange)
                .doOnSuccess(ignored -> publish(exchange, finalCorrelationId, Instant.now(clock), null))
                .doOnError(error -> publish(exchange, finalCorrelationId, Instant.now(clock), error));
    }

    private boolean shouldSkip(String path) {
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/api/v1/internal/audit/entries");
    }

    private void publish(ServerWebExchange exchange, String correlationId, Instant occurredAt, Throwable error) {
        ServerHttpResponse response = exchange.getResponse();
        int statusCode = response.getStatusCode() == null ? 500 : response.getStatusCode().value();
        if (error != null && statusCode < 400) {
            statusCode = 500;
        }

        auditPublisher.publish(new AuditTrailEntryRequest(
                serviceName,
                "HTTP_REQUEST",
                exchange.getRequest().getMethod() + " " + exchange.getRequest().getURI().getPath(),
                exchange.getRequest().getMethod() == null ? "UNKNOWN" : exchange.getRequest().getMethod().name(),
                exchange.getRequest().getURI().getPath(),
                exchange.getRequest().getURI().getRawQuery(),
                statusCode,
                outcome(statusCode),
                null,
                null,
                exchange.getRequest().getRemoteAddress() == null ? null : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress(),
                exchange.getRequest().getHeaders().getFirst("User-Agent"),
                correlationId,
                occurredAt
        ));
    }

    private String outcome(int statusCode) {
        if (statusCode >= 500) {
            return "ERROR";
        }
        if (statusCode >= 400) {
            return "REJECTED";
        }
        return "SUCCESS";
    }
}
