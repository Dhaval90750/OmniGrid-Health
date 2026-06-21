package com.medcore.his.repository;

import com.medcore.his.domain.operations.HousekeepingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTask, UUID> {
}
