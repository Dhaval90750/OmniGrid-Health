package com.medcore.his.domain.auxiliary;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "nutrition_plans")
@Getter
@Setter
public class NutritionPlan extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private DietAssessment assessment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private int targetCaloriesPerDay;

    @Column(nullable = false)
    private int targetProteinGrams;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, COMPLETED, REVISED
}
