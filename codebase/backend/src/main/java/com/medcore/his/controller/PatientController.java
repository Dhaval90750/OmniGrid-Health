package com.medcore.his.controller;

import com.medcore.his.dto.PatientRegistrationRequest;
import com.medcore.his.dto.PatientResponse;
import com.medcore.his.service.PatientService;
import com.medcore.his.service.PdfGenerationService;
import com.medcore.his.service.QrCodeService;
import com.medcore.his.repository.PatientRepository;
import com.medcore.his.domain.patient.Patient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;
    private final PdfGenerationService pdfGenerationService;
    private final QrCodeService qrCodeService;
    private final PatientRepository patientRepository;

    @Autowired
    public PatientController(PatientService patientService,
                             PdfGenerationService pdfGenerationService,
                             QrCodeService qrCodeService,
                             PatientRepository patientRepository) {
        this.patientService = patientService;
        this.pdfGenerationService = pdfGenerationService;
        this.qrCodeService = qrCodeService;
        this.patientRepository = patientRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<PatientResponse> registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {
        PatientResponse response = patientService.registerPatient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('NURSE') or hasRole('RECEPTIONIST')")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('NURSE') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<PatientResponse>> searchPatients(@RequestParam String q) {
        return ResponseEntity.ok(patientService.searchPatients(q));
    }

    @GetMapping("/advanced-search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('NURSE') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<PatientResponse>> advancedSearchPatients(
            @RequestParam(required = false) String uhid,
            @RequestParam(required = false) String mobileNumber,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String nationalId) {
        return ResponseEntity.ok(patientService.advancedSearchPatients(uhid, mobileNumber, firstName, lastName, nationalId));
    }

    @GetMapping("/{id}/qr-pdf")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('NURSE') or hasRole('RECEPTIONIST')")
    public ResponseEntity<byte[]> getPatientQrPdf(@PathVariable UUID id) {
        Patient patient = patientRepository.findById(id).orElse(null);
        if (patient == null) {
            return ResponseEntity.notFound().build();
        }
        
        int checksum = patient.getUhid().length() + (patient.getFirstName() + patient.getLastName()).length();
        String bloodGroup = patient.getBloodGroup() != null ? patient.getBloodGroup() : "Unknown";
        String qrJson = String.format("{\"uhid\":\"%s\",\"hospital_code\":\"MEDCORE-01\",\"name\":\"%s %s\",\"dob\":\"%s\",\"blood_group\":\"%s\",\"allergies_flag\":\"false\",\"checksum\":\"%s\"}",
                patient.getUhid(), patient.getFirstName(), patient.getLastName(),
                patient.getDateOfBirth().toString(), bloodGroup, checksum);
        
        byte[] qrBytes = qrCodeService.generateQrCodeImage(qrJson, 150, 150);
        
        byte[] pdfBytes = pdfGenerationService.generatePatientQrCardPdf(patient, qrBytes);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "patient_" + patient.getUhid() + "_qr.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
