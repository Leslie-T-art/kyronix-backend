package com.kyronic.riskengine.auth.application.service;

import com.kyronic.riskengine.auth.application.dto.ReferenceDataRequest;
import com.kyronic.riskengine.auth.application.dto.ReferenceDataResponse;
import com.kyronic.riskengine.auth.application.dto.RoleDefinitionRequest;
import com.kyronic.riskengine.auth.application.dto.RoleDefinitionResponse;
import com.kyronic.riskengine.auth.application.dto.UserResponse;
import com.kyronic.riskengine.auth.application.dto.UserUpsertRequest;
import com.kyronic.riskengine.auth.domain.ReferenceDataEntry;
import com.kyronic.riskengine.auth.domain.ReferenceDataType;
import com.kyronic.riskengine.auth.domain.RoleDefinition;
import com.kyronic.riskengine.auth.domain.UserAccount;
import com.kyronic.riskengine.auth.infrastructure.persistence.ReferenceDataEntryRepository;
import com.kyronic.riskengine.auth.infrastructure.persistence.RoleDefinitionRepository;
import com.kyronic.riskengine.auth.infrastructure.persistence.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class AdministrationService {

    private final UserAccountRepository userAccountRepository;
    private final RoleDefinitionRepository roleDefinitionRepository;
    private final ReferenceDataEntryRepository referenceDataEntryRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministrationService(UserAccountRepository userAccountRepository,
                                 RoleDefinitionRepository roleDefinitionRepository,
                                 ReferenceDataEntryRepository referenceDataEntryRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.roleDefinitionRepository = roleDefinitionRepository;
        this.referenceDataEntryRepository = referenceDataEntryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> listUsers() {
        return userAccountRepository.findAll().stream()
                .filter(account -> !account.isDeleted())
                .map(this::toUserResponse)
                .toList();
    }

    public UserResponse createUser(UserUpsertRequest request) {
        UserAccount userAccount = new UserAccount(
                UUID.randomUUID(),
                request.username(),
                request.fullName(),
                passwordEncoder.encode(request.password()),
                request.active(),
                request.locked(),
                request.departmentId(),
                request.branchId(),
                false,
                Set.copyOf(request.roles()),
                Set.copyOf(request.permissions())
        );
        return toUserResponse(userAccountRepository.save(userAccount));
    }

    public UserResponse updateUser(UUID id, UserUpsertRequest request) {
        UserAccount account = userAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        account.updateProfile(
                request.username(),
                request.fullName(),
                passwordEncoder.encode(request.password()),
                request.active(),
                request.locked(),
                request.departmentId(),
                request.branchId(),
                Set.copyOf(request.roles()),
                Set.copyOf(request.permissions())
        );
        return toUserResponse(userAccountRepository.save(account));
    }

    public void deleteUser(UUID id) {
        UserAccount account = userAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        account.markDeleted();
        userAccountRepository.save(account);
    }

    public List<RoleDefinitionResponse> listRoles() {
        return roleDefinitionRepository.findAll().stream().map(this::toRoleResponse).toList();
    }

    public RoleDefinitionResponse createRole(RoleDefinitionRequest request) {
        RoleDefinition roleDefinition = new RoleDefinition(UUID.randomUUID(), request.code(), request.name(), request.description(), request.active());
        return toRoleResponse(roleDefinitionRepository.save(roleDefinition));
    }

    public RoleDefinitionResponse updateRole(UUID id, RoleDefinitionRequest request) {
        RoleDefinition roleDefinition = roleDefinitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("role not found"));
        roleDefinition.update(request.code(), request.name(), request.description(), request.active());
        return toRoleResponse(roleDefinitionRepository.save(roleDefinition));
    }

    public void deleteRole(UUID id) {
        roleDefinitionRepository.deleteById(id);
    }

    public List<ReferenceDataResponse> listReferenceData(ReferenceDataType type) {
        return referenceDataEntryRepository.findByTypeOrderByCodeAsc(type).stream().map(this::toReferenceResponse).toList();
    }

    public ReferenceDataResponse createReferenceData(ReferenceDataType type, ReferenceDataRequest request) {
        ReferenceDataEntry entry = new ReferenceDataEntry(UUID.randomUUID(), type, request.code(), request.name(), request.active());
        return toReferenceResponse(referenceDataEntryRepository.save(entry));
    }

    public ReferenceDataResponse updateReferenceData(UUID id, ReferenceDataRequest request) {
        ReferenceDataEntry entry = referenceDataEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reference data not found"));
        entry.update(request.code(), request.name(), request.active());
        return toReferenceResponse(referenceDataEntryRepository.save(entry));
    }

    public void deleteReferenceData(UUID id) {
        referenceDataEntryRepository.deleteById(id);
    }

    private UserResponse toUserResponse(UserAccount account) {
        return new UserResponse(
                account.getId(),
                account.getUsername(),
                account.getFullName(),
                account.isActive(),
                account.isLocked(),
                account.getDepartmentId(),
                account.getBranchId(),
                Set.copyOf(account.getRoles()),
                Set.copyOf(account.getPermissions())
        );
    }

    private RoleDefinitionResponse toRoleResponse(RoleDefinition roleDefinition) {
        return new RoleDefinitionResponse(
                roleDefinition.getId(),
                roleDefinition.getCode(),
                roleDefinition.getName(),
                roleDefinition.getDescription(),
                roleDefinition.isActive()
        );
    }

    private ReferenceDataResponse toReferenceResponse(ReferenceDataEntry entry) {
        return new ReferenceDataResponse(entry.getId(), entry.getCode(), entry.getName(), entry.isActive());
    }
}
