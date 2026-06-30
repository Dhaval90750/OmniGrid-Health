package com.medcore.his.controller;

import com.medcore.his.domain.discharge.Discharge;
import com.medcore.his.service.DischargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/discharges")
public class DischargeController {

    private final DischargeService dischargeService;

    @Autowired
    public DischargeController(DischargeService dischargeService) {
        this.dischargeService = dischargeService;
    }

    @PostMapping
    public ResponseEntity<Discharge> dischargePatient(@RequestBody Discharge discharge) {
        return new ResponseEntity<>(dischargeService.initiateDischarge(discharge), HttpStatus.CREATED);
    }
}
