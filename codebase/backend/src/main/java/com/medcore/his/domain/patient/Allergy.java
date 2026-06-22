package com.medcore.his.domain.patient;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "patient_allergies")
@Getter
@Setter
public class Allergy extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "allergen", nullable = false)
    private String allergen;

    @Column(name = "severity", nullable = false)
    private String severity;

    @Column(name = "reaction")
    private String reaction;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";
}
