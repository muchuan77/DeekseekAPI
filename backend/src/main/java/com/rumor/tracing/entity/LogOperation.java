package com.rumor.tracing.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Document(indexName = "operation_logs")
public class LogOperation {
    @Id
    private String id;
    
    @Field(type = FieldType.Keyword)
    private String username;
    
    @Field(type = FieldType.Keyword)
    private String operationType;
    
    @Field(type = FieldType.Text)
    private String description;
    
    @Field(type = FieldType.Date)
    private LocalDateTime executionTime;
    
    @Field(type = FieldType.Keyword)
    private String status;
    
    @Field(type = FieldType.Text)
    private String details;
    
    @Field(type = FieldType.Keyword)
    private String ipAddress;
    
    @Field(type = FieldType.Keyword)
    private String userAgent;
} 