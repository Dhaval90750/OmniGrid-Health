package com.medcore.his.controller;

import com.medcore.his.domain.clinical.Drug;
import com.medcore.his.domain.clinical.Prescription;
import com.medcore.his.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping("/drugs/search")
    public ResponseEntity<List<Drug>> searchDrugs(@RequestParam String query) {
        return ResponseEntity.ok(prescriptionService.searchDrugs(query));
    }

    @PostMapping("/prescriptions")
    public ResponseEntity<Prescription> createPrescription(@RequestBody Prescription prescription) {
        Prescription saved = prescriptionService.savePrescription(prescription);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
