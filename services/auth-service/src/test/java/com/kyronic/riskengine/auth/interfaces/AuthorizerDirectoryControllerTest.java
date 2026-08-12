package com.kyronic.riskengine.auth.interfaces;

import com.kyronic.riskengine.auth.application.dto.AuthorizerCandidateResponse;
import com.kyronic.riskengine.auth.application.service.AdministrationService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizerDirectoryControllerTest {

    @Test
    void returnsEligibleDepartmentAuthorizers() {
        Long departmentId = 101L;
        AuthorizerDirectoryController controller = new AuthorizerDirectoryController(new FakeAdministrationService(departmentId));

        List<AuthorizerCandidateResponse> response = controller.candidates(departmentId, "OLTS_AUTHORIZE");

        assertThat(response).containsExactly(new AuthorizerCandidateResponse(
                222222L,
                departmentId,
                Set.of("OLTS_AUTHORIZE", "OLTS_READ"),
                true,
                false
        ));
    }

    private static final class FakeAdministrationService extends AdministrationService {
        private final Long departmentId;

        private FakeAdministrationService(Long departmentId) {
            super(null, null, null, null);
            this.departmentId = departmentId;
        }

        @Override
        public List<AuthorizerCandidateResponse> listEligibleAuthorizers(Long departmentId, String permission) {
            return List.of(new AuthorizerCandidateResponse(
                    222222L,
                    this.departmentId,
                    Set.of(permission, "OLTS_READ"),
                    true,
                    false
            ));
        }
    }
}
