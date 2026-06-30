package com.medcore.his.controller;

import com.medcore.his.domain.auth.Role;
import com.medcore.his.domain.auth.User;
import com.medcore.his.dto.JwtResponse;
import com.medcore.his.dto.LoginRequest;
import com.medcore.his.dto.SignupRequest;
import com.medcore.his.domain.staff.StaffProfile;
import com.medcore.his.repository.RoleRepository;
import com.medcore.his.repository.UserRepository;
import com.medcore.his.repository.StaffProfileRepository;
import com.medcore.his.security.JwtUtils;
import com.medcore.his.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    StaffProfileRepository staffProfileRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getUsername(), // mapping email placeholder for now
                roles,
                userDetails.getPermissions()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPasswordHash(encoder.encode(signUpRequest.getPassword()));
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());

        String strRole = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRole == null || strRole.isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_DOCTOR")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName("ROLE_DOCTOR");
                        role.setDescription("Doctor Role");
                        return roleRepository.save(role);
                    });
            roles.add(userRole);
        } else {
            String roleName = strRole.toUpperCase();
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }
            if (roleName.equals("ROLE_ADMIN")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Error: Registration of administrative roles is not permitted."));
            }
            final String finalRoleName = roleName;
            Role userRole = roleRepository.findByName(finalRoleName)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName(finalRoleName);
                        role.setDescription(finalRoleName.substring(5) + " Role");
                        return roleRepository.save(role);
                    });
            roles.add(userRole);
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        // Auto-create StaffProfile
        StaffProfile profile = new StaffProfile();
        profile.setFullName(signUpRequest.getFirstName() + " " + signUpRequest.getLastName());
        
        String roleStr = strRole != null ? strRole : "DOCTOR";
        if (roleStr.startsWith("ROLE_")) {
            roleStr = roleStr.substring(5);
        }
        roleStr = roleStr.substring(0, 1).toUpperCase() + roleStr.substring(1).toLowerCase();
        profile.setRole(roleStr);
        profile.setDepartment(roleStr.equalsIgnoreCase("doctor") ? "General Medicine" : "General Operations");
        
        long count = staffProfileRepository.count() + 1;
        profile.setEmployeeCode("EMP-" + String.format("%03d", count));
        profile.setEmail(signUpRequest.getEmail());
        profile.setContactNumber("+91-9999988888");
        profile.setActive(true);
        staffProfileRepository.save(profile);

        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }
}
