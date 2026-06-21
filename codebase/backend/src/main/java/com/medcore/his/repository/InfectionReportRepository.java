package com.medcore.his.repository;

import com.medcore.his.domain.auxiliary.InfectionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InfectionReportRepository extends JpaRepository<InfectionReport, UUID> {
}
