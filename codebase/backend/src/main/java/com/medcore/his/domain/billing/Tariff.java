package com.medcore.his.domain.billing;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tariffs")
@Getter
@Setter
public class Tariff extends BaseEntity {

    @Column(name = "service_code", nullable = false, unique = true, length = 50)
    private String serviceCode;

    @Column(name = "service_name", nullable = false, length = 255)
    private String serviceName;

    @Column(nullable = false, length = 100)
    private String category; // e.g., Consultation, Procedure, Lab Test, Room Charge

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
