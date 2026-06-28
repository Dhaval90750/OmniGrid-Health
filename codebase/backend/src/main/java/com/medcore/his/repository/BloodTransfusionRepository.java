package com.medcore.his.repository;

import com.medcore.his.domain.auxiliary.BloodTransfusion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BloodTransfusionRepository extends JpaRepository<BloodTransfusion, UUID> {
    List<BloodTransfusion> findByPatientId(UUID patientId);
}
