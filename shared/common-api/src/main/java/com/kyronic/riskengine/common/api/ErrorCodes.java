package com.kyronic.riskengine.common.api;

public final class ErrorCodes {

    public static final String MAKER_CANNOT_AUTHORIZE = "MAKER_CANNOT_AUTHORIZE";
    public static final String LAST_EDITOR_CANNOT_AUTHORIZE = "LAST_EDITOR_CANNOT_AUTHORIZE";
    public static final String AUTHORIZATION_VERSION_CONFLICT = "AUTHORIZATION_VERSION_CONFLICT";
    public static final String INVALID_WORKFLOW_TRANSITION = "INVALID_WORKFLOW_TRANSITION";
    public static final String AUTHORIZER_RESOLUTION_FAILED = "AUTHORIZER_RESOLUTION_FAILED";

    private ErrorCodes() {
    }
}
