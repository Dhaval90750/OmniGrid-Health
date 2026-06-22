package com.medcore.his.repository;

import com.medcore.his.domain.clinical.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, UUID> {
    List<Diagnosis> findByPatientIdOrderByDiagnosedDateDesc(UUID patientId);
    List<Diagnosis> findByPatientIdAndStatusOrderByDiagnosedDateDesc(UUID patientId, String status);
}
