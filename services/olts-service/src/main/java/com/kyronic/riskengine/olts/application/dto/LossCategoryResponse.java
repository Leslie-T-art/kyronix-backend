package com.kyronic.riskengine.olts.application.dto;

import java.util.UUID;

public record LossCategoryResponse(
        UUID id,
        String code,
        String name,
        String description
) {
}
