package com.medcore.his.domain.operations;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "diet_orders")
@Getter
@Setter
public class DietOrder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false, length = 100)
    private String dietType; // NORMAL, DIABETIC, RENAL, LIQUID, NPO

    @Column(columnDefinition = "TEXT")
    private String specialInstructions; // Allergies, preferences

    @Column(nullable = false, length = 50)
    private String status = "ORDERED"; // ORDERED, PREPARING, DELIVERED, CONSUMED
}
