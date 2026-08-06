package com.kyronic.riskengine.auth.interfaces;

import com.kyronic.riskengine.auth.application.dto.AuthorizerCandidateResponse;
import com.kyronic.riskengine.auth.application.service.AdministrationService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizerDirectoryControllerTest {

    @Test
    void returnsEligibleDepartmentAuthorizers() {
        UUID departmentId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        AuthorizerDirectoryController controller = new AuthorizerDirectoryController(new FakeAdministrationService(departmentId));

        List<AuthorizerCandidateResponse> response = controller.candidates(departmentId, "OLTS_AUTHORIZE");

        assertThat(response).containsExactly(new AuthorizerCandidateResponse(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                departmentId,
                Set.of("OLTS_AUTHORIZE", "OLTS_READ"),
                true,
                false
        ));
    }

    private static final class FakeAdministrationService extends AdministrationService {
        private final UUID departmentId;

        private FakeAdministrationService(UUID departmentId) {
            super(null, null, null, null);
            this.departmentId = departmentId;
        }

        @Override
        public List<AuthorizerCandidateResponse> listEligibleAuthorizers(UUID departmentId, String permission) {
            return List.of(new AuthorizerCandidateResponse(
                    UUID.fromString("22222222-2222-2222-2222-222222222222"),
                    this.departmentId,
                    Set.of(permission, "OLTS_READ"),
                    true,
                    false
            ));
        }
    }
}
