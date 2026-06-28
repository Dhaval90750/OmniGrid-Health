package com.medcore.his.domain.operations;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_incidents")
@Getter
@Setter
public class SecurityIncident extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false, length = 100)
    private String incidentType; // THEFT, VIOLENCE, UNAUTHORIZED_ACCESS, FIRE

    @Column(nullable = false)
    private LocalDateTime incidentTime;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String severity = "MINOR"; // MINOR, MODERATE, SEVERE, CRITICAL

    @Column(nullable = false)
    private String status = "REPORTED"; // REPORTED, INVESTIGATING, RESOLVED
}
