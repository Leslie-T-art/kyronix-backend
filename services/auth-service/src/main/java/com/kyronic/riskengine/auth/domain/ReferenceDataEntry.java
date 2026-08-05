package com.kyronic.riskengine.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "reference_data_entries")
public class ReferenceDataEntry {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReferenceDataType type;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean active;

    protected ReferenceDataEntry() {
    }

    public ReferenceDataEntry(UUID id, ReferenceDataType type, String code, String name, boolean active) {
        this.id = id;
        this.type = type;
        this.code = code;
        this.name = name;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public ReferenceDataType getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public void update(String code, String name, boolean active) {
        this.code = code;
        this.name = name;
        this.active = active;
    }
}
