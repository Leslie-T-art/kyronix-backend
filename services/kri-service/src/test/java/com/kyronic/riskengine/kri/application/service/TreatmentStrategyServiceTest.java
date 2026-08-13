package com.kyronic.riskengine.kri.application.service;

import com.kyronic.riskengine.kri.application.dto.TreatmentStrategyRequest;
import com.kyronic.riskengine.kri.domain.TreatmentStrategy;
import com.kyronic.riskengine.kri.infrastructure.persistence.TreatmentStrategyRepository;
import com.kyronic.riskengine.kri.interfaces.TreatmentStrategyNotFoundException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TreatmentStrategyServiceTest {

    @Test
    void createPersistsTreatmentStrategy() {
        List<TreatmentStrategy> storedStrategies = new ArrayList<>();
        TreatmentStrategyService service = new TreatmentStrategyService(
                repository(storedStrategies),
                new FixedCurrentUserProvider("risk.inputter"),
                Clock.fixed(Instant.parse("2026-08-13T10:00:00Z"), ZoneOffset.UTC)
        );

        var response = service.create(request());

        assertThat(response.code()).isEqualTo("MITIGATE");
        assertThat(response.name()).isEqualTo("Mitigate");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.createdBy()).isEqualTo("risk.inputter");
    }

    @Test
    void updateChangesStoredTreatmentStrategy() {
        List<TreatmentStrategy> storedStrategies = new ArrayList<>();
        storedStrategies.add(existingStrategy());
        TreatmentStrategyService service = new TreatmentStrategyService(
                repository(storedStrategies),
                new FixedCurrentUserProvider("dept.head"),
                Clock.fixed(Instant.parse("2026-08-13T11:00:00Z"), ZoneOffset.UTC)
        );

        var response = service.update(1L, new TreatmentStrategyRequest("TRANSFER", "Transfer", "INACTIVE"));

        assertThat(response.code()).isEqualTo("TRANSFER");
        assertThat(response.status()).isEqualTo("INACTIVE");
        assertThat(response.updatedBy()).isEqualTo("dept.head");
    }

    @Test
    void deleteRemovesTreatmentStrategy() {
        List<TreatmentStrategy> storedStrategies = new ArrayList<>();
        storedStrategies.add(existingStrategy());
        TreatmentStrategyService service = new TreatmentStrategyService(
                repository(storedStrategies),
                new FixedCurrentUserProvider("risk.inputter"),
                Clock.fixed(Instant.parse("2026-08-13T10:00:00Z"), ZoneOffset.UTC)
        );

        service.delete(1L);

        assertThat(storedStrategies).isEmpty();
    }

    @Test
    void getThrowsWhenStrategyMissing() {
        TreatmentStrategyService service = new TreatmentStrategyService(
                repository(new ArrayList<>()),
                new FixedCurrentUserProvider("risk.inputter"),
                Clock.fixed(Instant.parse("2026-08-13T10:00:00Z"), ZoneOffset.UTC)
        );

        assertThatThrownBy(() -> service.get(99L))
                .isInstanceOf(TreatmentStrategyNotFoundException.class)
                .hasMessage("Treatment strategy not found: 99");
    }

    @SuppressWarnings("unchecked")
    private TreatmentStrategyRepository repository(List<TreatmentStrategy> storedStrategies) {
        return (TreatmentStrategyRepository) Proxy.newProxyInstance(
                TreatmentStrategyRepository.class.getClassLoader(),
                new Class<?>[]{TreatmentStrategyRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "save" -> {
                        TreatmentStrategy strategy = (TreatmentStrategy) args[0];
                        storedStrategies.removeIf(existing -> existing.getId().equals(strategy.getId()));
                        storedStrategies.add(strategy);
                        yield strategy;
                    }
                    case "findById" -> storedStrategies.stream()
                            .filter(strategy -> strategy.getId().equals(args[0]))
                            .findFirst();
                    case "findAll" -> storedStrategies.stream().toList();
                    case "delete" -> {
                        TreatmentStrategy strategy = (TreatmentStrategy) args[0];
                        storedStrategies.removeIf(existing -> existing.getId().equals(strategy.getId()));
                        yield null;
                    }
                    case "toString" -> "FakeTreatmentStrategyRepository";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName() + " " + (args == null ? 0 : args.length));
                }
        );
    }

    private TreatmentStrategy existingStrategy() {
        return new TreatmentStrategy(
                1L,
                "MITIGATE",
                "Mitigate",
                "ACTIVE",
                Instant.parse("2026-08-01T10:00:00Z"),
                "system.admin",
                Instant.parse("2026-08-01T10:00:00Z"),
                "system.admin",
                0L
        );
    }

    private TreatmentStrategyRequest request() {
        return new TreatmentStrategyRequest("MITIGATE", "Mitigate", "ACTIVE");
    }

    private static final class FixedCurrentUserProvider extends CurrentUserProvider {
        private final String username;

        private FixedCurrentUserProvider(String username) {
            this.username = username;
        }

        @Override
        public String currentUsername() {
            return username;
        }
    }
}
