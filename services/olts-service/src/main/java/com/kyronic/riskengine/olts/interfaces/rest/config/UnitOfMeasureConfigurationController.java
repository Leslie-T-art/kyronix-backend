package com.kyronic.riskengine.olts.interfaces.rest.config;

import com.kyronic.riskengine.olts.application.service.config.UnitOfMeasureConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/olts/config/units-of-measure")
@Tag(name = "OLTS Config - Unit Of Measure", description = "CRUD endpoints for OLTS units of measure configuration.")
public class UnitOfMeasureConfigurationController extends AbstractOltsConfigurationController {

    public UnitOfMeasureConfigurationController(UnitOfMeasureConfigurationService service) {
        super(service, "Unit of measure");
    }
}
