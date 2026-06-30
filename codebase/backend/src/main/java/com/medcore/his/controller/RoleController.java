package com.medcore.his.controller;

import com.medcore.his.dto.RoleMatrixDTO;
import com.medcore.his.dto.RolePermissionsUpdateRequest;
import com.medcore.his.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/matrix")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleMatrixDTO>> getRoleMatrix() {
        return ResponseEntity.ok(roleService.getRoleMatrix());
    }

    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleMatrixDTO> updateRolePermissions(
            @PathVariable UUID roleId,
            @RequestBody RolePermissionsUpdateRequest request) {
        return ResponseEntity.ok(roleService.updateRolePermissions(roleId, request));
    }
}
