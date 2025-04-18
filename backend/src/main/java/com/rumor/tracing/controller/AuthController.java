package com.rumor.tracing.controller;

import com.rumor.tracing.dto.request.AuthRequest;
import com.rumor.tracing.dto.request.RegisterRequest;
import com.rumor.tracing.dto.response.AuthResponse;
import com.rumor.tracing.dto.response.ApiResponse;
import com.rumor.tracing.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "注册成功")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "用户名已存在")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse authResponse = authService.register(request);
            return ResponseEntity.ok(ApiResponse.success(authResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "用户名或密码错误")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse authResponse = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success(authResponse));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, e.getMessage()));
        }
    }

    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            AuthResponse authResponse = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(ApiResponse.success(authResponse));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, e.getMessage()));
        }
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        try {
            authService.logout(request);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }
} 