package com.rumor.tracing.dto.request;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class UserUpdateRequest {
    @Size(max = 50)
    private String fullName;

    @Size(max = 50)
    @Email
    private String email;

    @Size(min = 6, max = 100)
    private String password;

    private String phoneNumber;
}
