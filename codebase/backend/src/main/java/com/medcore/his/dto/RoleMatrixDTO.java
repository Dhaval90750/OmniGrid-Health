package com.medcore.his.dto;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class RoleMatrixDTO {
    private UUID id;
    private String name;
    private String description;
    // Map of ModuleName -> AccessLevel string
    private Map<String, String> permissions;
}
