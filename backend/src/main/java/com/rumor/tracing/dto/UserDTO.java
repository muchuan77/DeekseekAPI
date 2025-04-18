package com.rumor.tracing.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String phone;
    private List<String> roleIds;
    private Boolean enabled;
} 