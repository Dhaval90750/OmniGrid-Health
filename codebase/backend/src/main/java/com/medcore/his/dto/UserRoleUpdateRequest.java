package com.medcore.his.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserRoleUpdateRequest {
    private List<String> roleNames;
}
