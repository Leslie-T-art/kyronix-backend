package com.kyronic.riskengine.auth.application.dto;

import java.util.Set;

public record AuthMeResponse(
        String username,
        Set<String> roles,
        Set<String> permissions
) {
}
