package com.medcore.his.domain.clinical;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "prescription_lines")
@Getter
@Setter
public class PrescriptionLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id")
    private Drug drug;

    @Column(name = "custom_drug_name", length = 255)
    private String customDrugName;

    @Column(nullable = false, length = 100)
    private String dosage;

    @Column(nullable = false, length = 50)
    private String route;

    @Column(nullable = false, length = 50)
    private String frequency;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private Integer quantity;

    @Column(columnDefinition = "TEXT")
    private String instructions;
}
