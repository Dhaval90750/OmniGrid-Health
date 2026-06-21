package com.medcore.his.domain.billing;

import com.medcore.his.domain.clinical.Visit;
import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ipd_bills")
@Getter
@Setter
public class IpdBill extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_id", nullable = false, unique = true)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "insurance_coverage", nullable = false)
    private BigDecimal insuranceCoverage = BigDecimal.ZERO;

    @Column(name = "patient_payable", nullable = false)
    private BigDecimal patientPayable = BigDecimal.ZERO;

    @Column(name = "amount_paid", nullable = false)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(nullable = false, length = 50)
    private String status = "Draft";

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillItem> items = new ArrayList<>();
}
