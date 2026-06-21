package com.medcore.his.controller;

import com.medcore.his.domain.pharmacy.DispensingRecord;
import com.medcore.his.domain.pharmacy.PharmacyStock;
import com.medcore.his.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/pharmacy")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    @Autowired
    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @GetMapping("/stock/{drugId}")
    public ResponseEntity<List<PharmacyStock>> getStockByDrug(@PathVariable UUID drugId) {
        return ResponseEntity.ok(pharmacyService.getStockByDrug(drugId));
    }

    @PostMapping("/dispense")
    public ResponseEntity<DispensingRecord> dispense(@RequestBody DispensingRecord record) {
        // Simple mapping, normally deductions would be part of a DTO
        DispensingRecord saved = pharmacyService.dispensePrescription(record, List.of());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
