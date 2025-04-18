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
@Document(indexName = "system_logs")
public class SystemLog {
    
    @Id
    private String id;

    @Field(type = FieldType.Date)
    private LocalDateTime timestamp;

    @Field(type = FieldType.Keyword)
    private String level;

    @Field(type = FieldType.Keyword)
    private String logger;

    @Field(type = FieldType.Text)
    private String message;

    @Field(type = FieldType.Text)
    private String stackTrace;

    @Field(type = FieldType.Keyword)
    private String thread;
} 