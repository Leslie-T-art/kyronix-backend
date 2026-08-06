package com.kyronic.riskengine.olts.application.service;

import com.kyronic.riskengine.olts.application.dto.LossCategoryRequest;
import com.kyronic.riskengine.olts.infrastructure.persistence.LossCategoryJpaEntity;
import com.kyronic.riskengine.olts.infrastructure.persistence.LossCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LossCategoryServiceTest {

    private LossCategoryService service;

    @BeforeEach
    void setUp() {
        service = new LossCategoryService(new InMemoryLossCategoryRepository());
    }

    @Test
    void createsAndListsLossCategories() {
        var created = service.create(new LossCategoryRequest("INT-FRD", "Internal Fraud", "Internal fraud losses"));

        assertThat(created.code()).isEqualTo("INT-FRD");
        assertThat(service.list()).extracting(category -> category.code()).containsExactly("INT-FRD");
    }

    @Test
    void updatesLossCategory() {
        var created = service.create(new LossCategoryRequest("OPS", "Operations", "Operations errors"));

        var updated = service.update(created.id(), new LossCategoryRequest("OPS-1", "Operations Risk", "Operations incidents and losses"));

        assertThat(updated.code()).isEqualTo("OPS-1");
        assertThat(updated.name()).isEqualTo("Operations Risk");
        assertThat(updated.description()).isEqualTo("Operations incidents and losses");
    }

    @Test
    void rejectsUnknownLossCategory() {
        assertThatThrownBy(() -> service.get(UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("loss category not found");
    }

    private static final class InMemoryLossCategoryRepository implements LossCategoryRepository {
        private final Map<UUID, LossCategoryJpaEntity> store = new LinkedHashMap<>();

        @Override
        public List<LossCategoryJpaEntity> findAllByOrderByCodeAsc() {
            return store.values().stream()
                    .sorted(Comparator.comparing(LossCategoryJpaEntity::getCode))
                    .toList();
        }

        @Override
        public Optional<LossCategoryJpaEntity> findByCodeIgnoreCase(String code) {
            return store.values().stream()
                    .filter(category -> category.getCode().equalsIgnoreCase(code))
                    .findFirst();
        }

        @Override
        public <S extends LossCategoryJpaEntity> S save(S entity) {
            store.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public <S extends LossCategoryJpaEntity> List<S> saveAll(Iterable<S> entities) {
            List<S> saved = new ArrayList<>();
            for (S entity : entities) {
                save(entity);
                saved.add(entity);
            }
            return saved;
        }

        @Override
        public Optional<LossCategoryJpaEntity> findById(UUID uuid) {
            return Optional.ofNullable(store.get(uuid));
        }

        @Override
        public boolean existsById(UUID uuid) {
            return store.containsKey(uuid);
        }

        @Override
        public List<LossCategoryJpaEntity> findAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public List<LossCategoryJpaEntity> findAllById(Iterable<UUID> uuids) {
            List<LossCategoryJpaEntity> entities = new ArrayList<>();
            for (UUID id : uuids) {
                if (store.containsKey(id)) {
                    entities.add(store.get(id));
                }
            }
            return entities;
        }

        @Override
        public long count() {
            return store.size();
        }

        @Override
        public void deleteById(UUID uuid) {
            store.remove(uuid);
        }

        @Override
        public void delete(LossCategoryJpaEntity entity) {
            store.remove(entity.getId());
        }

        @Override
        public void deleteAllById(Iterable<? extends UUID> uuids) {
            for (UUID id : uuids) {
                store.remove(id);
            }
        }

        @Override
        public void deleteAll(Iterable<? extends LossCategoryJpaEntity> entities) {
            for (LossCategoryJpaEntity entity : entities) {
                store.remove(entity.getId());
            }
        }

        @Override
        public void deleteAll() {
            store.clear();
        }

        @Override
        public void flush() {
        }

        @Override
        public <S extends LossCategoryJpaEntity> S saveAndFlush(S entity) {
            return save(entity);
        }

        @Override
        public <S extends LossCategoryJpaEntity> List<S> saveAllAndFlush(Iterable<S> entities) {
            return saveAll(entities);
        }

        @Override
        public void deleteAllInBatch(Iterable<LossCategoryJpaEntity> entities) {
            deleteAll(entities);
        }

        @Override
        public void deleteAllByIdInBatch(Iterable<UUID> uuids) {
            deleteAllById(uuids);
        }

        @Override
        public void deleteAllInBatch() {
            deleteAll();
        }

        @Override
        public LossCategoryJpaEntity getOne(UUID uuid) {
            return store.get(uuid);
        }

        @Override
        public LossCategoryJpaEntity getById(UUID uuid) {
            return store.get(uuid);
        }

        @Override
        public LossCategoryJpaEntity getReferenceById(UUID uuid) {
            return store.get(uuid);
        }

        @Override
        public <S extends LossCategoryJpaEntity> Optional<S> findOne(Example<S> example) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends LossCategoryJpaEntity> List<S> findAll(Example<S> example) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends LossCategoryJpaEntity> List<S> findAll(Example<S> example, Sort sort) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends LossCategoryJpaEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends LossCategoryJpaEntity> long count(Example<S> example) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends LossCategoryJpaEntity> boolean exists(Example<S> example) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends LossCategoryJpaEntity, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<LossCategoryJpaEntity> findAll(Sort sort) {
            return findAllByOrderByCodeAsc();
        }

        @Override
        public Page<LossCategoryJpaEntity> findAll(Pageable pageable) {
            throw new UnsupportedOperationException();
        }
    }
}
