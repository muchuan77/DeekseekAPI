package com.rumor.tracing.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogResponseDTO {
    private String id;
    private String level;
    private String message;
    private String type;
    private LocalDateTime timestamp;
    private String username;
    private String ip;
    private String userAgent;
} 