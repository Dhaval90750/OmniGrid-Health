package com.medcore.his.controller;

import com.medcore.his.domain.radiology.RadiologyOrder;
import com.medcore.his.domain.radiology.RadiologyTemplate;
import com.medcore.his.service.RadiologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/radiology")
public class RadiologyController {

    private final RadiologyService radiologyService;

    @Autowired
    public RadiologyController(RadiologyService radiologyService) {
        this.radiologyService = radiologyService;
    }

    @GetMapping("/templates")
    public ResponseEntity<List<RadiologyTemplate>> getTemplates(@RequestParam String modality) {
        return ResponseEntity.ok(radiologyService.getTemplatesByModality(modality));
    }

    @PostMapping("/orders")
    public ResponseEntity<RadiologyOrder> createOrder(@RequestBody RadiologyOrder order) {
        RadiologyOrder saved = radiologyService.placeOrder(order);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
