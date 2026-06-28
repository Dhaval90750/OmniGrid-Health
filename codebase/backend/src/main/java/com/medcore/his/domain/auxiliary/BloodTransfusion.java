package com.medcore.his.domain.auxiliary;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "blood_transfusions")
@Getter
@Setter
public class BloodTransfusion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "donation_id", nullable = false)
    private BloodDonation donation;

    @Column(nullable = false)
    private String crossMatchResult = "PENDING"; // PENDING, COMPATIBLE, INCOMPATIBLE

    @Column
    private LocalDateTime transfusionStartTime;

    @Column
    private LocalDateTime transfusionEndTime;

    @Column(columnDefinition = "TEXT")
    private String reactionNotes;

    @Column(nullable = false)
    private String status = "REQUESTED"; // REQUESTED, CROSS_MATCHED, ISSUED, TRANSFUSED
}
