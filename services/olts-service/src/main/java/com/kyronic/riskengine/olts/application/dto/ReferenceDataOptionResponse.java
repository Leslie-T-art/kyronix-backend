package com.kyronic.riskengine.olts.application.dto;

import java.util.UUID;

public record ReferenceDataOptionResponse(
        UUID id,
        String code,
        String name,
        boolean active
) {
}
