package com.medcore.his.repository;

import com.medcore.his.domain.patient.PatientConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatientConsentRepository extends JpaRepository<PatientConsent, UUID> {
    List<PatientConsent> findByPatientIdOrderBySignedAtDesc(UUID patientId);
}
