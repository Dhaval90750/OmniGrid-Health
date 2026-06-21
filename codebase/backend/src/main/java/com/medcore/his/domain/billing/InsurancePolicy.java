package com.medcore.his.domain.billing;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "insurance_policies")
@Getter
@Setter
public class InsurancePolicy extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "tpa_name")
    private String tpaName;

    @Column(name = "policy_number", nullable = false, unique = true)
    private String policyNumber;

    @Column(name = "coverage_limit")
    private BigDecimal coverageLimit;

    @Column(name = "valid_until")
    private LocalDate validUntil;
}
