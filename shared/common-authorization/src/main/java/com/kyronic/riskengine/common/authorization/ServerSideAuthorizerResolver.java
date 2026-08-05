package com.kyronic.riskengine.common.authorization;

import com.kyronic.riskengine.common.api.ErrorCodes;

import java.util.Comparator;
import java.util.List;

public class ServerSideAuthorizerResolver {

    public AuthorizerCandidate resolve(AuthorizerResolutionRequest request, List<AuthorizerCandidate> candidates) {
        return candidates.stream()
                .filter(candidate -> candidate.active())
                .filter(candidate -> candidate.departmentId().equals(request.departmentId()))
                .filter(candidate -> candidate.permissions().contains(request.requiredPermission()))
                .filter(candidate -> candidate.isEffectiveAt(request.when()))
                .filter(candidate -> !candidate.userId().equals(request.inputterUserId()))
                .filter(candidate -> !candidate.userId().equals(request.lastModifiedBy()))
                .sorted(Comparator.comparing(AuthorizerCandidate::delegated))
                .findFirst()
                .orElseThrow(() -> new AuthorizationException(
                        "No eligible authorizer resolved for department",
                        ErrorCodes.AUTHORIZER_RESOLUTION_FAILED
                ));
    }
}
