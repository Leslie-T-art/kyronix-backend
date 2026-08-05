package com.kyronic.riskengine.auth.application.dto;

import java.util.UUID;

public record ReferenceDataResponse(
        UUID id,
        String code,
        String name,
        boolean active
) {
}
