package com.kyronic.riskengine.common.authorization;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServerSideAuthorizerResolverTest {

    private final ServerSideAuthorizerResolver resolver = new ServerSideAuthorizerResolver();

    @Test
    void resolvesDepartmentHeadInsteadOfInputter() {
        Long departmentId = 101L;
        Long inputterId = 1001L;
        Long headId = 1002L;

        AuthorizerResolutionRequest request = new AuthorizerResolutionRequest(
                departmentId,
                inputterId,
                1003L,
                "OLTS_AUTHORIZE",
                Instant.parse("2026-08-05T08:00:00Z")
        );

        AuthorizerCandidate result = resolver.resolve(request, List.of(
                new AuthorizerCandidate(inputterId, departmentId, Set.of("OLTS_AUTHORIZE"), true, false, null, null, null),
                new AuthorizerCandidate(headId, departmentId, Set.of("OLTS_AUTHORIZE"), true, false, null, null, null)
        ));

        assertThat(result.userId()).isEqualTo(headId);
    }

    @Test
    void rejectsExpiredDelegate() {
        Long departmentId = 101L;

        AuthorizerResolutionRequest request = new AuthorizerResolutionRequest(
                departmentId,
                1001L,
                1004L,
                "OLTS_AUTHORIZE",
                Instant.parse("2026-08-05T08:00:00Z")
        );

        assertThatThrownBy(() -> resolver.resolve(request, List.of(
                new AuthorizerCandidate(1005L, departmentId, Set.of("OLTS_AUTHORIZE"), true, true, 1006L,
                        Instant.parse("2026-07-01T00:00:00Z"), Instant.parse("2026-07-31T23:59:59Z"))
        ))).isInstanceOf(AuthorizationException.class);
    }
}
