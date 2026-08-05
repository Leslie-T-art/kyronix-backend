package com.kyronic.riskengine.auth.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyronic.riskengine.auth.application.service.AuditRequestFactory;
import com.kyronic.riskengine.auth.application.service.AuditTrailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProblemDetailsAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final AuditTrailService auditTrailService;
    private final AuditRequestFactory auditRequestFactory;

    public ProblemDetailsAuthenticationEntryPoint(ObjectMapper objectMapper,
                                                 AuditTrailService auditTrailService,
                                                 AuditRequestFactory auditRequestFactory) {
        this.objectMapper = objectMapper;
        this.auditTrailService = auditTrailService;
        this.auditRequestFactory = auditRequestFactory;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Authentication required");
        problemDetail.setDetail(authException.getMessage());
        problemDetail.setProperty("errorCode", "AUTHENTICATION_REQUIRED");
        problemDetail.setProperty("correlationId", auditRequestFactory.resolveCorrelationId(request));
        auditTrailService.record(auditRequestFactory.createUnauthenticated(
                request,
                "AUTHENTICATION_REQUIRED",
                "UNAUTHENTICATED_REQUEST",
                "HTTP_REQUEST",
                "FAILED",
                authException.getMessage()
        ));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problemDetail);
    }
}
