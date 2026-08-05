package com.kyronic.riskengine.audit.application.service;

import com.kyronic.riskengine.audit.application.dto.AuditTrailResponse;
import com.kyronic.riskengine.audit.domain.AuditTrailEntry;
import com.kyronic.riskengine.audit.infrastructure.persistence.AuditTrailRepository;
import com.kyronic.riskengine.common.observability.AuditTrailEntryRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class AuditTrailService {

    private final AuditTrailRepository repository;

    public AuditTrailService(AuditTrailRepository repository) {
        this.repository = repository;
    }

    public AuditTrailResponse record(AuditTrailEntryRequest request) {
        AuditTrailEntry saved = repository.save(new AuditTrailEntry(
                UUID.randomUUID(),
                request.serviceName(),
                request.category(),
                request.action(),
                request.httpMethod(),
                request.requestPath(),
                request.queryString(),
                request.statusCode(),
                request.outcome(),
                request.username(),
                request.userId(),
                request.sourceIp(),
                request.userAgent(),
                request.correlationId(),
                request.occurredAt()
        ));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AuditTrailResponse> listTrail(String serviceName,
                                              String username,
                                              String outcome,
                                              String requestPath,
                                              String searchText,
                                              Integer limit) {
        int resolvedLimit = normalizeLimit(limit);
        String normalizedServiceName = normalize(serviceName);
        String normalizedUsername = normalize(username);
        String normalizedOutcome = normalize(outcome);
        String normalizedRequestPath = normalize(requestPath);
        String normalizedSearchText = normalize(searchText);

        return repository.findAll(Sort.by(Sort.Direction.DESC, "occurredAt")).stream()
                .filter(entry -> matchesEquals(entry.getServiceName(), normalizedServiceName))
                .filter(entry -> matchesEquals(entry.getUsername(), normalizedUsername))
                .filter(entry -> matchesEquals(entry.getOutcome(), normalizedOutcome))
                .filter(entry -> matchesContains(entry.getRequestPath(), normalizedRequestPath))
                .filter(entry -> matchesSearch(entry, normalizedSearchText))
                .limit(resolvedLimit)
                .map(this::toResponse)
                .toList();
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit < 1) {
            return 200;
        }
        return Math.min(limit, 1000);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private boolean matchesEquals(String actualValue, String expectedValue) {
        if (expectedValue == null) {
            return true;
        }
        return actualValue != null && actualValue.equalsIgnoreCase(expectedValue);
    }

    private boolean matchesContains(String actualValue, String expectedValue) {
        if (expectedValue == null) {
            return true;
        }
        return actualValue != null && actualValue.toLowerCase(Locale.ROOT).contains(expectedValue.toLowerCase(Locale.ROOT));
    }

    private boolean matchesSearch(AuditTrailEntry entry, String searchText) {
        if (searchText == null) {
            return true;
        }
        String normalizedSearchText = searchText.toLowerCase(Locale.ROOT);
        return contains(entry.getServiceName(), normalizedSearchText)
                || contains(entry.getAction(), normalizedSearchText)
                || contains(entry.getRequestPath(), normalizedSearchText)
                || contains(entry.getUsername(), normalizedSearchText);
    }

    private boolean contains(String actualValue, String searchText) {
        return actualValue != null && actualValue.toLowerCase(Locale.ROOT).contains(searchText);
    }

    private AuditTrailResponse toResponse(AuditTrailEntry entry) {
        return new AuditTrailResponse(
                entry.getId(),
                entry.getServiceName(),
                entry.getCategory(),
                entry.getAction(),
                entry.getHttpMethod(),
                entry.getRequestPath(),
                entry.getQueryString(),
                entry.getStatusCode(),
                entry.getOutcome(),
                entry.getUsername(),
                entry.getUserId(),
                entry.getSourceIp(),
                entry.getUserAgent(),
                entry.getCorrelationId(),
                entry.getOccurredAt()
        );
    }
}
