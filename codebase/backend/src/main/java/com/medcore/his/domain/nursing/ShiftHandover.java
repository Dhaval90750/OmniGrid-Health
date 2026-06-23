package com.medcore.his.domain.nursing;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "shift_handovers")
@Getter
@Setter
public class ShiftHandover extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "handing_over_nurse", nullable = false)
    private User handingOverNurse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiving_nurse")
    private User receivingNurse;

    @Column(name = "shift_date_time", nullable = false)
    private java.time.LocalDateTime shiftDateTime = java.time.LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String sbarSituation;

    @Column(columnDefinition = "TEXT")
    private String sbarBackground;

    @Column(columnDefinition = "TEXT")
    private String sbarAssessment;

    @Column(columnDefinition = "TEXT")
    private String sbarRecommendation;
}
