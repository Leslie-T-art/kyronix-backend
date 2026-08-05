package com.kyronic.riskengine.auth.interfaces;

import com.kyronic.riskengine.auth.application.dto.ReferenceDataRequest;
import com.kyronic.riskengine.auth.application.dto.ReferenceDataResponse;
import com.kyronic.riskengine.auth.application.dto.RoleDefinitionRequest;
import com.kyronic.riskengine.auth.application.dto.RoleDefinitionResponse;
import com.kyronic.riskengine.auth.application.dto.UserResponse;
import com.kyronic.riskengine.auth.application.dto.UserUpsertRequest;
import com.kyronic.riskengine.auth.application.service.AdministrationService;
import com.kyronic.riskengine.auth.domain.ReferenceDataType;
import com.kyronic.riskengine.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Administration", description = "Users, roles, departments, branches, loss categories, and event types.")
public class AdministrationController {

    private final AdministrationService administrationService;

    public AdministrationController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @GetMapping("/users")
    @Operation(summary = "List users")
    public ApiResponse<List<UserResponse>> listUsers() {
        return ApiResponse.success("Users retrieved successfully", administrationService.listUsers(), correlationId());
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserUpsertRequest request) {
        return ApiResponse.success("User created successfully", administrationService.createUser(request), correlationId());
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user")
    public ApiResponse<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpsertRequest request) {
        return ApiResponse.success("User updated successfully", administrationService.updateUser(id, request), correlationId());
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Suspend/delete user")
    public ApiResponse<Void> deleteUser(@PathVariable UUID id) {
        administrationService.deleteUser(id);
        return ApiResponse.success("User deleted successfully", null, correlationId());
    }

    @GetMapping("/roles")
    @Operation(summary = "List roles")
    public ApiResponse<List<RoleDefinitionResponse>> listRoles() {
        return ApiResponse.success("Roles retrieved successfully", administrationService.listRoles(), correlationId());
    }

    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create role")
    public ApiResponse<RoleDefinitionResponse> createRole(@Valid @RequestBody RoleDefinitionRequest request) {
        return ApiResponse.success("Role created successfully", administrationService.createRole(request), correlationId());
    }

    @PutMapping("/roles/{id}")
    @Operation(summary = "Update role")
    public ApiResponse<RoleDefinitionResponse> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleDefinitionRequest request) {
        return ApiResponse.success("Role updated successfully", administrationService.updateRole(id, request), correlationId());
    }

    @DeleteMapping("/roles/{id}")
    @Operation(summary = "Delete role")
    public ApiResponse<Void> deleteRole(@PathVariable UUID id) {
        administrationService.deleteRole(id);
        return ApiResponse.success("Role deleted successfully", null, correlationId());
    }

    @GetMapping("/departments")
    @Operation(summary = "List departments")
    public ApiResponse<List<ReferenceDataResponse>> listDepartments() {
        return ApiResponse.success("Departments retrieved successfully", administrationService.listReferenceData(ReferenceDataType.DEPARTMENT), correlationId());
    }

    @PostMapping("/departments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create department")
    public ApiResponse<ReferenceDataResponse> createDepartment(@Valid @RequestBody ReferenceDataRequest request) {
        return ApiResponse.success("Department created successfully", administrationService.createReferenceData(ReferenceDataType.DEPARTMENT, request), correlationId());
    }

    @PutMapping("/departments/{id}")
    @Operation(summary = "Update department")
    public ApiResponse<ReferenceDataResponse> updateDepartment(@PathVariable UUID id, @Valid @RequestBody ReferenceDataRequest request) {
        return ApiResponse.success("Department updated successfully", administrationService.updateReferenceData(id, request), correlationId());
    }

    @DeleteMapping("/departments/{id}")
    @Operation(summary = "Delete department")
    public ApiResponse<Void> deleteDepartment(@PathVariable UUID id) {
        administrationService.deleteReferenceData(id);
        return ApiResponse.success("Department deleted successfully", null, correlationId());
    }

