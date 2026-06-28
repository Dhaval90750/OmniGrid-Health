package com.medcore.his.domain.inventory;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "goods_receipt_note_lines")
@Getter
@Setter
public class GoodsReceiptNoteLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grn_id", nullable = false)
    private GoodsReceiptNote grn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @Column(nullable = false)
    private int orderedQuantity;

    @Column(nullable = false)
    private int receivedQuantity;

    @Column(nullable = false)
    private int acceptedQuantity;

    @Column(nullable = false)
    private int rejectedQuantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    private String batchNumber;

    private LocalDate expiryDate;

    private String rejectionReason;
}
