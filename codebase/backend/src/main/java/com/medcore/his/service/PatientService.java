package com.medcore.his.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medcore.his.domain.patient.Patient;
import com.medcore.his.dto.PatientRegistrationRequest;
import com.medcore.his.dto.PatientResponse;
import com.medcore.his.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PatientService(PatientRepository patientRepository, QrCodeService qrCodeService, JdbcTemplate jdbcTemplate) {
        this.patientRepository = patientRepository;
        this.qrCodeService = qrCodeService;
        this.jdbcTemplate = jdbcTemplate;
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
        patient.setAddressLine2(request.getAddressLine2());
        patient.setCity(request.getCity());
        patient.setState(request.getState());
        patient.setCountry(request.getCountry());
        patient.setZipCode(request.getZipCode());
        patient.setMaritalStatus(request.getMaritalStatus());
        patient.setNationality(request.getNationality());
        patient.setPrimaryLanguage(request.getPrimaryLanguage());
        patient.setNationalId(request.getNationalId());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyContactRelation(request.getEmergencyContactRelation());
        patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        
        if (!request.isBypassDuplicateCheck()) {
            Patient existing = patientRepository.findFirstByMobileNumber(request.getMobileNumber())
                    .orElseGet(() -> patientRepository.findFirstByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDateOfBirth(
                            request.getFirstName(), request.getLastName(), request.getDateOfBirth()
                    ).orElse(null));

            if (existing != null) {
                throw new com.medcore.his.exception.DuplicatePatientException(
                        "Duplicate patient detected with same mobile number or identical name and DOB.",
                        getPatientWithQrCode(existing)
                );
            }
        }
        
        // Generate UHID: MED-YYYY-XXXXXX atomically via sequence
        String currentYear = String.valueOf(Year.now().getValue());
        Long sequenceValue = jdbcTemplate.queryForObject("SELECT nextval('uhid_seq')", Long.class);
        if (sequenceValue == null) {
            throw new RuntimeException("Failed to generate UHID sequence");
        }
        String sequence = String.format("%06d", sequenceValue);
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
