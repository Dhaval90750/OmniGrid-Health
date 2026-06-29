package com.medcore.his.domain.clinical;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "telemedicine_rooms")
@Getter
@Setter
public class TelemedicineRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String roomId;

    @Column(nullable = false)
    private String patientName;

    @Column(nullable = false)
    private String doctorName;

    @Column(nullable = false)
    private String status; // WAITING, IN_PROGRESS, COMPLETED

    @Column(nullable = false, length = 500)
    private String joinUrl;

    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @PrePersist
    protected void onCreate() {
        if (scheduledTime == null) {
            scheduledTime = LocalDateTime.now();
        }
    }
}
