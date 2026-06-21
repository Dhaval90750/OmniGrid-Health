package com.medcore.his.controller;

import com.medcore.his.domain.ot.OtBooking;
import com.medcore.his.domain.ot.SurgeryRecord;
import com.medcore.his.service.OtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/ot")
public class OtController {

    private final OtService otService;

    @Autowired
    public OtController(OtService otService) {
        this.otService = otService;
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<OtBooking>> getAllBookings() {
        return ResponseEntity.ok(otService.getAllBookings());
    }

    @PostMapping("/bookings")
    public ResponseEntity<OtBooking> createBooking(@RequestBody OtBooking booking) {
        return new ResponseEntity<>(otService.createBooking(booking), HttpStatus.CREATED);
    }

    @PostMapping("/records")
    public ResponseEntity<SurgeryRecord> saveRecord(@RequestBody SurgeryRecord record) {
        return new ResponseEntity<>(otService.saveSurgeryRecord(record), HttpStatus.CREATED);
    }
}
