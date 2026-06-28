package com.medcore.his.domain.inventory;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goods_receipt_notes")
@Getter
@Setter
public class GoodsReceiptNote extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String grnNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(nullable = false)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDate invoiceDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by_id", nullable = false)
    private User receivedBy;

    @Column(nullable = false)
    private String status = "PENDING_QC"; // PENDING_QC, ACCEPTED, REJECTED, PARTIAL_ACCEPT

    @Column(columnDefinition = "TEXT")
    private String qcRemarks;

    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoodsReceiptNoteLine> lines = new ArrayList<>();
}
