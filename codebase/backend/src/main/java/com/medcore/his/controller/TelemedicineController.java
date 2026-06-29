package com.medcore.his.controller;

import com.medcore.his.service.TelemedicineService;
import com.medcore.his.domain.clinical.TelemedicineRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/telemedicine")
public class TelemedicineController {

    private final TelemedicineService telemedicineService;

    @Autowired
    public TelemedicineController(TelemedicineService telemedicineService) {
        this.telemedicineService = telemedicineService;
    }

    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(@RequestBody Map<String, String> payload) {
        String patientName = payload.getOrDefault("patientName", "Unknown Patient");
        String doctorName = payload.getOrDefault("doctorName", "Unknown Doctor");
        
        TelemedicineRoom room = telemedicineService.createVirtualRoom(patientName, doctorName);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/rooms")
    public ResponseEntity<?> getAllActiveRooms() {
        return ResponseEntity.ok(telemedicineService.getAllActiveRooms());
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<?> getRoomStatus(@PathVariable String roomId) {
        TelemedicineRoom room = telemedicineService.getRoomStatus(roomId);
        if (room != null) {
            return ResponseEntity.ok(room);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/rooms/{roomId}/status")
    public ResponseEntity<?> updateRoomStatus(@PathVariable String roomId, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        if (newStatus == null || newStatus.isEmpty()) {
            return ResponseEntity.badRequest().body("Status is required");
        }
        
        boolean updated = telemedicineService.updateRoomStatus(roomId, newStatus);
        if (updated) {
            return ResponseEntity.ok(telemedicineService.getRoomStatus(roomId));
        }
        return ResponseEntity.notFound().build();
    }
}
