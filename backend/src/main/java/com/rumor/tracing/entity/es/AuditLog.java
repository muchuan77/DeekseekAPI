package com.rumor.tracing.entity.es;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@Document(indexName = "audit_logs")
public class AuditLog {
    
    @Id
    private String id;

    @Field(type = FieldType.Date)
    private LocalDateTime timestamp;

    @Field(type = FieldType.Keyword)
    private String username;

    @Field(type = FieldType.Keyword)
    private String action;

    @Field(type = FieldType.Keyword)
    private String entityType;

    @Field(type = FieldType.Keyword)
    private String entityId;

    @Field(type = FieldType.Text)
    private String oldValue;

    @Field(type = FieldType.Text)
    private String newValue;

    @Field(type = FieldType.Keyword)
    private String ipAddress;
} 