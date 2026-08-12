package com.kyronic.riskengine.olts.interfaces.rest.config;

import com.kyronic.riskengine.olts.application.service.config.CurrencyConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/olts/config/currencies")
@Tag(name = "OLTS Config - Currency", description = "CRUD endpoints for OLTS currency configuration.")
public class CurrencyConfigurationController extends AbstractOltsConfigurationController {

    public CurrencyConfigurationController(CurrencyConfigurationService service) {
        super(service, "Currency");
    }
}
