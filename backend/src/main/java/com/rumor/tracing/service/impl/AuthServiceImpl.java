package com.rumor.tracing.service.impl;

import com.rumor.tracing.dto.request.AuthRequest;
import com.rumor.tracing.dto.request.RegisterRequest;
import com.rumor.tracing.dto.response.AuthResponse;
import com.rumor.tracing.entity.User;
import com.rumor.tracing.entity.User.UserStatus;
import com.rumor.tracing.exception.BusinessException;
import com.rumor.tracing.repository.UserRepository;
import com.rumor.tracing.security.JwtTokenUtil;
import com.rumor.tracing.service.AuthService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new BusinessException("User not found"));
        String token = jwtTokenUtil.generateToken(user);
        return new AuthResponse(token, user.getId().toString(), user.getUsername(), new ArrayList<>(user.getRoles()));
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone());
        user.setStatus(UserStatus.ENABLED);
        user.setRoles(request.getRoles() != null ? new HashSet<>(request.getRoles()) : Collections.singleton("USER"));

        user = userRepository.save(user);

        String token = jwtTokenUtil.generateToken(user);
        return new AuthResponse(token, user.getId().toString(), user.getUsername(), new ArrayList<>(user.getRoles()));
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        // TODO: 实现刷新令牌逻辑
        return new AuthResponse("newToken", "userId", "username", Collections.emptyList());
    }

    @Override
    public void logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        // TODO: 可以在这里添加其他登出逻辑，比如记录登出日志等
    }
} 