package com.kyronic.riskengine.auth.interfaces;

import com.kyronic.riskengine.auth.application.dto.LoginRequest;
import com.kyronic.riskengine.auth.application.dto.LoginResponse;
import com.kyronic.riskengine.auth.application.dto.AuthMeResponse;
import com.kyronic.riskengine.auth.application.service.AuditRequestFactory;
import com.kyronic.riskengine.auth.application.service.AuditTrailService;
import com.kyronic.riskengine.auth.application.service.AuthTokenService;
import com.kyronic.riskengine.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and identity endpoints")
public class AuthController {

    private final AuthTokenService authTokenService;
    private final AuditTrailService auditTrailService;
    private final AuditRequestFactory auditRequestFactory;

    public AuthController(AuthTokenService authTokenService,
                          AuditTrailService auditTrailService,
                          AuditRequestFactory auditRequestFactory) {
        this.authTokenService = authTokenService;
        this.auditTrailService = auditTrailService;
        this.auditRequestFactory = auditRequestFactory;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate a user and return a bearer access token.", security = @SecurityRequirement(name = ""))
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        LoginResponse response = authTokenService.login(request);
        auditTrailService.record(auditRequestFactory.createLoginSuccess(httpRequest, response));
        return ApiResponse.success("Authentication successful", response, auditRequestFactory.resolveCorrelationId(httpRequest));
    }

    @GetMapping("/me")
    @Operation(summary = "Current user", description = "Resolve the currently authenticated principal from the bearer token.")
    public ApiResponse<AuthMeResponse> me(Authentication authentication, HttpServletRequest httpRequest) {
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        Set<String> roles = authorities.stream()
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .collect(Collectors.toSet());
        Set<String> permissions = authorities.stream()
                .filter(authority -> !authority.startsWith("ROLE_"))
                .collect(Collectors.toSet());
        String username = authentication.getPrincipal() instanceof Jwt jwt ? jwt.getSubject() : authentication.getName();
        AuthMeResponse response = new AuthMeResponse(username, roles, permissions);
        auditTrailService.record(auditRequestFactory.create(
                authentication,
                httpRequest,
                "AUTH_PROFILE_VIEWED",
                "VIEW_PROFILE",
                "USER_ACCOUNT",
                null,
                username,
                "SUCCESS",
                null,
                null,
                response
        ));
        return ApiResponse.success(
                "Authenticated principal resolved",
                response,
                auditRequestFactory.resolveCorrelationId(httpRequest)
        );
    }
}
