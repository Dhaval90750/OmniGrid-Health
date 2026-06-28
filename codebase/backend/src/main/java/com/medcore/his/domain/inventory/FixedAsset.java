package com.medcore.his.domain.inventory;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "fixed_assets")
@Getter
@Setter
public class FixedAsset extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String assetCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false, length = 100)
    private String departmentId;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    @Column(nullable = false)
    private java.math.BigDecimal purchaseCost;

    @Column(nullable = false)
    private int expectedLifeYears;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, UNDER_MAINTENANCE, DEPRECIATED, SCRAPPED
}
