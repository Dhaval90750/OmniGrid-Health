package com.medcore.his.repository;

import com.medcore.his.domain.lab.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, UUID> {
    List<LabResult> findBySampleId(UUID sampleId);
}
