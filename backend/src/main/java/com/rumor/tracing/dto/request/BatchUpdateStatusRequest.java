package com.rumor.tracing.dto.request;

import com.rumor.tracing.entity.User.UserStatus;
import lombok.Data;
import java.util.List;

@Data
public class BatchUpdateStatusRequest {
    private List<Long> userIds;
    private UserStatus status;
} 