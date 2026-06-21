package com.medcore.his.repository;

import com.medcore.his.domain.lab.LabOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LabOrderRepository extends JpaRepository<LabOrder, UUID> {
}
