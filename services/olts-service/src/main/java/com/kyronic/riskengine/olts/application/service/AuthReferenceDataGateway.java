package com.kyronic.riskengine.olts.application.service;

import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.olts.application.dto.ReferenceDataOptionResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AuthReferenceDataGateway {

    private static final ParameterizedTypeReference<ApiResponse<List<ReferenceDataOptionResponse>>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public AuthReferenceDataGateway(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://localhost:8081")
                .build();
    }

    public List<ReferenceDataOptionResponse> listDepartments(String authorizationHeader) {
        return fetch("/api/v1/admin/departments", authorizationHeader);
    }

    public List<ReferenceDataOptionResponse> listBranches(String authorizationHeader) {
        return fetch("/api/v1/admin/branches", authorizationHeader);
    }

    public List<ReferenceDataOptionResponse> listEventTypes(String authorizationHeader) {
        return fetch("/api/v1/admin/event-types", authorizationHeader);
    }

    private List<ReferenceDataOptionResponse> fetch(String path, String authorizationHeader) {
        ApiResponse<List<ReferenceDataOptionResponse>> response = restClient.get()
                .uri(path)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .body(RESPONSE_TYPE);
        return response == null || response.data() == null ? List.of() : response.data();
    }
}
