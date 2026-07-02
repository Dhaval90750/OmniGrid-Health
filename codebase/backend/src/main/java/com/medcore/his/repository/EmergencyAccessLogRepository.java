package com.medcore.his.repository;

import com.medcore.his.domain.auth.EmergencyAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmergencyAccessLogRepository extends JpaRepository<EmergencyAccessLog, UUID> {
}
