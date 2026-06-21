package com.medcore.his.repository;

import com.medcore.his.domain.nursing.MedAdministration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedAdministrationRepository extends JpaRepository<MedAdministration, UUID> {
    List<MedAdministration> findByPatientIdOrderByScheduledTimeAsc(UUID patientId);
}
