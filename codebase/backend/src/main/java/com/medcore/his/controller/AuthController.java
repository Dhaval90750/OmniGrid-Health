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
        
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (user != null) {
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(java.time.LocalDateTime.now())) {
                return ResponseEntity.status(403).body(Map.of("message", "Account locked. Try again later."));
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            if (user != null) {
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(null);
                user.setLastLoginAt(java.time.LocalDateTime.now());
                userRepository.save(user);
            }

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getUsername(), // mapping email placeholder for now
                    roles,
                    userDetails.getPermissions()));
                    
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            if (user != null) {
                int attempts = user.getFailedLoginAttempts() + 1;
                user.setFailedLoginAttempts(attempts);
                if (attempts >= 5) {
                    user.setLockedUntil(java.time.LocalDateTime.now().plusMinutes(15));
                }
                userRepository.save(user);
            }
            return ResponseEntity.status(401).body(Map.of("message", "Error: Unauthorized"));
        }
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
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Role must be specified."));
        }

        String roleName = strRole.toUpperCase();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        Set<String> allowedRoles = Set.of(
                "ROLE_SUPER_ADMIN", "ROLE_HOSPITAL_ADMIN", "ROLE_DOCTOR", "ROLE_NURSE",
                "ROLE_LAB_TECH", "ROLE_PATHOLOGIST", "ROLE_RADIOLOGIST", "ROLE_PHARMACIST",
                "ROLE_RECEPTIONIST", "ROLE_BILLING_EXEC", "ROLE_INVENTORY_MGR",
                "ROLE_OPERATIONS_MGR", "ROLE_DIETITIAN", "ROLE_MANAGEMENT"
        );

        if (!allowedRoles.contains(roleName)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Invalid role provided."));
        }

        Role userRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found in database. Please run migrations."));
        roles.add(userRole);

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

    @Autowired
    com.medcore.his.repository.EmergencyAccessLogRepository emergencyAccessLogRepository;

    @PostMapping("/emergency-access")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> requestEmergencyAccess(
            @Valid @RequestBody com.medcore.his.dto.EmergencyAccessRequest request,
            jakarta.servlet.http.HttpServletRequest httpServletRequest) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        com.medcore.his.domain.auth.EmergencyAccessLog log = new com.medcore.his.domain.auth.EmergencyAccessLog();
        log.setUserId(userDetails.getId());
        log.setPatientId(request.getPatientId());
        log.setJustificationType(request.getJustificationType());
        log.setJustificationText(request.getJustificationText());
        log.setAccessGrantedAt(java.time.LocalDateTime.now());
        log.setAccessExpiresAt(java.time.LocalDateTime.now().plusMinutes(60));
        log.setIpAddress(httpServletRequest.getRemoteAddr());

        emergencyAccessLogRepository.save(log);

        // Generate a special 60-minute JWT token with emergency flag (in a real system, claims would be updated)
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(Map.of(
            "message", "Emergency access granted for 60 minutes.",
            "token", jwt,
            "expiresAt", log.getAccessExpiresAt()
        ));
    }
}
