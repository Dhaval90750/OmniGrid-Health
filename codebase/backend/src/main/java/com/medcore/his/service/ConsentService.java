package com.medcore.his.service;

import com.medcore.his.domain.patient.Patient;
import com.medcore.his.domain.patient.PatientConsent;
import com.medcore.his.repository.PatientConsentRepository;
import com.medcore.his.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class ConsentService {

    private final PatientConsentRepository consentRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public ConsentService(PatientConsentRepository consentRepository, PatientRepository patientRepository) {
        this.consentRepository = consentRepository;
        this.patientRepository = patientRepository;
    }

    public PatientConsent recordConsent(UUID patientId, PatientConsent consent) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        consent.setPatient(patient);
        consent.setSignedAt(LocalDateTime.now());
        consent.setIsRevoked(false);
        
        // Generate a mock hash representing the digital signature logic
        consent.setSignatureHash(generateMockHash(patient.getUhid(), consent.getConsentType(), consent.getSignedAt().toString()));
        
        return consentRepository.save(consent);
    }

    public List<PatientConsent> getPatientConsents(UUID patientId) {
        return consentRepository.findByPatientIdOrderBySignedAtDesc(patientId);
    }
    
    public PatientConsent revokeConsent(UUID consentId) {
        PatientConsent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new RuntimeException("Consent not found"));
        consent.setIsRevoked(true);
        consent.setRevokedAt(LocalDateTime.now());
        return consentRepository.save(consent);
    }

    private String generateMockHash(String uhid, String type, String timestamp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String payload = uhid + "|" + type + "|" + timestamp;
            byte[] hash = digest.digest(payload.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }
}
