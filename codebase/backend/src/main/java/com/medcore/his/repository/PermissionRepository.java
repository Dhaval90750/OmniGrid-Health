package com.medcore.his.repository;

import com.medcore.his.domain.auth.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByCode(String code);
    Optional<Permission> findByModuleAndAccessLevel(String module, String accessLevel);
}
