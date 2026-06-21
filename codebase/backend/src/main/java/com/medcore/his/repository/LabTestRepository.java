package com.medcore.his.repository;

import com.medcore.his.domain.lab.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, UUID> {
    List<LabTest> findByTestNameContainingIgnoreCaseOrTestCodeContainingIgnoreCase(String name, String code);
}
