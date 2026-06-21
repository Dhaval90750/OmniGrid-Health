package com.medcore.his.controller;

import com.medcore.his.domain.clinical.Admission;
import com.medcore.his.service.AdmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/admissions")
public class AdmissionController {

    private final AdmissionService admissionService;

    @Autowired
    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    @PostMapping
    public ResponseEntity<Admission> admitPatient(@RequestBody Admission admission) {
        Admission savedAdmission = admissionService.admitPatient(admission);
        return new ResponseEntity<>(savedAdmission, HttpStatus.CREATED);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Admission>> getActiveAdmissions() {
        return ResponseEntity.ok(admissionService.getActiveAdmissions());
    }
}
