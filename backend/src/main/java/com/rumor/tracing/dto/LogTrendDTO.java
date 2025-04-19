package com.rumor.tracing.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogTrendDTO {
    private LocalDateTime timestamp;
    private long count;
    private String type;
} 