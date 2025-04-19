package com.rumor.tracing.controller;

import com.rumor.tracing.dto.UserDTO;
import com.rumor.tracing.dto.response.ApiResponse;
import com.rumor.tracing.entity.User;
import com.rumor.tracing.entity.User.UserStatus;
import com.rumor.tracing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持关键字搜索和状态筛选")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getUsers(
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "用户状态") @RequestParam(required = false) UserStatus status,
            Pageable pageable) {
        
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.or(
                    cb.like(root.get("username"), "%" + keyword + "%"),
                    cb.like(root.get("fullName"), "%" + keyword + "%"),
                    cb.like(root.get("email"), "%" + keyword + "%")
                ));
            }
            
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return ResponseEntity.ok(ApiResponse.success(userService.getUsers(spec, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详细信息")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @Parameter(description = "用户信息") @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(ApiResponse.success(userService.createUser(userDTO)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新指定用户的信息")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "用户信息") @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, userDTO)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除指定用户")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态", description = "更新指定用户的状态")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "用户状态") @RequestParam UserStatus status) {
        userService.updateUserStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "更新用户角色", description = "更新指定用户的角色")
    public ResponseEntity<ApiResponse<Void>> updateUserRoles(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "角色列表") @RequestBody List<String> roles) {
        userService.updateUserRoles(id, roles);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "重置密码", description = "重置指定用户的密码")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        userService.resetPassword(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/change-password")
    @Operation(summary = "修改密码", description = "修改当前用户的密码")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "旧密码") @RequestParam String oldPassword,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        userService.changePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/batch/status")
    @Operation(summary = "批量更新状态", description = "批量更新多个用户的状态")
    public ResponseEntity<ApiResponse<Void>> batchUpdateStatus(
            @Parameter(description = "用户ID列表") @RequestBody List<Long> ids,
            @Parameter(description = "用户状态") @RequestParam UserStatus status) {
        userService.batchUpdateStatus(ids, status);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/batch/delete")
    @Operation(summary = "批量删除", description = "批量删除多个用户")
    public ResponseEntity<ApiResponse<Void>> batchDelete(
            @Parameter(description = "用户ID列表") @RequestBody List<Long> ids) {
        userService.batchDelete(ids);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/import")
    @Operation(summary = "导入用户", description = "从文件导入用户数据")
    public ResponseEntity<ApiResponse<Void>> importUsers(
            @Parameter(description = "用户数据文件") @RequestParam("file") MultipartFile file) {
        // TODO: Implement file parsing and user import
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/export")
    @Operation(summary = "导出用户", description = "导出用户数据")
    public ResponseEntity<ApiResponse<List<UserDTO>>> exportUsers(
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "用户状态") @RequestParam(required = false) UserStatus status) {
        
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.or(
                    cb.like(root.get("username"), "%" + keyword + "%"),
                    cb.like(root.get("fullName"), "%" + keyword + "%"),
                    cb.like(root.get("email"), "%" + keyword + "%")
                ));
            }
            
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return ResponseEntity.ok(ApiResponse.success(userService.exportUsers(spec)));
    }

    @PostMapping("/{id}/lock")
    @Operation(summary = "锁定用户", description = "锁定指定用户")
    public ResponseEntity<ApiResponse<Void>> lockUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        userService.lockUser(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/unlock")
    @Operation(summary = "解锁用户", description = "解锁指定用户")
    public ResponseEntity<ApiResponse<Void>> unlockUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        userService.unlockUser(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户", description = "获取当前登录用户的信息")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        return ResponseEntity.ok(ApiResponse.success(userService.getCurrentUser()));
    }
} 