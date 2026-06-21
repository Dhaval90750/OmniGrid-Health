package com.medcore.his.domain.pharmacy;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.clinical.Prescription;
import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispensing_records")
@Getter
@Setter
public class DispensingRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pharmacist_id", nullable = false)
    private User pharmacist;

    @Column(name = "dispensing_date", nullable = false)
    private LocalDateTime dispensingDate = LocalDateTime.now();

    @Column(nullable = false, length = 50)
    private String status = "Dispensed"; // Dispensed, Partially_Dispensed, Cancelled
}
