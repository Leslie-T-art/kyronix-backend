package com.kyronic.riskengine.olts.application.service;

import com.kyronic.riskengine.common.authorization.AuthorizerCandidate;

import java.util.List;
import java.util.UUID;

public interface AuthorizationDirectory {
    List<AuthorizerCandidate> findCandidates(UUID departmentId, String permission);
}
