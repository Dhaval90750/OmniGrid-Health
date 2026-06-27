package com.medcore.his.dto;

import lombok.Data;
import java.util.Map;

@Data
public class RolePermissionsUpdateRequest {
    // Map of ModuleName -> AccessLevel string
    private Map<String, String> permissions;
}
