package com.medcore.his.repository;

import com.medcore.his.domain.clinical.TriageAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TriageAssessmentRepository extends JpaRepository<TriageAssessment, UUID> {
    List<TriageAssessment> findByStatusOrderByEsiLevelAsc(String status);
}
