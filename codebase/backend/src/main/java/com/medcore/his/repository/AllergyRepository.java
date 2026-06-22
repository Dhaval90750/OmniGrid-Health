package com.medcore.his.repository;

import com.medcore.his.domain.patient.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, UUID> {
    List<Allergy> findByPatientIdAndStatus(UUID patientId, String status);
}
