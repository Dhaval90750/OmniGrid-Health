package com.medcore.his.domain.auxiliary;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "blood_inventory")
@Getter
@Setter
public class BloodInventory extends BaseEntity {

    @Column(name = "blood_group", nullable = false, length = 10)
    private String bloodGroup;

    @Column(name = "component_type", nullable = false, length = 50)
    private String componentType;

    @Column(name = "unit_number", nullable = false, unique = true, length = 50)
    private String unitNumber;

    @Column(name = "collection_date", nullable = false)
    private LocalDate collectionDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false, length = 50)
    private String status = "Available"; // Available, Cross-matched, Issued, Discarded

    @Column(name = "donor_id", length = 50)
    private String donorId;
}
