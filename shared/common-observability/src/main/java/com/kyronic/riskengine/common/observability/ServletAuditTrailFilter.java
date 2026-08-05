package com.kyronic.riskengine.common.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class ServletAuditTrailFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    private final PlatformAuditPublisher auditPublisher;
    private final Clock clock;
    private final String serviceName;

    public ServletAuditTrailFilter(PlatformAuditPublisher auditPublisher, Clock clock, String serviceName) {
        this.auditPublisher = auditPublisher;
        this.clock = clock;
        this.serviceName = serviceName;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/api/v1/internal/audit/entries");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        try {
            filterChain.doFilter(request, response);
            statusCode = response.getStatus();
        } catch (RuntimeException | ServletException | IOException exception) {
            statusCode = response.getStatus() > 0 ? response.getStatus() : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            publish(request, correlationId, statusCode, Instant.now(clock));
            throw exception;
        }

        publish(request, correlationId, statusCode, Instant.now(clock));
    }

    private void publish(HttpServletRequest request, String correlationId, int statusCode, Instant occurredAt) {
        String username = request.getUserPrincipal() == null ? null : request.getUserPrincipal().getName();
        auditPublisher.publish(new AuditTrailEntryRequest(
                serviceName,
                "HTTP_REQUEST",
                request.getMethod() + " " + request.getRequestURI(),
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                statusCode,
                outcome(statusCode),
                username,
                null,
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
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
