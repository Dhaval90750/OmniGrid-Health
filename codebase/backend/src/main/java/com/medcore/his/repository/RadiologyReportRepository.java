package com.medcore.his.repository;

import com.medcore.his.domain.radiology.RadiologyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RadiologyReportRepository extends JpaRepository<RadiologyReport, UUID> {
    RadiologyReport findByOrderId(UUID orderId);
}
