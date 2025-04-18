package com.rumor.tracing.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation_logs")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OperationLog extends BaseEntity {
    
    @Column(nullable = false, length = 50)
    private String operationType;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 10)
    private String method;
    
    @Column(length = 500)
    private String url;
    
    @Column(columnDefinition = "TEXT")
    private String params;
    
    @Column(columnDefinition = "TEXT")
    private String result;
    
    @Column
    private Long duration;
    
    @Column(length = 50)
    private String ip;
    
    @Column(length = 500)
    private String userAgent;
    
    @Column(length = 50)
    private String username;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "execution_time")
    private LocalDateTime executionTime;

    @PrePersist
    protected void onPrePersist() {
        executionTime = LocalDateTime.now();
    }
} 