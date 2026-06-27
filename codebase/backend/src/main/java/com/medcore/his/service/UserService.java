package com.medcore.his.service;

import com.medcore.his.domain.auth.Role;
import com.medcore.his.domain.auth.User;
import com.medcore.his.dto.UserResponseDTO;
import com.medcore.his.repository.RoleRepository;
import com.medcore.his.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUserRoles(UUID id, List<String> roleNames) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        Set<Role> roles = new HashSet<>();
        
        for (String roleName : roleNames) {
            String name = roleName;
            if (!name.startsWith("ROLE_")) {
                name = "ROLE_" + name;
            }
            Role role = roleRepository.findByName(name).orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }
        
        user.setRoles(roles);
        userRepository.save(user);
        return mapToDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUserStatus(UUID id, boolean active) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(active);
        userRepository.save(user);
        return mapToDTO(user);
    }

    private UserResponseDTO mapToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setActive(user.isActive());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return dto;
    }
}
