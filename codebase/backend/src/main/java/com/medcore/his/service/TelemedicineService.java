package com.medcore.his.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TelemedicineService {

    private static final Logger logger = LoggerFactory.getLogger(TelemedicineService.class);

    // In-memory store for virtual rooms (simulating a DB/Redis store for active sessions)
    private final Map<String, VirtualRoom> activeRooms = new ConcurrentHashMap<>();

    public static class VirtualRoom {
        private String roomId;
        private String patientName;
        private String doctorName;
        private String status; // WAITING, IN_PROGRESS, COMPLETED
        private String joinUrl;
        private LocalDateTime scheduledTime;

        public VirtualRoom(String roomId, String patientName, String doctorName, String status, String joinUrl) {
            this.roomId = roomId;
            this.patientName = patientName;
            this.doctorName = doctorName;
            this.status = status;
            this.joinUrl = joinUrl;
            this.scheduledTime = LocalDateTime.now();
        }

        public String getRoomId() { return roomId; }
        public String getPatientName() { return patientName; }
        public String getDoctorName() { return doctorName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getJoinUrl() { return joinUrl; }
        public LocalDateTime getScheduledTime() { return scheduledTime; }
    }

    public VirtualRoom createVirtualRoom(String patientName, String doctorName) {
        String roomId = UUID.randomUUID().toString();
        // In a real scenario, this URL would point to a WebRTC meeting like Jitsi or Vonage
        String mockJoinUrl = "https://meet.omnigrid.health/room/" + roomId;
        
        VirtualRoom room = new VirtualRoom(roomId, patientName, doctorName, "WAITING", mockJoinUrl);
        activeRooms.put(roomId, room);
        
        logger.info("Created virtual room {} for Doctor {} and Patient {}", roomId, doctorName, patientName);
        return room;
    }

    public VirtualRoom getRoomStatus(String roomId) {
        return activeRooms.get(roomId);
    }

    public boolean updateRoomStatus(String roomId, String newStatus) {
        VirtualRoom room = activeRooms.get(roomId);
        if (room != null) {
            room.setStatus(newStatus);
            logger.info("Virtual room {} status updated to {}", roomId, newStatus);
            return true;
        }
        return false;
    }

    public Map<String, VirtualRoom> getAllActiveRooms() {
        return activeRooms;
    }
}
