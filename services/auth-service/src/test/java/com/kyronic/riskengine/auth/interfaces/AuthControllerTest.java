package com.kyronic.riskengine.auth.interfaces;

import com.kyronic.riskengine.auth.application.dto.LoginRequest;
import com.kyronic.riskengine.auth.application.dto.LoginResponse;
import com.kyronic.riskengine.auth.application.service.AuthTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest {

    @Test
    void loginReturnsBearerTokenPayload() {
        AuthController controller = new AuthController(new FakeAuthTokenService());

        var response = controller.login(new LoginRequest("risk.inputter", "ChangeMe123!"));

        assertThat(response.success()).isTrue();
        assertThat(response.data().accessToken()).isEqualTo("jwt-token");
        assertThat(response.data().tokenType()).isEqualTo("Bearer");
    }

    @Test
    void meReturnsRolesAndPermissions() {
        AuthController controller = new AuthController(new FakeAuthTokenService());
        var authentication = new UsernamePasswordAuthenticationToken(
                "risk.inputter",
                "n/a",
                Set.of(new SimpleGrantedAuthority("ROLE_INPUTTER"), new SimpleGrantedAuthority("OLTS_CREATE"))
        );

        var response = controller.me(authentication);

        assertThat(response.success()).isTrue();
        assertThat(response.data().username()).isEqualTo("risk.inputter");
        assertThat(response.data().roles()).containsExactly("INPUTTER");
        assertThat(response.data().permissions()).containsExactly("OLTS_CREATE");
    }

    private static final class FakeAuthTokenService extends AuthTokenService {
        private FakeAuthTokenService() {
            super(null, null, null, Clock.fixed(Instant.parse("2026-08-05T08:30:00Z"), ZoneOffset.UTC));
        }

        @Override
        public LoginResponse login(LoginRequest request) {
            return new LoginResponse(
                    "jwt-token",
                    "Bearer",
                    600,
                    Instant.parse("2026-08-05T08:30:00Z"),
                    Instant.parse("2026-08-05T08:40:00Z"),
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    request.username(),
                    "Risk Inputter",
                    UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                    UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                    Set.of("INPUTTER"),
                    Set.of("OLTS_CREATE", "OLTS_READ")
            );
        }
    }
}
