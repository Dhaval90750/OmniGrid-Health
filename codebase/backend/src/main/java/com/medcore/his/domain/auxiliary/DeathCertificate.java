package com.medcore.his.domain.auxiliary;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import com.medcore.his.domain.staff.StaffProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "death_certificates")
@Getter
@Setter
public class DeathCertificate extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false, unique = true, length = 100)
    private String certificateNumber;

    @Column(nullable = false)
    private LocalDateTime timeOfDeath;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String causeOfDeath; // Immediate, Antecedent, Underlying

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "certified_by_id", nullable = false)
    private StaffProfile certifiedBy;

    @Column(nullable = false)
    private String status = "DRAFT"; // DRAFT, ISSUED, HANDED_OVER
}
