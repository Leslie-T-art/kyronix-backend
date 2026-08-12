package com.kyronic.riskengine.olts.interfaces.rest.config;

import com.kyronic.riskengine.olts.application.service.config.RootCauseConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/olts/config/root-causes")
@Tag(name = "OLTS Config - Root Cause", description = "CRUD endpoints for OLTS root cause configuration.")
public class RootCauseConfigurationController extends AbstractOltsConfigurationController {

    public RootCauseConfigurationController(RootCauseConfigurationService service) {
        super(service, "Root cause");
    }
}
