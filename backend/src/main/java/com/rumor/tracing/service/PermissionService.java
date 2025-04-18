package com.rumor.tracing.service;

import com.rumor.tracing.dto.PermissionDTO;
import com.rumor.tracing.dto.RoleDTO;
import java.util.List;

public interface PermissionService {
    // 权限管理
    List<PermissionDTO> getPermissions();
    PermissionDTO createPermission(PermissionDTO permission);
    PermissionDTO updatePermission(String id, PermissionDTO permission);
    void deletePermission(String id);
    
    // 角色管理
    List<RoleDTO> getRoles();
    RoleDTO createRole(RoleDTO role);
    RoleDTO updateRole(String id, RoleDTO role);
    void deleteRole(String id);
    
    // 权限分配
    void assignPermission(String roleId, String permissionId);
    List<RoleDTO> getUserRoles(String userId);
    void updateUserRoles(String userId, List<String> roleIds);
    List<PermissionDTO> getRolePermissions(String roleId);
    void updateRolePermissions(String roleId, List<String> permissionIds);
} 