package com.rumor.tracing.dto.response;

import com.rumor.tracing.entity.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String status;

    // 新增构造函数
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.status = user.getStatus().name();
    }
}
