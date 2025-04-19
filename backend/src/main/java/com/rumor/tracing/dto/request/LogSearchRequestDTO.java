package com.rumor.tracing.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogSearchRequestDTO {
    private String keyword;
    private String level;
    private String type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int page;
    private int size;
} 