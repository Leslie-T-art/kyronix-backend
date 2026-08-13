package com.kyronic.riskengine.olts.interfaces.rest.config;

import com.kyronic.riskengine.olts.application.service.config.KriCategoryConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/olts/config/kri-categories")
@Tag(name = "OLTS Config - KRI Category", description = "CRUD endpoints for OLTS KRI category configuration.")
public class KriCategoryConfigurationController extends AbstractOltsConfigurationController {

    public KriCategoryConfigurationController(KriCategoryConfigurationService service) {
        super(service, "KRI category");
    }
}
