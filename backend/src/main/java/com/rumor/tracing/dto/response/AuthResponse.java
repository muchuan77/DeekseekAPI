package com.rumor.tracing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String userId;
    private String username;
    private List<String> roles;
} 