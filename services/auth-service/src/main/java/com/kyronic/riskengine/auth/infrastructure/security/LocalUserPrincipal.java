package com.kyronic.riskengine.auth.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class LocalUserPrincipal extends User {

    private final UUID userId;
    private final String fullName;
    private final UUID departmentId;
    private final UUID branchId;
    private final Set<String> roles;
    private final Set<String> permissions;

    public LocalUserPrincipal(UUID userId,
                              String username,
                              String password,
                              boolean enabled,
                              boolean accountNonLocked,
                              String fullName,
                              UUID departmentId,
                              UUID branchId,
                              Set<String> roles,
                              Set<String> permissions,
                              Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, accountNonLocked, authorities);
        this.userId = userId;
        this.fullName = fullName;
        this.departmentId = departmentId;
        this.branchId = branchId;
        this.roles = roles;
        this.permissions = permissions;
    }

    public UUID getUserId() {
        return userId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public String getFullName() {
        return fullName;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public UUID getBranchId() {
        return branchId;
    }

    public Set<String> getPermissions() {
        return permissions;
    }
}
