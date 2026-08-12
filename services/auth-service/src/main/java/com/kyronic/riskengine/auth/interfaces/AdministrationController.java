package com.kyronic.riskengine.auth.interfaces;

import com.kyronic.riskengine.auth.application.dto.ReferenceDataRequest;
import com.kyronic.riskengine.auth.application.dto.ReferenceDataResponse;
import com.kyronic.riskengine.auth.application.dto.RoleDefinitionRequest;
import com.kyronic.riskengine.auth.application.dto.RoleDefinitionResponse;
import com.kyronic.riskengine.auth.application.dto.UserResponse;
import com.kyronic.riskengine.auth.application.service.AuditRequestFactory;
import com.kyronic.riskengine.auth.application.service.AuditTrailService;
import com.kyronic.riskengine.auth.application.dto.UserUpsertRequest;
import com.kyronic.riskengine.auth.application.service.AdministrationService;
import com.kyronic.riskengine.auth.domain.ReferenceDataType;
import com.kyronic.riskengine.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Administration", description = "Users, roles, departments, branches, loss categories, and event types.")
public class AdministrationController {

    private final AdministrationService administrationService;
    private final AuditTrailService auditTrailService;
    private final AuditRequestFactory auditRequestFactory;

    public AdministrationController(AdministrationService administrationService,
                                    AuditTrailService auditTrailService,
                                    AuditRequestFactory auditRequestFactory) {
        this.administrationService = administrationService;
        this.auditTrailService = auditTrailService;
        this.auditRequestFactory = auditRequestFactory;
    }

