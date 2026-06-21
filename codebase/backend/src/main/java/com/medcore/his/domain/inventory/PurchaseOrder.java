package com.medcore.his.domain.inventory;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
public class PurchaseOrder extends BaseEntity {

    @Column(name = "po_number", nullable = false, unique = true)
    private String poNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(nullable = false, length = 50)
    private String status = "Draft"; // Draft, Approved, Dispatched, Delivered

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;
}
