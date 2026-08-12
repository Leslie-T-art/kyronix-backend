package com.kyronic.riskengine.olts.interfaces.rest.config;

import com.kyronic.riskengine.olts.application.service.config.ResidualRiskConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/olts/config/residual-risks")
@Tag(name = "OLTS Config - Residual Risk", description = "CRUD endpoints for OLTS residual risk configuration.")
public class ResidualRiskConfigurationController extends AbstractOltsConfigurationController {

    public ResidualRiskConfigurationController(ResidualRiskConfigurationService service) {
        super(service, "Residual risk");
    }
}
