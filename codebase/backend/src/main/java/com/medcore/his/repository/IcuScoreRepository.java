package com.medcore.his.repository;

import com.medcore.his.domain.icu.IcuScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IcuScoreRepository extends JpaRepository<IcuScore, UUID> {
    List<IcuScore> findByPatientIdOrderByRecordedAtDesc(UUID patientId);
}
