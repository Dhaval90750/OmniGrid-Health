package com.medcore.his.domain.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "break_the_glass_log")
public class EmergencyAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "justification_type", nullable = false)
    private String justificationType; // e.g. "LIFE_THREATENING_EMERGENCY", "SYSTEM_OUTAGE"

    @Column(name = "justification_text", nullable = false)
    private String justificationText;

    @Column(name = "access_granted_at", nullable = false)
    private LocalDateTime accessGrantedAt;

    @Column(name = "access_expires_at", nullable = false)
    private LocalDateTime accessExpiresAt;

    @Column(name = "ip_address")
    private String ipAddress;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public String getJustificationType() { return justificationType; }
    public void setJustificationType(String justificationType) { this.justificationType = justificationType; }
    public String getJustificationText() { return justificationText; }
    public void setJustificationText(String justificationText) { this.justificationText = justificationText; }
    public LocalDateTime getAccessGrantedAt() { return accessGrantedAt; }
    public void setAccessGrantedAt(LocalDateTime accessGrantedAt) { this.accessGrantedAt = accessGrantedAt; }
    public LocalDateTime getAccessExpiresAt() { return accessExpiresAt; }
    public void setAccessExpiresAt(LocalDateTime accessExpiresAt) { this.accessExpiresAt = accessExpiresAt; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
