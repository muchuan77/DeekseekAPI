package com.rumor.tracing.controller;

import com.rumor.tracing.dto.request.PasswordChangeRequest;
import com.rumor.tracing.dto.request.UserCreateRequest;
import com.rumor.tracing.dto.request.UserUpdateRequest;
import com.rumor.tracing.dto.response.ApiResponse;
import com.rumor.tracing.dto.response.PageResponse;
import com.rumor.tracing.dto.response.UserResponse;
import com.rumor.tracing.entity.User;
import com.rumor.tracing.entity.User.UserStatus;
import com.rumor.tracing.security.annotation.RequiresAdmin;
import com.rumor.tracing.security.annotation.RequiresModerator;
import com.rumor.tracing.security.annotation.RequiresUser;
import com.rumor.tracing.service.OperationLogService;
import com.rumor.tracing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final OperationLogService operationLogService;

    @PostMapping
    @RequiresAdmin
    @Operation(summary = "创建用户", description = "创建新用户")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "创建成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "用户名已存在"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限")
    })
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户名已存在"));
        }
        UserResponse user = userService.createUser(request);
        operationLogService.logOperation("CREATE_USER", "创建用户: " + request.getUsername());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping
    @RequiresModerator
    @Operation(summary = "获取用户列表", description = "分页获取用户列表")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限")
    })
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsers(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "用户状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        Page<UserResponse> users = userService.getUsers(keyword, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(users)));
    }

    @GetMapping("/{id}")
    @RequiresUser
    @Operation(summary = "获取用户详情", description = "获取指定用户的详细信息")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @Parameter(description = "用户ID") @PathVariable Long id, 
            Authentication authentication) {
        if (!isAdminOrModerator(authentication) && !isSelf(id, authentication)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("没有权限查看该用户信息"));
        }
        UserResponse user = userService.getUser(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/{id}")
    @RequiresUser
    @Operation(summary = "更新用户信息", description = "更新指定用户的信息")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication) {
        if (!isAdmin(authentication) && !isSelf(id, authentication)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("没有权限修改该用户信息"));
        }
        UserResponse user = userService.updateUser(id, request);
        operationLogService.logOperation("UPDATE_USER", "更新用户: " + user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @DeleteMapping("/{id}")
    @RequiresAdmin
    @Operation(summary = "删除用户", description = "删除指定用户")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "删除成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        userService.deleteUser(id);
        operationLogService.logOperation("DELETE_USER", "删除用户ID: " + id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }

    @PostMapping("/{id}/reset-password")
    @RequiresAdmin
    @Operation(summary = "重置用户密码", description = "重置指定用户的密码")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "重置成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        userService.resetPassword(id);
        operationLogService.logOperation("RESET_PASSWORD", "重置用户密码: " + id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }

    @PostMapping("/{id}/change-password")
    @RequiresUser
    @Operation(summary = "修改密码", description = "修改当前用户的密码")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "修改成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限")
    })
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody PasswordChangeRequest request,
            Authentication authentication) {
        if (!isSelf(id, authentication)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("没有权限修改该用户密码"));
        }
        userService.changePassword(id, request.getNewPassword());
        operationLogService.logOperation("CHANGE_PASSWORD", "修改用户密码: " + id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }

    @PostMapping("/batch-delete")
    @RequiresAdmin
    @Operation(summary = "批量删除用户", description = "批量删除多个用户")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "删除成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限")
    })
    public ResponseEntity<ApiResponse<Void>> batchDeleteUsers(
            @Parameter(description = "用户ID列表") @RequestBody List<Long> userIds) {
        userService.batchDeleteUsers(userIds);
        operationLogService.logOperation("BATCH_DELETE_USERS", "批量删除用户: " + userIds);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }

    @PostMapping("/batch-update-status")
    @RequiresAdmin
    @Operation(summary = "批量更新用户状态", description = "批量更新多个用户的状态")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限")
    })
    public ResponseEntity<ApiResponse<Void>> batchUpdateUserStatus(
            @Valid @RequestBody BatchUpdateStatusRequest request) {
        userService.batchUpdateUserStatus(request.getUserIds(), request.getStatus());
        operationLogService.logOperation("BATCH_UPDATE_STATUS", 
            "批量更新用户状态: " + request.getUserIds() + " -> " + request.getStatus());
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }

    @GetMapping("/export")
    @RequiresAdmin
    @Operation(summary = "导出用户数据", description = "导出所有用户数据到Excel")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "导出成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限")
    })
    public void exportUsers(HttpServletResponse response) throws IOException {
        List<User> users = userService.getAllUsers();
        String filename = "users_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "用户名", "姓名", "邮箱", "状态", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            
            // 填充数据
            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getFullName());
                row.createCell(3).setCellValue(user.getEmail());
                row.createCell(4).setCellValue(user.getStatus().name());
                row.createCell(5).setCellValue(user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            
            workbook.write(response.getOutputStream());
        }
        
        operationLogService.logOperation("导出用户", "导出了 " + users.size() + " 个用户数据");
    }

    @PostMapping("/import")
    @RequiresAdmin
    @Operation(summary = "导入用户数据", description = "从Excel导入用户数据")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "导入成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限")
    })
    public ResponseEntity<ApiResponse<ImportResult>> importUsers(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) throws IOException {
        ImportResult result = new ImportResult();
        List<User> users = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    User user = new User();
                    user.setUsername(row.getCell(1).getStringCellValue());
                    user.setFullName(row.getCell(2).getStringCellValue());
                    user.setEmail(row.getCell(3).getStringCellValue());
                    user.setStatus(UserStatus.valueOf(row.getCell(4).getStringCellValue()));
                    user.setPassword(passwordEncoder.encode("123456")); // 默认密码
                    
                    users.add(user);
                    result.setSuccessCount(result.getSuccessCount() + 1);
                } catch (Exception e) {
                    result.setErrorCount(result.getErrorCount() + 1);
                    result.getErrors().add("第 " + (i + 1) + " 行: " + e.getMessage());
                }
            }
        }
        
        if (!users.isEmpty()) {
            userService.batchCreateUsers(users);
        }
        
        operationLogService.logOperation("导入用户", 
            "成功导入 " + result.getSuccessCount() + " 个用户，失败 " + result.getErrorCount() + " 个");
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isAdminOrModerator(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || 
                                auth.getAuthority().equals("ROLE_MODERATOR"));
    }

    private boolean isSelf(Long userId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return currentUser.getId().equals(userId);
    }

    @Data
    static class BatchUpdateStatusRequest {
        private List<Long> userIds;
        private UserStatus status;
    }

    @Data
    static class ImportResult {
        private int successCount = 0;
        private int errorCount = 0;
        private List<String> errors = new ArrayList<>();
    }
} 