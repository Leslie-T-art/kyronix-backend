package com.kyronic.riskengine.olts.interfaces.rest.config;

import com.kyronic.riskengine.olts.application.service.config.RecoveryMethodConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/olts/config/recovery-methods")
@Tag(name = "OLTS Config - Recovery Method", description = "CRUD endpoints for OLTS recovery method configuration.")
public class RecoveryMethodConfigurationController extends AbstractOltsConfigurationController {

    public RecoveryMethodConfigurationController(RecoveryMethodConfigurationService service) {
        super(service, "Recovery method");
    }
}
