package com.medcore.his.controller;

import com.medcore.his.domain.nursing.MedAdministration;
import com.medcore.his.domain.nursing.PatientVital;
import com.medcore.his.service.NursingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/nursing")
public class NursingController {

    private final NursingService nursingService;

    @Autowired
    public NursingController(NursingService nursingService) {
        this.nursingService = nursingService;
    }

    @GetMapping("/vitals/patient/{patientId}")
    public ResponseEntity<List<PatientVital>> getVitals(@PathVariable UUID patientId) {
        return ResponseEntity.ok(nursingService.getVitalsByPatient(patientId));
    }

    @PostMapping("/vitals")
    public ResponseEntity<PatientVital> recordVitals(@RequestBody PatientVital vital) {
        return new ResponseEntity<>(nursingService.saveVitals(vital), HttpStatus.CREATED);
    }

    @GetMapping("/mar/patient/{patientId}")
    public ResponseEntity<List<MedAdministration>> getMar(@PathVariable UUID patientId) {
        return ResponseEntity.ok(nursingService.getMarForPatient(patientId));
    }

    @PostMapping("/mar")
    public ResponseEntity<MedAdministration> recordAdministration(@RequestBody MedAdministration admin) {
        return new ResponseEntity<>(nursingService.recordAdministration(admin), HttpStatus.CREATED);
    }
}
