package com.medcore.his.repository;

import com.medcore.his.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    
    List<User> findByLicenseExpiryDateBetweenAndIsActiveTrue(LocalDate startDate, LocalDate endDate);
}
