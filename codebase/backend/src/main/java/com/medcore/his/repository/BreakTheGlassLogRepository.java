package com.medcore.his.repository;

import com.medcore.his.domain.security.BreakTheGlassLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BreakTheGlassLogRepository extends JpaRepository<BreakTheGlassLog, UUID> {
}
