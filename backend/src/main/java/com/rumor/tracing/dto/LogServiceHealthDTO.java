package com.rumor.tracing.dto;

import lombok.Data;

@Data
public class LogServiceHealthDTO {
    private String serviceName;
    private double availability;
    private double responseTime;
    private int errorCount;
} 