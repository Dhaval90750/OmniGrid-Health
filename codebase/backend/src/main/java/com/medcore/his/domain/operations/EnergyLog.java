package com.medcore.his.domain.operations;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "energy_logs")
@Getter
@Setter
public class EnergyLog extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String utilityType; // ELECTRICITY, WATER, GAS, DIESEL

    @Column(nullable = false)
    private LocalDate logDate;

    @Column(nullable = false)
    private java.math.BigDecimal consumedUnits;

    @Column(nullable = false, length = 50)
    private String unitOfMeasure; // KWH, LITERS, CUBIC_METERS

    @Column(nullable = false)
    private java.math.BigDecimal estimatedCost;
}
