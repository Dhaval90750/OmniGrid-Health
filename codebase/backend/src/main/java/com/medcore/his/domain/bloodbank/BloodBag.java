package com.medcore.his.domain.bloodbank;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "blood_bags")
@Getter
@Setter
public class BloodBag extends BaseEntity {

    @Column(name = "bag_number", nullable = false, unique = true, length = 50)
    private String bagNumber;

    @Column(name = "blood_group", nullable = false, length = 5)
    private String bloodGroup; // A+, O-, B+, AB-

    @Column(name = "component_type", nullable = false, length = 50)
    private String componentType; // Whole Blood, PRBC, FFP, Platelets

    @Column(name = "collection_date", nullable = false)
    private LocalDate collectionDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "volume_ml")
    private Integer volumeMl;

    @Column(length = 20)
    private String status = "AVAILABLE"; // AVAILABLE, RESERVED, ISSUED, DISCARDED
}
