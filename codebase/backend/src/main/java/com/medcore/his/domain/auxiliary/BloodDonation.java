package com.medcore.his.domain.auxiliary;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_donations")
@Getter
@Setter
public class BloodDonation extends BaseEntity {

    @Column(nullable = false)
    private String donorName;

    @Column(nullable = false, length = 15)
    private String contactNumber;

    @Column(nullable = false, length = 10)
    private String bloodGroup; // A+, A-, B+, B-, O+, O-, AB+, AB-

    @Column(nullable = false)
    private LocalDateTime donationTime;

    @Column(nullable = false, unique = true)
    private String bagNumber;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private String status = "QUARANTINED"; // QUARANTINED, TESTED_SAFE, ISSUED, DISCARDED
}
