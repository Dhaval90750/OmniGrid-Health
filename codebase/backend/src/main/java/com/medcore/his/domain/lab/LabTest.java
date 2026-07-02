package com.medcore.his.domain.lab;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "lab_tests")
@Getter
@Setter
public class LabTest extends BaseEntity {

    @Column(name = "test_code", nullable = false, unique = true, length = 50)
    private String testCode;

    @Column(name = "test_name", nullable = false, length = 255)
    private String testName;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(name = "loinc_code", length = 20)
    private String loincCode;

    @Column(name = "sample_type", length = 50)
    private String sampleType; // Blood, Urine, etc.

    @Column(name = "tube_color", length = 50)
    private String tubeColor; // Red, Lavender, etc.

    @Column(name = "tat_target_hours")
    private Integer tatTargetHours; // Turn-around time in hours

    @Column(name = "reference_range_low")
    private BigDecimal referenceRangeLow;

    @Column(name = "reference_range_high")
    private BigDecimal referenceRangeHigh;

    @Column(length = 50)
    private String unit;
}
