package com.medcore.his.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medcore.his.domain.patient.Patient;
import com.medcore.his.dto.PatientRegistrationRequest;
import com.medcore.his.dto.PatientResponse;
import com.medcore.his.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final QrCodeService qrCodeService;
    private final ObjectMapper objectMapper;

    @Autowired
    public PatientService(PatientRepository patientRepository, QrCodeService qrCodeService) {
        this.patientRepository = patientRepository;
        this.qrCodeService = qrCodeService;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public PatientResponse registerPatient(PatientRegistrationRequest request) {
        Patient patient = new Patient();
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setMobileNumber(request.getMobileNumber());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setEmail(request.getEmail());
        patient.setAddressLine1(request.getAddressLine1());
        patient.setCity(request.getCity());
        patient.setState(request.getState());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        
        // Generate UHID: MED-YYYY-XXXXXX
        String currentYear = String.valueOf(Year.now().getValue());
        long currentCount = patientRepository.countTotalPatients() + 1;
        String sequence = String.format("%06d", currentCount);
        String generatedUhid = "MED-" + currentYear + "-" + sequence;
        
        patient.setUhid(generatedUhid);
        
        Patient savedPatient = patientRepository.save(patient);
        
        return getPatientWithQrCode(savedPatient);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return getPatientWithQrCode(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> searchPatients(String keyword) {
        return patientRepository.searchPatients(keyword).stream()
                .map(p -> PatientResponse.fromEntity(p, null)) // Don't generate QR in list view for performance
                .collect(Collectors.toList());
    }

    private PatientResponse getPatientWithQrCode(Patient patient) {
        Map<String, String> qrDataMap = new HashMap<>();
        qrDataMap.put("uhid", patient.getUhid());
        qrDataMap.put("name", patient.getFirstName() + " " + patient.getLastName());
        qrDataMap.put("dob", patient.getDateOfBirth().toString());
        
        String qrJson;
        try {
            qrJson = objectMapper.writeValueAsString(qrDataMap);
        } catch (JsonProcessingException e) {
            qrJson = patient.getUhid(); // fallback to just UHID string
        }
        
        String qrCodeBase64 = qrCodeService.generateQrCodeBase64(qrJson);
        return PatientResponse.fromEntity(patient, qrCodeBase64);
    }
}
