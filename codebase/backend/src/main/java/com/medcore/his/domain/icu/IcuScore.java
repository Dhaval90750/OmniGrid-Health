package com.medcore.his.domain.icu;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "icu_scores")
@Getter
@Setter
public class IcuScore extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recorded_by", nullable = false)
    private User recordedBy;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt = LocalDateTime.now();

    @Column(name = "score_type", nullable = false, length = 50)
    private String scoreType = "GCS";

    @Column(name = "gcs_eye")
    private Integer gcsEye;

    @Column(name = "gcs_verbal")
    private Integer gcsVerbal;

    @Column(name = "gcs_motor")
    private Integer gcsMotor;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;
}
