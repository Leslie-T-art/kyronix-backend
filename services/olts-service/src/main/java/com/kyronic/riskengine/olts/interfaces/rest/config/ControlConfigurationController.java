package com.kyronic.riskengine.olts.interfaces.rest.config;

import com.kyronic.riskengine.olts.application.service.config.ControlConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/olts/config/controls")
@Tag(name = "OLTS Config - Controls", description = "CRUD endpoints for OLTS controls configuration.")
public class ControlConfigurationController extends AbstractOltsConfigurationController {

    public ControlConfigurationController(ControlConfigurationService service) {
        super(service, "Control");
    }
}
