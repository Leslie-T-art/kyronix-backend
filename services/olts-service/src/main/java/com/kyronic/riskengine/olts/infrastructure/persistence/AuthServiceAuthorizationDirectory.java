package com.kyronic.riskengine.olts.infrastructure.persistence;

import com.kyronic.riskengine.common.authorization.AuthorizerCandidate;
import com.kyronic.riskengine.olts.application.service.AuthorizationDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;

@Component
public class AuthServiceAuthorizationDirectory implements AuthorizationDirectory {

    private static final ParameterizedTypeReference<List<AuthAuthorizerCandidateResponse>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public AuthServiceAuthorizationDirectory(RestClient.Builder restClientBuilder,
                                             @Value("${kyronic.auth.service-url:http://localhost:8081}") String authServiceUrl) {
        this.restClient = restClientBuilder
                .baseUrl(authServiceUrl)
                .build();
    }

    @Override
    public List<AuthorizerCandidate> findCandidates(Long departmentId, String permission) {
        List<AuthAuthorizerCandidateResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/internal/authorizers/candidates")
                        .queryParam("departmentId", departmentId)
                        .queryParam("permission", permission)
                        .build())
                .retrieve()
                .body(RESPONSE_TYPE);
        if (response == null) {
            return List.of();
        }
        return response.stream()
                .map(candidate -> new AuthorizerCandidate(
                        candidate.userId(),
                        candidate.departmentId(),
                        candidate.permissions() == null ? Set.of() : candidate.permissions(),
                        candidate.active(),
                        candidate.delegated(),
                        null,
                        null,
                        null
                ))
                .toList();
    }

    private record AuthAuthorizerCandidateResponse(
            Long userId,
            Long departmentId,
            Set<String> permissions,
            boolean active,
            boolean delegated
    ) {
    }
}
