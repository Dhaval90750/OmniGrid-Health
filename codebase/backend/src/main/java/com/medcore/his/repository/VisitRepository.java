package com.medcore.his.repository;

import com.medcore.his.domain.clinical.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VisitRepository extends JpaRepository<Visit, UUID> {
    List<Visit> findByDoctorIdOrderByVisitDateDesc(UUID doctorId);
}
