package com.kyronic.riskengine.auth.interfaces;

import com.kyronic.riskengine.auth.application.dto.AuthorizerCandidateResponse;
import com.kyronic.riskengine.auth.application.service.AdministrationService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Hidden
@RestController
@RequestMapping("/api/v1/internal/authorizers")
public class AuthorizerDirectoryController {

    private final AdministrationService administrationService;

    public AuthorizerDirectoryController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @GetMapping("/candidates")
    public List<AuthorizerCandidateResponse> candidates(@RequestParam("departmentId") Long departmentId,
                                                        @RequestParam("permission") String permission) {
        return administrationService.listEligibleAuthorizers(departmentId, permission);
    }
}
