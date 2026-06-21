package com.medcore.his.domain.ot;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.clinical.Visit;
import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ot_bookings")
@Getter
@Setter
public class OtBooking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "surgeon_id", nullable = false)
    private User surgeon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anesthesiologist_id")
    private User anesthesiologist;

    @Column(name = "ot_room_number", nullable = false, length = 50)
    private String otRoomNumber;

    @Column(name = "procedure_name", nullable = false, length = 255)
    private String procedureName;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "estimated_duration")
    private Integer estimatedDuration;

    @Column(nullable = false, length = 50)
    private String status = "Scheduled"; // Scheduled, In_Progress, Completed, Cancelled
    
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private SurgeryRecord surgeryRecord;
}
