package com.medcore.his.domain.inventory;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stock", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"store_id", "item_id", "batch_number"})
})
@Getter
@Setter
public class Stock extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private InventoryStore store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "expiry_date")
    private java.time.LocalDate expiryDate;

    @Column(nullable = false)
    private java.math.BigDecimal unitCost;
}
