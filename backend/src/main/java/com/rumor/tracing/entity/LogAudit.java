package com.rumor.tracing.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Document(indexName = "audit_logs")
public class LogAudit {
    @Id
    private String id;
    
    @Field(type = FieldType.Keyword)
    private String username;
    
    @Field(type = FieldType.Keyword)
    private String targetType;
    
    @Field(type = FieldType.Keyword)
    private String targetId;
    
    @Field(type = FieldType.Keyword)
    private String action;
    
    @Field(type = FieldType.Text)
    private String details;
    
    @Field(type = FieldType.Date)
    private LocalDateTime auditTime;
    
    @Field(type = FieldType.Keyword)
    private String ipAddress;
    
    @Field(type = FieldType.Text)
    private String oldValue;
    
    @Field(type = FieldType.Text)
    private String newValue;
} 