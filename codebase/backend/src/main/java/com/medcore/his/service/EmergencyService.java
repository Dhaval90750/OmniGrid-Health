package com.medcore.his.service;

import com.medcore.his.domain.clinical.TriageAssessment;
import com.medcore.his.domain.patient.Patient;
import com.medcore.his.repository.PatientRepository;
import com.medcore.his.repository.TriageAssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmergencyService {

    private final TriageAssessmentRepository triageAssessmentRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public EmergencyService(TriageAssessmentRepository triageAssessmentRepository, PatientRepository patientRepository) {
        this.triageAssessmentRepository = triageAssessmentRepository;
        this.patientRepository = patientRepository;
    }

    public TriageAssessment recordTriage(UUID patientId, TriageAssessment assessment) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        assessment.setPatient(patient);
        if (assessment.getEsiLevel() == null) {
            assessment.setEsiLevel(calculateEsi(assessment));
        }
        
        return triageAssessmentRepository.save(assessment);
    }

    public List<TriageAssessment> getActiveEmergencies() {
        return triageAssessmentRepository.findByStatusOrderByEsiLevelAsc("PENDING");
    }
    
    public TriageAssessment updateStatus(UUID assessmentId, String newStatus) {
        TriageAssessment assessment = triageAssessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        assessment.setStatus(newStatus);
        return triageAssessmentRepository.save(assessment);
    }

    private int calculateEsi(TriageAssessment a) {
        // Simple mock logic for ESI calculation
        if (a.getHeartRate() != null && (a.getHeartRate() > 130 || a.getHeartRate() < 40)) return 1;
        if (a.getOxygenSaturation() != null && a.getOxygenSaturation() < 90) return 2;
        if (a.getGcsScore() != null && a.getGcsScore() < 13) return 2;
        return 3;
    }
}
