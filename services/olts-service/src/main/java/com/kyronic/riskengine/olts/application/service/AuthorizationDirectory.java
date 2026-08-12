package com.kyronic.riskengine.olts.application.service;

import com.kyronic.riskengine.common.authorization.AuthorizerCandidate;

import java.util.List;

public interface AuthorizationDirectory {
    List<AuthorizerCandidate> findCandidates(Long departmentId, String permission);
}
