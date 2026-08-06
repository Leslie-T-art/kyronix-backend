package com.kyronic.riskengine.auth.application.service;

import com.kyronic.riskengine.auth.application.dto.AuthMeResponse;
import com.kyronic.riskengine.auth.application.dto.AuthorizerCandidateResponse;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public UserResponse getUser(UUID id) {
        return toUserResponse(userAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found")));
    }

    @Transactional(readOnly = true)
    public AuthMeResponse getCurrentUserProfile(String username) {
        UserAccount account = userAccountRepository.findByUsername(username)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Map<UUID, ReferenceDataEntry> referenceDataById = referenceDataEntryRepository.findAllById(
                        List.of(account.getDepartmentId(), account.getBranchId()).stream()
                                .filter(java.util.Objects::nonNull)
                                .toList())
                .stream()
                .collect(Collectors.toMap(ReferenceDataEntry::getId, Function.identity()));

        Map<String, RoleDefinition> rolesByCode = roleDefinitionRepository.findByCodeIn(account.getRoles())
                .stream()
                .collect(Collectors.toMap(RoleDefinition::getCode, Function.identity()));

        return new AuthMeResponse(
                account.getId(),
                account.getUsername(),
                account.getFullName(),
                account.isActive(),
                account.isLocked(),
                toReferenceAssignment(referenceDataById.get(account.getDepartmentId())),
                toReferenceAssignment(referenceDataById.get(account.getBranchId())),
                account.getRoles().stream()
                        .sorted()
                        .map(roleCode -> {
                            RoleDefinition role = rolesByCode.get(roleCode);
                            return new AuthMeResponse.RoleAssignment(
                                    roleCode,
                                    role != null ? role.getName() : roleCode
                            );
                        })
                        .collect(Collectors.toCollection(java.util.LinkedHashSet::new)),
                account.getPermissions().stream()
                        .sorted()
                        .collect(Collectors.toCollection(java.util.LinkedHashSet::new))
        );
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

    @Transactional(readOnly = true)
    public RoleDefinitionResponse getRole(UUID id) {
        return toRoleResponse(roleDefinitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("role not found")));
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

    @Transactional(readOnly = true)
    public ReferenceDataResponse getReferenceData(UUID id) {
        return toReferenceResponse(referenceDataEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reference data not found")));
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

    @Transactional(readOnly = true)
    public List<AuthorizerCandidateResponse> listEligibleAuthorizers(UUID departmentId, String permission) {
        return userAccountRepository.findByDepartmentIdAndActiveTrueAndDeletedFalse(departmentId).stream()
                .filter(account -> !account.isLocked())
                .filter(account -> account.getPermissions().contains(permission))
                .filter(account -> account.getRoles().contains("DEPARTMENT_HEAD") || account.getRoles().contains("AUTHORIZER"))
                .sorted(Comparator.comparing(UserAccount::getUsername, String.CASE_INSENSITIVE_ORDER))
                .map(account -> new AuthorizerCandidateResponse(
                        account.getId(),
                        account.getDepartmentId(),
                        Set.copyOf(account.getPermissions()),
                        account.isActive(),
                        false
                ))
                .filter(candidate -> Objects.nonNull(candidate.userId()))
                .toList();
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

    private AuthMeResponse.ReferenceAssignment toReferenceAssignment(ReferenceDataEntry entry) {
        if (entry == null) {
            return null;
        }
        return new AuthMeResponse.ReferenceAssignment(entry.getId(), entry.getCode(), entry.getName());
    }
}
