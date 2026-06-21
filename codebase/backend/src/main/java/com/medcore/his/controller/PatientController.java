package com.medcore.his.controller;

import com.medcore.his.dto.PatientRegistrationRequest;
import com.medcore.his.dto.PatientResponse;
import com.medcore.his.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    // @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<PatientResponse> registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {
        PatientResponse response = patientService.registerPatient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('NURSE') or hasRole('RECEPTIONIST')")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/search")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('NURSE') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<PatientResponse>> searchPatients(@RequestParam String q) {
        return ResponseEntity.ok(patientService.searchPatients(q));
    }
}
