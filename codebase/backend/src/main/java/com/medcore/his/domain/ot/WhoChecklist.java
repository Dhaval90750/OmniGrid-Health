package com.medcore.his.domain.ot;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "who_checklists")
@Getter
@Setter
public class WhoChecklist extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private OtBooking booking;

    @Column(name = "sign_in_completed")
    private Boolean signInCompleted = false;

    @Column(name = "time_out_completed")
    private Boolean timeOutCompleted = false;

    @Column(name = "sign_out_completed")
    private Boolean signOutCompleted = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
