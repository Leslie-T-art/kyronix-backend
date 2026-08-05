package com.kyronic.riskengine.common.authorization;

public enum AuthorizationRequestStatus {
    PENDING,
    IN_REVIEW,
    APPROVED,
    REJECTED,
    RETURNED,
    WITHDRAWN,
    ESCALATED
}
