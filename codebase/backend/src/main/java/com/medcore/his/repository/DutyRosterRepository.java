package com.medcore.his.repository;

import com.medcore.his.domain.staff.DutyRoster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DutyRosterRepository extends JpaRepository<DutyRoster, UUID> {
}
