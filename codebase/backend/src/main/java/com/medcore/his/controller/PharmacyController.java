package com.medcore.his.controller;

import com.medcore.his.domain.pharmacy.DispensingRecord;
import com.medcore.his.domain.pharmacy.PharmacyStock;
import com.medcore.his.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_PHARMACIST', 'ROLE_DOCTOR')")
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

    @GetMapping("/dashboard/stats")
    public ResponseEntity<java.util.Map<String, Long>> getStats() {
        return ResponseEntity.ok(pharmacyService.getPharmacyStats());
    }

    @GetMapping("/stock/alerts/low")
    public ResponseEntity<List<PharmacyStock>> getLowStockAlerts() {
        return ResponseEntity.ok(pharmacyService.getLowStockAlerts());
    }

    @GetMapping("/stock/alerts/expiring")
    public ResponseEntity<List<PharmacyStock>> getExpiringStockAlerts() {
        return ResponseEntity.ok(pharmacyService.getExpiringStockAlerts());
    }

    @GetMapping("/narcotics/register")
    public ResponseEntity<List<com.medcore.his.domain.pharmacy.StockMovement>> getNarcoticRegister() {
        return ResponseEntity.ok(pharmacyService.getNarcoticRegister());
    }
}
