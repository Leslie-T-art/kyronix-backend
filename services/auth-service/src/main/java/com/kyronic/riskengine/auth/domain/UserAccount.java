package com.kyronic.riskengine.auth.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean locked;

    @Column
    private UUID departmentId;

    @Column
    private UUID branchId;

    @Column(nullable = false)
    private boolean deleted;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name")
    private Set<String> roles = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "permission_name")
    private Set<String> permissions = new HashSet<>();

    protected UserAccount() {
    }

    public UserAccount(UUID id,
                       String username,
                       String fullName,
                       String passwordHash,
                       boolean active,
                       boolean locked,
                       UUID departmentId,
                       UUID branchId,
                       boolean deleted,
                       Set<String> roles,
                       Set<String> permissions) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.active = active;
        this.locked = locked;
        this.departmentId = departmentId;
        this.branchId = branchId;
        this.deleted = deleted;
        this.roles = roles;
        this.permissions = permissions;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isLocked() {
        return locked;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public UUID getBranchId() {
        return branchId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void updateProfile(String username,
                              String fullName,
                              String passwordHash,
                              boolean active,
                              boolean locked,
                              UUID departmentId,
                              UUID branchId,
                              Set<String> roles,
                              Set<String> permissions) {
        this.username = username;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.active = active;
        this.locked = locked;
        this.departmentId = departmentId;
        this.branchId = branchId;
        this.roles = new HashSet<>(roles);
        this.permissions = new HashSet<>(permissions);
    }

    public void markDeleted() {
        this.deleted = true;
        this.active = false;
    }
}
