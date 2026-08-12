package com.kyronic.riskengine.olts.interfaces.rest.config;

import com.kyronic.riskengine.olts.application.service.config.BaselEventCategoryConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/olts/config/basel-event-categories")
@Tag(name = "OLTS Config - Basel Event Category", description = "CRUD endpoints for OLTS Basel event category configuration.")
public class BaselEventCategoryConfigurationController extends AbstractOltsConfigurationController {

    public BaselEventCategoryConfigurationController(BaselEventCategoryConfigurationService service) {
        super(service, "Basel event category");
    }
}
