package com.kyronic.riskengine.olts.interfaces.rest.config;

import com.kyronic.riskengine.olts.application.service.config.ActionStatusConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/olts/config/action-statuses")
@Tag(name = "OLTS Config - Action Status", description = "CRUD endpoints for OLTS action status configuration.")
public class ActionStatusConfigurationController extends AbstractOltsConfigurationController {

    public ActionStatusConfigurationController(ActionStatusConfigurationService service) {
        super(service, "Action status");
    }
}
