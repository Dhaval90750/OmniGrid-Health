package com.medcore.his.repository;

import com.medcore.his.domain.lab.LabSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LabSampleRepository extends JpaRepository<LabSample, UUID> {
    LabSample findByBarcode(String barcode);
    java.util.List<LabSample> findByStatus(String status);
}
