package com.medcore.his.domain.inventory;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory_items")
@Getter
@Setter
public class InventoryItem extends BaseEntity {

    @Column(name = "item_code", nullable = false, unique = true)
    private String itemCode;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private InventoryCategory categoryEntity;

    @Column(nullable = false, length = 100)
    private String category; // Deprecated by categoryEntity, kept for backward compatibility

    @Column(name = "unit_of_measure", nullable = false, length = 50)
    private String unitOfMeasure; // Box, Vial, Piece
    
    @Column(length = 1)
    private String abcClass; // A, B, C (Value based)
    
    @Column(length = 1)
    private String vedClass; // V, E, D (Vital, Essential, Desirable)
    
    @Column(length = 1)
    private String fsnClass; // F, S, N (Fast, Slow, Non-moving)

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel = 10;
    
    @Column(name = "max_level")
    private Integer maxLevel;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock = 0;
}
