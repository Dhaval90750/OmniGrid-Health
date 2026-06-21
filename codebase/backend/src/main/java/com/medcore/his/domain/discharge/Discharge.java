package com.medcore.his.domain.discharge;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.clinical.Visit;
import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "discharges")
@Getter
@Setter
public class Discharge extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_id", nullable = false, unique = true)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discharged_by", nullable = false)
    private User dischargedBy;

    @Column(name = "discharge_type", nullable = false, length = 50)
    private String dischargeType; // Normal, DAMA, LAMA, Death

    @Column(name = "final_summary", columnDefinition = "TEXT")
    private String finalSummary;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "discharge_date", nullable = false)
    private LocalDateTime dischargeDate = LocalDateTime.now();
}
