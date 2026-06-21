package com.medcore.his.repository;

import com.medcore.his.domain.nursing.PatientVital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatientVitalRepository extends JpaRepository<PatientVital, UUID> {
    List<PatientVital> findByPatientIdOrderByRecordedAtDesc(UUID patientId);
}
