package com.medcore.his.repository;

import com.medcore.his.domain.clinical.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VisitRepository extends JpaRepository<Visit, UUID> {
    List<Visit> findByDoctorIdOrderByVisitDateDesc(UUID doctorId);
    List<Visit> findByPatientIdOrderByVisitDateDesc(UUID patientId);
    
    @org.springframework.data.jpa.repository.Query("SELECT MAX(v.tokenNumber) FROM Visit v WHERE v.doctor.id = :doctorId AND CAST(v.visitDate AS date) = CAST(:date AS date)")
    Integer findMaxTokenNumberByDoctorIdAndVisitDate(@org.springframework.data.repository.query.Param("doctorId") UUID doctorId, @org.springframework.data.repository.query.Param("date") java.time.LocalDateTime date);

    @org.springframework.data.jpa.repository.Query("SELECT v FROM Visit v WHERE v.doctor.id = :doctorId AND CAST(v.visitDate AS date) = CAST(:date AS date) AND v.status != 'COMPLETED' ORDER BY v.tokenNumber ASC")
    List<Visit> findActiveQueueByDoctorAndDate(@org.springframework.data.repository.query.Param("doctorId") UUID doctorId, @org.springframework.data.repository.query.Param("date") java.time.LocalDateTime date);

    @org.springframework.data.jpa.repository.Query("SELECT v FROM Visit v WHERE CAST(v.visitDate AS date) = CAST(:date AS date) AND v.status != 'COMPLETED' ORDER BY v.tokenNumber ASC")
    List<Visit> findAllActiveQueuesByDate(@org.springframework.data.repository.query.Param("date") java.time.LocalDateTime date);
}
