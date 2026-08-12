package com.kyronic.riskengine.common.api;

public final class ErrorCodes {

    public static final String MAKER_CANNOT_AUTHORIZE = "MAKER_CANNOT_AUTHORIZE";
    public static final String LAST_EDITOR_CANNOT_AUTHORIZE = "LAST_EDITOR_CANNOT_AUTHORIZE";
    public static final String AUTHORIZATION_VERSION_CONFLICT = "AUTHORIZATION_VERSION_CONFLICT";
    public static final String INVALID_WORKFLOW_TRANSITION = "INVALID_WORKFLOW_TRANSITION";
    public static final String AUTHORIZER_RESOLUTION_FAILED = "AUTHORIZER_RESOLUTION_FAILED";
    public static final String FORBIDDEN_CROSS_DEPARTMENT_ACCESS = "FORBIDDEN_CROSS_DEPARTMENT_ACCESS";
    public static final String READ_ONLY_ROLE_FORBIDDEN = "READ_ONLY_ROLE_FORBIDDEN";
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String REFERENTIAL_INTEGRITY_CONFLICT = "REFERENTIAL_INTEGRITY_CONFLICT";
    public static final String DUPLICATE_BUSINESS_REFERENCE = "DUPLICATE_BUSINESS_REFERENCE";
    public static final String INVALID_REFERENCE_DATA = "INVALID_REFERENCE_DATA";
    public static final String INVALID_DATE_RANGE = "INVALID_DATE_RANGE";
    public static final String INVALID_AMOUNT = "INVALID_AMOUNT";
    public static final String DEPARTMENT_SCOPE_REQUIRED = "DEPARTMENT_SCOPE_REQUIRED";

    private ErrorCodes() {
    }
}
