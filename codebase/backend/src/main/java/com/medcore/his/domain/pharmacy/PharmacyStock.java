package com.medcore.his.domain.pharmacy;

import com.medcore.his.domain.clinical.Drug;
import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pharmacy_stock", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"drug_id", "batch_number"})
})
@Getter
@Setter
public class PharmacyStock extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @Column(name = "batch_number", nullable = false, length = 100)
    private String batchNumber;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "mrp")
    private BigDecimal mrp;
}
