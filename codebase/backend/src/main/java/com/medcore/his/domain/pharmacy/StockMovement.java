package com.medcore.his.domain.pharmacy;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "PharmacyStockMovement")
@Table(name = "stock_movements")
@Getter
@Setter
public class StockMovement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    private PharmacyStock stock;

    @Column(name = "movement_type", nullable = false, length = 50)
    private String movementType; // Receipt, Issue, Dispense, Adjustment

    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange; // Positive or negative

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
