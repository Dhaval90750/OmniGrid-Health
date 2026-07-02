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
    private final com.medcore.his.repository.BreakTheGlassLogRepository breakGlassRepo;

    @Autowired
    public EmergencyService(TriageAssessmentRepository triageAssessmentRepository, 
                            PatientRepository patientRepository,
                            com.medcore.his.repository.BreakTheGlassLogRepository breakGlassRepo) {
        this.triageAssessmentRepository = triageAssessmentRepository;
        this.patientRepository = patientRepository;
        this.breakGlassRepo = breakGlassRepo;
    }

    public void logBreakGlass(UUID patientId, UUID userId, String reason, String ipAddress) {
        com.medcore.his.domain.security.BreakTheGlassLog log = new com.medcore.his.domain.security.BreakTheGlassLog();
        log.setPatientId(patientId);
        log.setUserId(userId);
        log.setReason(reason);
        log.setIpAddress(ipAddress);
        breakGlassRepo.save(log);
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
