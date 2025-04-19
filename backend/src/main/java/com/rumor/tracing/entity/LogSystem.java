package com.rumor.tracing.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Document(indexName = "system_logs")
public class LogSystem {
    @Id
    private String id;
    
    @Field(type = FieldType.Date)
    private LocalDateTime logTime;
    
    @Field(type = FieldType.Keyword)
    private String level;
    
    @Field(type = FieldType.Text)
    private String message;
    
    @Field(type = FieldType.Text)
    private String stackTrace;
    
    @Field(type = FieldType.Keyword)
    private String applicationName;
    
    @Field(type = FieldType.Long)
    private Long responseTime;
    
    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;

    @Field(type = FieldType.Keyword)
    private String createdBy;

    @Field(type = FieldType.Keyword)
    private String updatedBy;

    @Field(type = FieldType.Integer)
    private Integer version;
} 