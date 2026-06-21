package com.medcore.his.domain.auxiliary;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "infection_reports")
@Getter
@Setter
public class InfectionReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "ward_id", nullable = false, length = 100)
    private String wardId;

    @Column(name = "infection_type", nullable = false, length = 100)
    private String infectionType; // CAUTI, CLABSI, SSI, VAP

    @Column(name = "identified_date", nullable = false)
    private LocalDate identifiedDate;

    @Column(name = "organism_identified", length = 100)
    private String organismIdentified;

    @Column(nullable = false, length = 50)
    private String status = "Active"; // Active, Resolved

    @Column(name = "reported_by", length = 100)
    private String reportedBy;
}
