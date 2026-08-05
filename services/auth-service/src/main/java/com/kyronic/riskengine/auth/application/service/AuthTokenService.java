package com.kyronic.riskengine.auth.application.service;

import com.kyronic.riskengine.auth.application.dto.LoginRequest;
import com.kyronic.riskengine.auth.application.dto.LoginResponse;
import com.kyronic.riskengine.auth.infrastructure.security.LocalUserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class AuthTokenService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final AuthTokenSettings tokenSettings;
    private final Clock clock;

    public AuthTokenService(AuthenticationManager authenticationManager,
                            JwtEncoder jwtEncoder,
                            AuthTokenSettings tokenSettings,
                            Clock clock) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.tokenSettings = tokenSettings;
        this.clock = clock;
    }

    public LoginResponse login(LoginRequest request) {
        LocalUserPrincipal principal = (LocalUserPrincipal) authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password())
        ).getPrincipal();

        Instant issuedAt = Instant.now(clock);
        Instant expiresAt = issuedAt.plusSeconds(tokenSettings.accessTokenTtlSeconds());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(tokenSettings.issuer())
                .audience(List.of(tokenSettings.audience()))
                .subject(principal.getUsername())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("userId", principal.getUserId().toString())
                .claim("roles", principal.getRoles())
                .claim("permissions", principal.getPermissions())
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(() -> "HS256").build(),
                claims
        )).getTokenValue();

        return new LoginResponse(
                accessToken,
                "Bearer",
                tokenSettings.accessTokenTtlSeconds(),
                issuedAt,
                expiresAt,
                principal.getUserId(),
                principal.getUsername(),
                principal.getFullName(),
                principal.getDepartmentId(),
                principal.getBranchId(),
                principal.getRoles(),
                principal.getPermissions()
        );
    }
}
