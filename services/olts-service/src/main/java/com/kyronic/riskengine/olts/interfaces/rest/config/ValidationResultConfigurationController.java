package com.kyronic.riskengine.olts.interfaces.rest.config;

import com.kyronic.riskengine.olts.application.service.config.ValidationResultConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/olts/config/validation-results")
@Tag(name = "OLTS Config - Validation Result", description = "CRUD endpoints for OLTS validation result configuration.")
public class ValidationResultConfigurationController extends AbstractOltsConfigurationController {

    public ValidationResultConfigurationController(ValidationResultConfigurationService service) {
        super(service, "Validation result");
    }
}
