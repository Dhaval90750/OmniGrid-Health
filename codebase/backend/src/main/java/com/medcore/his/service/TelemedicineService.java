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

    private final com.medcore.his.repository.TelemedicineRoomRepository roomRepository;

    @org.springframework.beans.factory.annotation.Autowired
    public TelemedicineService(com.medcore.his.repository.TelemedicineRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public com.medcore.his.domain.clinical.TelemedicineRoom createVirtualRoom(String patientName, String doctorName) {
        String roomId = UUID.randomUUID().toString();
        // In a real scenario, this URL would point to a WebRTC meeting like Jitsi or Vonage
        String joinUrl = "https://meet.omnigrid.health/room/" + roomId;
        
        com.medcore.his.domain.clinical.TelemedicineRoom room = new com.medcore.his.domain.clinical.TelemedicineRoom();
        room.setRoomId(roomId);
        room.setPatientName(patientName);
        room.setDoctorName(doctorName);
        room.setStatus("WAITING");
        room.setJoinUrl(joinUrl);
        room.setScheduledTime(LocalDateTime.now());
        
        room = roomRepository.save(room);
        
        logger.info("Created virtual room {} for Doctor {} and Patient {}", roomId, doctorName, patientName);
        return room;
    }

    public com.medcore.his.domain.clinical.TelemedicineRoom getRoomStatus(String roomId) {
        return roomRepository.findByRoomId(roomId).orElse(null);
    }

    public boolean updateRoomStatus(String roomId, String newStatus) {
        return roomRepository.findByRoomId(roomId).map(room -> {
            room.setStatus(newStatus);
            roomRepository.save(room);
            logger.info("Virtual room {} status updated to {}", roomId, newStatus);
            return true;
        }).orElse(false);
    }

    public java.util.List<com.medcore.his.domain.clinical.TelemedicineRoom> getAllActiveRooms() {
        return roomRepository.findAll();
    }
}
