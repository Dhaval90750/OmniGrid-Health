package com.medcore.his.domain.operations;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "incident_reports")
@Getter
@Setter
public class IncidentReport extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 50)
    private String category; // e.g. "Fall", "Medication Error", "Near Miss"

    @Column(nullable = false, length = 20)
    private String severity; // e.g. "Low", "Medium", "High", "Critical"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime incidentTime;

    @Column(length = 100)
    private String location;

    @Column(length = 100)
    private String reportedBy;

    @Column(nullable = false, length = 50)
    private String status = "Open"; // Open, Under Investigation, Resolved
}
