package com.rumor.tracing.service;

import com.rumor.tracing.dto.request.AuthRequest;
import com.rumor.tracing.dto.request.RegisterRequest;
import com.rumor.tracing.dto.response.AuthResponse;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(HttpServletRequest request);
}
