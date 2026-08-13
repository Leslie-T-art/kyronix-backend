package com.kyronic.riskengine.processflows.application.dto;

import jakarta.validation.constraints.Size;

public record ProcessFlowWorkflowActionRequest(
        @Size(max = 1000) String comment
) {
}
