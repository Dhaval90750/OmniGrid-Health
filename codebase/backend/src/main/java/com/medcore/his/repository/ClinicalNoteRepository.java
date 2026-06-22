package com.medcore.his.repository;

import com.medcore.his.domain.clinical.ClinicalNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClinicalNoteRepository extends JpaRepository<ClinicalNote, UUID> {
    List<ClinicalNote> findByVisitPatientIdOrderByCreatedAtDesc(UUID patientId);
}
