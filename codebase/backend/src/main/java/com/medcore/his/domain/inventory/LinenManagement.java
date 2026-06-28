package com.medcore.his.domain.inventory;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "linen_management")
@Getter
@Setter
public class LinenManagement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private InventoryStore store;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String status = "CLEAN"; // CLEAN, IN_USE, SOILED, IN_LAUNDRY, CONDEMNED

    @Column(name = "last_washed_time")
    private LocalDateTime lastWashedTime;
}
