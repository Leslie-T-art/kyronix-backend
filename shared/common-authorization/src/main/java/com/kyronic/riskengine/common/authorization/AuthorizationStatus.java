package com.kyronic.riskengine.common.authorization;

public enum AuthorizationStatus {
    DRAFT,
    PENDING_AUTHORIZATION,
    UNDER_AUTHORIZATION_REVIEW,
    RETURNED_FOR_CORRECTION,
    AUTHORIZED,
    REJECTED,
    WITHDRAWN,
    CANCELLED,
    SUPERSEDED,
    ARCHIVED
}
