package com.medcore.his.service;

import com.medcore.his.domain.auth.Permission;
import com.medcore.his.domain.auth.Role;
import com.medcore.his.dto.RoleMatrixDTO;
import com.medcore.his.dto.RolePermissionsUpdateRequest;
import com.medcore.his.repository.PermissionRepository;
import com.medcore.his.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<RoleMatrixDTO> getRoleMatrix() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(role -> {
            RoleMatrixDTO dto = new RoleMatrixDTO();
            dto.setId(role.getId());
            dto.setName(role.getName());
            dto.setDescription(role.getDescription());
            
            Map<String, String> perms = new HashMap<>();
            for (Permission p : role.getPermissions()) {
                if (p.getModule() != null && p.getAccessLevel() != null) {
                    perms.put(p.getModule(), p.getAccessLevel());
                }
            }
            dto.setPermissions(perms);
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public RoleMatrixDTO updateRolePermissions(UUID roleId, RolePermissionsUpdateRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Permission> newPermissions = new HashSet<>();
        
        for (Map.Entry<String, String> entry : request.getPermissions().entrySet()) {
            String module = entry.getKey();
            String accessLevel = entry.getValue();
            
            if (accessLevel.equals("NO_ACCESS")) {
                continue; // No permission entity needed
            }

            Permission p = permissionRepository.findByModuleAndAccessLevel(module, accessLevel)
                    .orElseGet(() -> {
                        Permission newPerm = new Permission();
                        newPerm.setModule(module);
                        newPerm.setAccessLevel(accessLevel);
                        newPerm.setCode("MOD_" + module.toUpperCase().replace(" ", "_") + "_" + accessLevel);
                        return permissionRepository.save(newPerm);
                    });
            newPermissions.add(p);
        }

        role.setPermissions(newPermissions);
        roleRepository.save(role);

        RoleMatrixDTO dto = new RoleMatrixDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setPermissions(request.getPermissions());
        return dto;
    }
}
