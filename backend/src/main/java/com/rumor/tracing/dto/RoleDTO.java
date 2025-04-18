package com.rumor.tracing.dto;

import lombok.Data;
import java.util.List;

@Data
public class RoleDTO {
    private String id;
    private String name;
    private String code;
    private String description;
    private List<String> permissionIds;
    private Boolean enabled;
}