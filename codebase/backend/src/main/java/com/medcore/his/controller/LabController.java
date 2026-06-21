package com.medcore.his.controller;

import com.medcore.his.domain.lab.LabOrder;
import com.medcore.his.domain.lab.LabTest;
import com.medcore.his.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/lab")
public class LabController {

    private final LabService labService;

    @Autowired
    public LabController(LabService labService) {
        this.labService = labService;
    }

    @GetMapping("/tests/search")
    public ResponseEntity<List<LabTest>> searchTests(@RequestParam String query) {
        return ResponseEntity.ok(labService.searchTests(query));
    }

    @PostMapping("/orders")
    public ResponseEntity<LabOrder> createOrder(@RequestBody LabOrder order) {
        LabOrder saved = labService.placeOrder(order);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
