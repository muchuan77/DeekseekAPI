package com.rumor.tracing.dto;

import lombok.Data;

@Data
public class PermissionDTO {
    private String id;
    private String name;
    private String code;
    private String description;
    private String type;
    private String parentId;
    private Boolean enabled;
} 