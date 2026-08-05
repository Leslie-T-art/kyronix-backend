package com.kyronic.riskengine.olts.infrastructure.persistence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class IncidentSequenceRepository {

    private final JdbcTemplate jdbcTemplate;

    public IncidentSequenceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long nextValue() {
        Long value = jdbcTemplate.queryForObject("select nextval('olts_incident_seq')", Long.class);
        return value == null ? 1L : value;
    }
}
