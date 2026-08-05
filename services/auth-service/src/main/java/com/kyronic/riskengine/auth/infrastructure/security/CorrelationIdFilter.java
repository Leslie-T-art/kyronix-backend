package com.kyronic.riskengine.auth.infrastructure.security;

import com.kyronic.riskengine.auth.application.service.AuditRequestFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId = request.getHeader(AuditRequestFactory.CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        request.setAttribute(AuditRequestFactory.CORRELATION_ID_ATTRIBUTE, correlationId);
        response.setHeader(AuditRequestFactory.CORRELATION_ID_HEADER, correlationId);
        filterChain.doFilter(request, response);
    }
}
