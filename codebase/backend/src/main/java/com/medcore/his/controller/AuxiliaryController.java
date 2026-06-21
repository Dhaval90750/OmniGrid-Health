package com.medcore.his.controller;

import com.medcore.his.domain.auxiliary.BloodInventory;
import com.medcore.his.domain.auxiliary.InfectionReport;
import com.medcore.his.service.AuxiliaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auxiliary")
public class AuxiliaryController {

    private final AuxiliaryService auxiliaryService;

    @Autowired
    public AuxiliaryController(AuxiliaryService auxiliaryService) {
        this.auxiliaryService = auxiliaryService;
    }

    @GetMapping("/blood-bank")
    public ResponseEntity<List<BloodInventory>> getBloodInventory() {
        return ResponseEntity.ok(auxiliaryService.getAllBloodInventory());
    }

    @PostMapping("/blood-bank")
    public ResponseEntity<BloodInventory> addBloodUnit(@RequestBody BloodInventory unit) {
        return new ResponseEntity<>(auxiliaryService.addBloodUnit(unit), HttpStatus.CREATED);
    }

    @GetMapping("/infections")
    public ResponseEntity<List<InfectionReport>> getInfectionReports() {
        return ResponseEntity.ok(auxiliaryService.getAllInfectionReports());
    }

    @PostMapping("/infections")
    public ResponseEntity<InfectionReport> addInfectionReport(@RequestBody InfectionReport report) {
        return new ResponseEntity<>(auxiliaryService.addInfectionReport(report), HttpStatus.CREATED);
    }
}
