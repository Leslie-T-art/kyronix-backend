package com.kyronic.riskengine.common.observability;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class PlatformAuditPublisher {

    private final RestClient restClient;

    public PlatformAuditPublisher(RestClient restClient) {
        this.restClient = restClient;
    }

    public void publish(AuditTrailEntryRequest entry) {
        try {
            restClient.post()
                    .uri("/api/v1/internal/audit/entries")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(entry)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RuntimeException ignored) {
            // Audit publication must never break business traffic.
        }
    }
}
