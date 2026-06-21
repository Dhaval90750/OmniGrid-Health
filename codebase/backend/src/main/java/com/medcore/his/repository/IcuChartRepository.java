package com.medcore.his.repository;

import com.medcore.his.domain.icu.IcuChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IcuChartRepository extends JpaRepository<IcuChart, UUID> {
    List<IcuChart> findByPatientIdOrderByRecordedAtDesc(UUID patientId);
}
