package com.medcore.his.domain.inventory;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "InventoryStockMovement")
@Table(name = "inventory_stock_movements")
@Getter
@Setter
public class StockMovement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_store_id")
    private InventoryStore sourceStore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_store_id")
    private InventoryStore destinationStore;

    @Column(nullable = false)
    private String movementType; // RECEIPT, ISSUE, TRANSFER, ADJUSTMENT, WRITE_OFF, RETURN

    @Column(nullable = false)
    private int quantity;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id", nullable = false)
    private User performedBy;
}
