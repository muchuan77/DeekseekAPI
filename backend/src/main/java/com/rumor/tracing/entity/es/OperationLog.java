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
@Document(indexName = "operation_logs")
public class OperationLog {
    
    @Id
    private String id;

    @Field(type = FieldType.Date)
    private LocalDateTime timestamp;

    @Field(type = FieldType.Keyword)
    private String username;

    @Field(type = FieldType.Keyword)
    private String method;

    @Field(type = FieldType.Keyword)
    private String className;

    @Field(type = FieldType.Text)
    private String parameters;

    @Field(type = FieldType.Text)
    private String result;

    @Field(type = FieldType.Text)
    private String error;

    @Field(type = FieldType.Keyword)
    private String ipAddress;

    @Field(type = FieldType.Keyword)
    private String userAgent;

    @Field(type = FieldType.Long)
    private long duration;
} 