    @GetMapping("/branches")
    @Operation(summary = "List branches")
    public ApiResponse<List<ReferenceDataResponse>> listBranches() {
        return ApiResponse.success("Branches retrieved successfully", administrationService.listReferenceData(ReferenceDataType.BRANCH), correlationId());
    }

    @PostMapping("/branches")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create branch")
    public ApiResponse<ReferenceDataResponse> createBranch(@Valid @RequestBody ReferenceDataRequest request) {
        return ApiResponse.success("Branch created successfully", administrationService.createReferenceData(ReferenceDataType.BRANCH, request), correlationId());
    }

    @PutMapping("/branches/{id}")
    @Operation(summary = "Update branch")
    public ApiResponse<ReferenceDataResponse> updateBranch(@PathVariable UUID id, @Valid @RequestBody ReferenceDataRequest request) {
        return ApiResponse.success("Branch updated successfully", administrationService.updateReferenceData(id, request), correlationId());
    }

    @DeleteMapping("/branches/{id}")
    @Operation(summary = "Delete branch")
    public ApiResponse<Void> deleteBranch(@PathVariable UUID id) {
        administrationService.deleteReferenceData(id);
        return ApiResponse.success("Branch deleted successfully", null, correlationId());
    }

    @GetMapping("/loss-categories")
    @Operation(summary = "List loss categories")
    public ApiResponse<List<ReferenceDataResponse>> listLossCategories() {
        return ApiResponse.success("Loss categories retrieved successfully", administrationService.listReferenceData(ReferenceDataType.LOSS_CATEGORY), correlationId());
    }

    @PostMapping("/loss-categories")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create loss category")
    public ApiResponse<ReferenceDataResponse> createLossCategory(@Valid @RequestBody ReferenceDataRequest request) {
        return ApiResponse.success("Loss category created successfully", administrationService.createReferenceData(ReferenceDataType.LOSS_CATEGORY, request), correlationId());
    }

    @PutMapping("/loss-categories/{id}")
    @Operation(summary = "Update loss category")
    public ApiResponse<ReferenceDataResponse> updateLossCategory(@PathVariable UUID id, @Valid @RequestBody ReferenceDataRequest request) {
        return ApiResponse.success("Loss category updated successfully", administrationService.updateReferenceData(id, request), correlationId());
    }

    @DeleteMapping("/loss-categories/{id}")
    @Operation(summary = "Delete loss category")
    public ApiResponse<Void> deleteLossCategory(@PathVariable UUID id) {
        administrationService.deleteReferenceData(id);
        return ApiResponse.success("Loss category deleted successfully", null, correlationId());
    }

    @GetMapping("/event-types")
    @Operation(summary = "List event types")
    public ApiResponse<List<ReferenceDataResponse>> listEventTypes() {
        return ApiResponse.success("Event types retrieved successfully", administrationService.listReferenceData(ReferenceDataType.EVENT_TYPE), correlationId());
    }

    @PostMapping("/event-types")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create event type")
    public ApiResponse<ReferenceDataResponse> createEventType(@Valid @RequestBody ReferenceDataRequest request) {
        return ApiResponse.success("Event type created successfully", administrationService.createReferenceData(ReferenceDataType.EVENT_TYPE, request), correlationId());
    }

    @PutMapping("/event-types/{id}")
    @Operation(summary = "Update event type")
    public ApiResponse<ReferenceDataResponse> updateEventType(@PathVariable UUID id, @Valid @RequestBody ReferenceDataRequest request) {
        return ApiResponse.success("Event type updated successfully", administrationService.updateReferenceData(id, request), correlationId());
    }

    @DeleteMapping("/event-types/{id}")
    @Operation(summary = "Delete event type")
    public ApiResponse<Void> deleteEventType(@PathVariable UUID id) {
        administrationService.deleteReferenceData(id);
        return ApiResponse.success("Event type deleted successfully", null, correlationId());
    }

    private String correlationId() {
        return UUID.randomUUID().toString();
    }
}
