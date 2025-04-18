package com.rumor.tracing.controller;

import com.rumor.tracing.dto.PermissionDTO;
import com.rumor.tracing.dto.RoleDTO;
import com.rumor.tracing.dto.response.ApiResponse;
import com.rumor.tracing.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限和角色管理相关接口")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/permissions")
    @Operation(summary = "获取权限列表")
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getPermissions() {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getPermissions()));
    }

    @PostMapping("/permissions")
    @Operation(summary = "创建权限")
    public ResponseEntity<ApiResponse<PermissionDTO>> createPermission(@RequestBody PermissionDTO permission) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.createPermission(permission)));
    }

    @PutMapping("/permissions/{id}")
    @Operation(summary = "更新权限")
    public ResponseEntity<ApiResponse<PermissionDTO>> updatePermission(@PathVariable String id, @RequestBody PermissionDTO permission) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.updatePermission(id, permission)));
    }

    @DeleteMapping("/permissions/{id}")
    @Operation(summary = "删除权限")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable String id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/roles")
    @Operation(summary = "获取角色列表")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getRoles() {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getRoles()));
    }

    @PostMapping("/roles")
    @Operation(summary = "创建角色")
    public ResponseEntity<ApiResponse<RoleDTO>> createRole(@RequestBody RoleDTO role) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.createRole(role)));
    }

    @PutMapping("/roles/{id}")
    @Operation(summary = "更新角色")
    public ResponseEntity<ApiResponse<RoleDTO>> updateRole(@PathVariable String id, @RequestBody RoleDTO role) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.updateRole(id, role)));
    }

    @DeleteMapping("/roles/{id}")
    @Operation(summary = "删除角色")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable String id) {
        permissionService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/assign")
    @Operation(summary = "分配权限")
    public ResponseEntity<ApiResponse<Void>> assignPermission(@RequestParam String roleId, @RequestParam String permissionId) {
        permissionService.assignPermission(roleId, permissionId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/users/{userId}/roles")
    @Operation(summary = "获取用户角色")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getUserRoles(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getUserRoles(userId)));
    }

    @PutMapping("/users/{userId}/roles")
    @Operation(summary = "更新用户角色")
    public ResponseEntity<ApiResponse<Void>> updateUserRoles(@PathVariable String userId, @RequestBody List<String> roleIds) {
        permissionService.updateUserRoles(userId, roleIds);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/roles/{roleId}/permissions")
    @Operation(summary = "获取角色权限")
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getRolePermissions(@PathVariable String roleId) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getRolePermissions(roleId)));
    }

    @PutMapping("/roles/{roleId}/permissions")
    @Operation(summary = "更新角色权限")
    public ResponseEntity<ApiResponse<Void>> updateRolePermissions(@PathVariable String roleId, @RequestBody List<String> permissionIds) {
        permissionService.updateRolePermissions(roleId, permissionIds);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
} 