package com.kyronic.riskengine.olts.interfaces.rest.config;

import com.kyronic.riskengine.olts.application.service.config.EventStatusConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/olts/config/event-statuses")
@Tag(name = "OLTS Config - Event Status", description = "CRUD endpoints for OLTS event status configuration.")
public class EventStatusConfigurationController extends AbstractOltsConfigurationController {

    public EventStatusConfigurationController(EventStatusConfigurationService service) {
        super(service, "Event status");
    }
}
