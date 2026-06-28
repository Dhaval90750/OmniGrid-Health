package com.medcore.his.domain.inventory;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "purchase_indent_lines")
@Getter
@Setter
public class PurchaseIndentLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indent_id", nullable = false)
    private PurchaseIndent indent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @Column(nullable = false)
    private int requestedQuantity;

    private int approvedQuantity;

    private String remarks;
}
