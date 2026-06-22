package com.medcore.his.controller;

import com.medcore.his.domain.patient.Allergy;
import com.medcore.his.domain.patient.Patient;
import com.medcore.his.repository.AllergyRepository;
import com.medcore.his.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/allergies")
public class AllergyController {

    private final AllergyRepository allergyRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public AllergyController(AllergyRepository allergyRepository, PatientRepository patientRepository) {
        this.allergyRepository = allergyRepository;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Allergy>> getPatientAllergies(@PathVariable UUID patientId) {
        return ResponseEntity.ok(allergyRepository.findByPatientIdAndStatus(patientId, "ACTIVE"));
    }

    @PostMapping("/patient/{patientId}")
    public ResponseEntity<?> addAllergy(@PathVariable UUID patientId, @RequestBody Allergy allergy) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
        }
        allergy.setPatient(patient);
        return ResponseEntity.ok(allergyRepository.save(allergy));
    }
}
