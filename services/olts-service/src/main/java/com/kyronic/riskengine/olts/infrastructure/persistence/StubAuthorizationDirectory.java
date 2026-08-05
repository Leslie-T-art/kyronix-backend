package com.kyronic.riskengine.olts.infrastructure.persistence;

import com.kyronic.riskengine.common.authorization.AuthorizerCandidate;
import com.kyronic.riskengine.olts.application.service.AuthorizationDirectory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class StubAuthorizationDirectory implements AuthorizationDirectory {

    @Override
    public List<AuthorizerCandidate> findCandidates(UUID departmentId, String permission) {
        return List.of(new AuthorizerCandidate(
                UUID.fromString("00000000-0000-0000-0000-000000000999"),
                departmentId,
                Set.of(permission),
                true,
                false,
                null,
                null,
                null
        ));
    }
}
