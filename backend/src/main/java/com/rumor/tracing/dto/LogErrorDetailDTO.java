package com.rumor.tracing.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogErrorDetailDTO {
    private LocalDateTime timestamp;
    private String type;
    private String message;
    private String stackTrace;
} 