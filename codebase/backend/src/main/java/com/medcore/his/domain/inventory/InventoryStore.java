package com.medcore.his.domain.inventory;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory_stores")
@Getter
@Setter
public class InventoryStore extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false)
    private String storeType; // MAIN, WARD, PHARMACY, OT, ICU, LAB

    @Column(nullable = false)
    private boolean isMainStore = false;

    @Column(nullable = false)
    private boolean isActive = true;
}
