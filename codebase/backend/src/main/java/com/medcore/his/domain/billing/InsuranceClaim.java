package com.medcore.his.domain.billing;

import com.medcore.his.domain.clinical.Visit;
import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "insurance_claims")
@Getter
@Setter
public class InsuranceClaim extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private InsurancePolicy policy;

    @Column(nullable = false, length = 50)
    private String status = "PreAuth_Pending";

    @Column(name = "preauth_amount")
    private BigDecimal preauthAmount;

    @Column(name = "claimed_amount")
    private BigDecimal claimedAmount;

    @Column(name = "approved_amount")
    private BigDecimal approvedAmount;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
