package com.medcore.his.domain.auth;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "is_mfa_enabled", nullable = false)
    private boolean isMfaEnabled = false;

    @Column(name = "mfa_secret", length = 255)
    private String mfaSecret;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
