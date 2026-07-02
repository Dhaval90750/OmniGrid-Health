package com.medcore.his.controller;

import com.medcore.his.domain.icu.IcuChart;
import com.medcore.his.domain.icu.IcuScore;
import com.medcore.his.service.IcuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_NURSE', 'ROLE_DOCTOR')")
@RestController
@RequestMapping("/api/v1/icu")
public class IcuController {

    private final IcuService icuService;

    @Autowired
    public IcuController(IcuService icuService) {
        this.icuService = icuService;
    }

    @GetMapping("/charts/patient/{patientId}")
    public ResponseEntity<List<IcuChart>> getCharts(@PathVariable UUID patientId) {
        return ResponseEntity.ok(icuService.getChartsByPatient(patientId));
    }

    @PostMapping("/charts")
    public ResponseEntity<IcuChart> saveChart(@RequestBody IcuChart chart) {
        return new ResponseEntity<>(icuService.saveChart(chart), HttpStatus.CREATED);
    }

    @GetMapping("/scores/patient/{patientId}")
    public ResponseEntity<List<IcuScore>> getScores(@PathVariable UUID patientId) {
        return ResponseEntity.ok(icuService.getScoresByPatient(patientId));
    }

    @PostMapping("/scores")
    public ResponseEntity<IcuScore> saveScore(@RequestBody IcuScore score) {
        return new ResponseEntity<>(icuService.saveScore(score), HttpStatus.CREATED);
    }
}
