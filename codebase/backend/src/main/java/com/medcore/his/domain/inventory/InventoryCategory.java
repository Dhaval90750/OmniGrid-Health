package com.medcore.his.domain.inventory;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory_categories")
@Getter
@Setter
public class InventoryCategory extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String classificationType; // CONSUMABLE, ASSET, IMPLANT, LINEN, PHARMACY

    @Column(nullable = false)
    private boolean isActive = true;
}