    @GetMapping("/users")
    @Operation(summary = "List users")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_USERS')")
    public ApiResponse<List<UserResponse>> listUsers(Authentication authentication, HttpServletRequest request) {
        List<UserResponse> response = administrationService.listUsers();
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_USERS_VIEWED", "LIST_USERS", "USER_ACCOUNT", null, null, "SUCCESS", null, null, response));
        return ApiResponse.success("Users retrieved successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_USERS')")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserUpsertRequest userRequest,
                                                Authentication authentication,
                                                HttpServletRequest request) {
        UserResponse response = administrationService.createUser(userRequest);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_USER_CREATED", "CREATE_USER", "USER_ACCOUNT", response.id().toString(), response.username(), "SUCCESS", null, null, response));
        return ApiResponse.success("User created successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_USERS')")
    public ApiResponse<UserResponse> updateUser(@PathVariable("id") Long id,
                                                @Valid @RequestBody UserUpsertRequest userRequest,
                                                Authentication authentication,
                                                HttpServletRequest request) {
        UserResponse previous = administrationService.getUser(id);
        UserResponse response = administrationService.updateUser(id, userRequest);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_USER_UPDATED", "UPDATE_USER", "USER_ACCOUNT", response.id().toString(), response.username(), "SUCCESS", null, previous, response));
        return ApiResponse.success("User updated successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Suspend/delete user")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_USERS')")
    public ApiResponse<Void> deleteUser(@PathVariable("id") Long id,
                                        Authentication authentication,
                                        HttpServletRequest request) {
        UserResponse previous = administrationService.getUser(id);
        administrationService.deleteUser(id);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_USER_DELETED", "DELETE_USER", "USER_ACCOUNT", id.toString(), previous.username(), "SUCCESS", null, previous, null));
        return ApiResponse.success("User deleted successfully", null, auditRequestFactory.resolveCorrelationId(request));
    }

    @GetMapping("/roles")
    @Operation(summary = "List roles")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_USERS')")
    public ApiResponse<List<RoleDefinitionResponse>> listRoles(Authentication authentication, HttpServletRequest request) {
        List<RoleDefinitionResponse> response = administrationService.listRoles();
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_ROLES_VIEWED", "LIST_ROLES", "ROLE_DEFINITION", null, null, "SUCCESS", null, null, response));
        return ApiResponse.success("Roles retrieved successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create role")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_USERS')")
    public ApiResponse<RoleDefinitionResponse> createRole(@Valid @RequestBody RoleDefinitionRequest roleRequest,
                                                          Authentication authentication,
                                                          HttpServletRequest request) {
        RoleDefinitionResponse response = administrationService.createRole(roleRequest);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_ROLE_CREATED", "CREATE_ROLE", "ROLE_DEFINITION", response.id().toString(), response.code(), "SUCCESS", null, null, response));
        return ApiResponse.success("Role created successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @PutMapping("/roles/{id}")
    @Operation(summary = "Update role")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_USERS')")
    public ApiResponse<RoleDefinitionResponse> updateRole(@PathVariable("id") Long id,
                                                          @Valid @RequestBody RoleDefinitionRequest roleRequest,
                                                          Authentication authentication,
                                                          HttpServletRequest request) {
        RoleDefinitionResponse previous = administrationService.getRole(id);
        RoleDefinitionResponse response = administrationService.updateRole(id, roleRequest);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_ROLE_UPDATED", "UPDATE_ROLE", "ROLE_DEFINITION", response.id().toString(), response.code(), "SUCCESS", null, previous, response));
        return ApiResponse.success("Role updated successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @DeleteMapping("/roles/{id}")
    @Operation(summary = "Delete role")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_USERS')")
    public ApiResponse<Void> deleteRole(@PathVariable("id") Long id,
                                        Authentication authentication,
                                        HttpServletRequest request) {
        RoleDefinitionResponse previous = administrationService.getRole(id);
        administrationService.deleteRole(id);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_ROLE_DELETED", "DELETE_ROLE", "ROLE_DEFINITION", id.toString(), previous.code(), "SUCCESS", null, previous, null));
        return ApiResponse.success("Role deleted successfully", null, auditRequestFactory.resolveCorrelationId(request));
    }

    @GetMapping("/departments")
    @Operation(summary = "List departments")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA') or hasRole('DEPARTMENT_HEAD') or hasRole('INPUTTER')")
    public ApiResponse<List<ReferenceDataResponse>> listDepartments(Authentication authentication, HttpServletRequest request) {
        List<ReferenceDataResponse> response = administrationService.listReferenceData(ReferenceDataType.DEPARTMENT);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_DEPARTMENTS_VIEWED", "LIST_DEPARTMENTS", "REFERENCE_DATA", null, "DEPARTMENT", "SUCCESS", null, null, response));
        return ApiResponse.success("Departments retrieved successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @PostMapping("/departments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create department")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA')")
    public ApiResponse<ReferenceDataResponse> createDepartment(@Valid @RequestBody ReferenceDataRequest dataRequest,
                                                               Authentication authentication,
                                                               HttpServletRequest request) {
        ReferenceDataResponse response = administrationService.createReferenceData(ReferenceDataType.DEPARTMENT, dataRequest);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_DEPARTMENT_CREATED", "CREATE_DEPARTMENT", "REFERENCE_DATA", response.id().toString(), response.code(), "SUCCESS", null, null, response));
        return ApiResponse.success("Department created successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @PutMapping("/departments/{id}")
    @Operation(summary = "Update department")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA')")
    public ApiResponse<ReferenceDataResponse> updateDepartment(@PathVariable("id") Long id,
                                                               @Valid @RequestBody ReferenceDataRequest dataRequest,
                                                               Authentication authentication,
                                                               HttpServletRequest request) {
        ReferenceDataResponse previous = administrationService.getReferenceData(id);
        ReferenceDataResponse response = administrationService.updateReferenceData(id, dataRequest);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_DEPARTMENT_UPDATED", "UPDATE_DEPARTMENT", "REFERENCE_DATA", response.id().toString(), response.code(), "SUCCESS", null, previous, response));
        return ApiResponse.success("Department updated successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @DeleteMapping("/departments/{id}")
    @Operation(summary = "Delete department")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA')")
    public ApiResponse<Void> deleteDepartment(@PathVariable("id") Long id,
                                              Authentication authentication,
                                              HttpServletRequest request) {
        ReferenceDataResponse previous = administrationService.getReferenceData(id);
        administrationService.deleteReferenceData(id);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_DEPARTMENT_DELETED", "DELETE_DEPARTMENT", "REFERENCE_DATA", id.toString(), previous.code(), "SUCCESS", null, previous, null));
        return ApiResponse.success("Department deleted successfully", null, auditRequestFactory.resolveCorrelationId(request));
    }

    @GetMapping("/branches")
    @Operation(summary = "List branches")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA') or hasRole('DEPARTMENT_HEAD') or hasRole('INPUTTER')")
    public ApiResponse<List<ReferenceDataResponse>> listBranches(Authentication authentication, HttpServletRequest request) {
        List<ReferenceDataResponse> response = administrationService.listReferenceData(ReferenceDataType.BRANCH);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_BRANCHES_VIEWED", "LIST_BRANCHES", "REFERENCE_DATA", null, "BRANCH", "SUCCESS", null, null, response));
        return ApiResponse.success("Branches retrieved successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @PostMapping("/branches")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create branch")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA')")
    public ApiResponse<ReferenceDataResponse> createBranch(@Valid @RequestBody ReferenceDataRequest dataRequest,
                                                           Authentication authentication,
                                                           HttpServletRequest request) {
        ReferenceDataResponse response = administrationService.createReferenceData(ReferenceDataType.BRANCH, dataRequest);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_BRANCH_CREATED", "CREATE_BRANCH", "REFERENCE_DATA", response.id().toString(), response.code(), "SUCCESS", null, null, response));
        return ApiResponse.success("Branch created successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @PutMapping("/branches/{id}")
    @Operation(summary = "Update branch")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA')")
    public ApiResponse<ReferenceDataResponse> updateBranch(@PathVariable("id") Long id,
                                                           @Valid @RequestBody ReferenceDataRequest dataRequest,
                                                           Authentication authentication,
                                                           HttpServletRequest request) {
        ReferenceDataResponse previous = administrationService.getReferenceData(id);
        ReferenceDataResponse response = administrationService.updateReferenceData(id, dataRequest);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_BRANCH_UPDATED", "UPDATE_BRANCH", "REFERENCE_DATA", response.id().toString(), response.code(), "SUCCESS", null, previous, response));
        return ApiResponse.success("Branch updated successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @DeleteMapping("/branches/{id}")
    @Operation(summary = "Delete branch")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA')")
    public ApiResponse<Void> deleteBranch(@PathVariable("id") Long id,
                                          Authentication authentication,
                                          HttpServletRequest request) {
        ReferenceDataResponse previous = administrationService.getReferenceData(id);
        administrationService.deleteReferenceData(id);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_BRANCH_DELETED", "DELETE_BRANCH", "REFERENCE_DATA", id.toString(), previous.code(), "SUCCESS", null, previous, null));
        return ApiResponse.success("Branch deleted successfully", null, auditRequestFactory.resolveCorrelationId(request));
    }

    @GetMapping("/loss-categories")
    @Operation(summary = "List loss categories")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA') or hasRole('DEPARTMENT_HEAD') or hasRole('INPUTTER')")
    public ApiResponse<List<ReferenceDataResponse>> listLossCategories(Authentication authentication, HttpServletRequest request) {
        List<ReferenceDataResponse> response = administrationService.listReferenceData(ReferenceDataType.LOSS_CATEGORY);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_LOSS_CATEGORIES_VIEWED", "LIST_LOSS_CATEGORIES", "REFERENCE_DATA", null, "LOSS_CATEGORY", "SUCCESS", null, null, response));
        return ApiResponse.success("Loss categories retrieved successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @PostMapping("/loss-categories")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create loss category")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA')")
    public ApiResponse<ReferenceDataResponse> createLossCategory(@Valid @RequestBody ReferenceDataRequest dataRequest,
                                                                 Authentication authentication,
                                                                 HttpServletRequest request) {
        ReferenceDataResponse response = administrationService.createReferenceData(ReferenceDataType.LOSS_CATEGORY, dataRequest);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_LOSS_CATEGORY_CREATED", "CREATE_LOSS_CATEGORY", "REFERENCE_DATA", response.id().toString(), response.code(), "SUCCESS", null, null, response));
        return ApiResponse.success("Loss category created successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @PutMapping("/loss-categories/{id}")
    @Operation(summary = "Update loss category")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA')")
    public ApiResponse<ReferenceDataResponse> updateLossCategory(@PathVariable("id") Long id,
                                                                 @Valid @RequestBody ReferenceDataRequest dataRequest,
                                                                 Authentication authentication,
                                                                 HttpServletRequest request) {
        ReferenceDataResponse previous = administrationService.getReferenceData(id);
        ReferenceDataResponse response = administrationService.updateReferenceData(id, dataRequest);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_LOSS_CATEGORY_UPDATED", "UPDATE_LOSS_CATEGORY", "REFERENCE_DATA", response.id().toString(), response.code(), "SUCCESS", null, previous, response));
        return ApiResponse.success("Loss category updated successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @DeleteMapping("/loss-categories/{id}")
    @Operation(summary = "Delete loss category")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA')")
    public ApiResponse<Void> deleteLossCategory(@PathVariable("id") Long id,
                                                Authentication authentication,
                                                HttpServletRequest request) {
        ReferenceDataResponse previous = administrationService.getReferenceData(id);
        administrationService.deleteReferenceData(id);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_LOSS_CATEGORY_DELETED", "DELETE_LOSS_CATEGORY", "REFERENCE_DATA", id.toString(), previous.code(), "SUCCESS", null, previous, null));
        return ApiResponse.success("Loss category deleted successfully", null, auditRequestFactory.resolveCorrelationId(request));
    }

    @GetMapping("/event-types")
    @Operation(summary = "List event types")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA') or hasRole('DEPARTMENT_HEAD') or hasRole('INPUTTER')")
    public ApiResponse<List<ReferenceDataResponse>> listEventTypes(Authentication authentication, HttpServletRequest request) {
        List<ReferenceDataResponse> response = administrationService.listReferenceData(ReferenceDataType.EVENT_TYPE);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_EVENT_TYPES_VIEWED", "LIST_EVENT_TYPES", "REFERENCE_DATA", null, "EVENT_TYPE", "SUCCESS", null, null, response));
        return ApiResponse.success("Event types retrieved successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @PostMapping("/event-types")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create event type")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA')")
    public ApiResponse<ReferenceDataResponse> createEventType(@Valid @RequestBody ReferenceDataRequest dataRequest,
                                                              Authentication authentication,
                                                              HttpServletRequest request) {
        ReferenceDataResponse response = administrationService.createReferenceData(ReferenceDataType.EVENT_TYPE, dataRequest);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_EVENT_TYPE_CREATED", "CREATE_EVENT_TYPE", "REFERENCE_DATA", response.id().toString(), response.code(), "SUCCESS", null, null, response));
        return ApiResponse.success("Event type created successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @PutMapping("/event-types/{id}")
    @Operation(summary = "Update event type")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA')")
    public ApiResponse<ReferenceDataResponse> updateEventType(@PathVariable("id") Long id,
                                                              @Valid @RequestBody ReferenceDataRequest dataRequest,
                                                              Authentication authentication,
                                                              HttpServletRequest request) {
        ReferenceDataResponse previous = administrationService.getReferenceData(id);
        ReferenceDataResponse response = administrationService.updateReferenceData(id, dataRequest);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_EVENT_TYPE_UPDATED", "UPDATE_EVENT_TYPE", "REFERENCE_DATA", response.id().toString(), response.code(), "SUCCESS", null, previous, response));
        return ApiResponse.success("Event type updated successfully", response, auditRequestFactory.resolveCorrelationId(request));
    }

    @DeleteMapping("/event-types/{id}")
    @Operation(summary = "Delete event type")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_REFERENCE_DATA')")
    public ApiResponse<Void> deleteEventType(@PathVariable("id") Long id,
                                             Authentication authentication,
                                             HttpServletRequest request) {
        ReferenceDataResponse previous = administrationService.getReferenceData(id);
        administrationService.deleteReferenceData(id);
        auditTrailService.record(auditRequestFactory.create(authentication, request, "ADMIN_EVENT_TYPE_DELETED", "DELETE_EVENT_TYPE", "REFERENCE_DATA", id.toString(), previous.code(), "SUCCESS", null, previous, null));
        return ApiResponse.success("Event type deleted successfully", null, auditRequestFactory.resolveCorrelationId(request));
    }
}
