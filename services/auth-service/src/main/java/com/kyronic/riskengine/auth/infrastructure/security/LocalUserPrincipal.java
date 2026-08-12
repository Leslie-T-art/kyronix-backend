package com.kyronic.riskengine.auth.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;

public class LocalUserPrincipal extends User {

    private final Long userId;
    private final String fullName;
    private final Long departmentId;
    private final Long branchId;
    private final Set<String> roles;
    private final Set<String> permissions;

    public LocalUserPrincipal(Long userId,
                              String username,
                              String password,
                              boolean enabled,
                              boolean accountNonLocked,
                              String fullName,
                              Long departmentId,
                              Long branchId,
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

    public Long getUserId() {
        return userId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public String getFullName() {
        return fullName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public Set<String> getPermissions() {
        return permissions;
    }
}
