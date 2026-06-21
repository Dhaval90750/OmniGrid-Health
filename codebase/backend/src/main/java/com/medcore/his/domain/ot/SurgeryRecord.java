package com.medcore.his.domain.ot;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ot_surgery_records")
@Getter
@Setter
public class SurgeryRecord extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private OtBooking booking;

    // WHO Checklist
    @Column(name = "sign_in_completed", nullable = false)
    private Boolean signInCompleted = false;

    @Column(name = "time_out_completed", nullable = false)
    private Boolean timeOutCompleted = false;

    @Column(name = "sign_out_completed", nullable = false)
    private Boolean signOutCompleted = false;

    // Operative Note
    @Column(name = "pre_op_diagnosis", length = 255)
    private String preOpDiagnosis;

    @Column(name = "post_op_diagnosis", length = 255)
    private String postOpDiagnosis;

    @Column(name = "procedure_performed", length = 255)
    private String procedurePerformed;

    @Column(columnDefinition = "TEXT")
    private String findings;

    @Column(name = "estimated_blood_loss")
    private Integer estimatedBloodLoss;

    @Column(name = "specimens_sent", nullable = false)
    private Boolean specimensSent = false;

    @Column(columnDefinition = "TEXT")
    private String complications;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
