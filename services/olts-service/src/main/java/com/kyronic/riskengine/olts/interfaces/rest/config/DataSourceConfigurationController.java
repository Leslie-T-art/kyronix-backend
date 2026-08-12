package com.kyronic.riskengine.olts.interfaces.rest.config;

import com.kyronic.riskengine.olts.application.service.config.DataSourceConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/olts/config/data-sources")
@Tag(name = "OLTS Config - Data Source", description = "CRUD endpoints for OLTS data source configuration.")
public class DataSourceConfigurationController extends AbstractOltsConfigurationController {

    public DataSourceConfigurationController(DataSourceConfigurationService service) {
        super(service, "Data source");
    }
}
