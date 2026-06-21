package com.medcore.his.domain.master;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "beds", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ward_id", "bed_number"})
})
@Getter
@Setter
public class Bed extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    @Column(name = "bed_number", nullable = false, length = 20)
    private String bedNumber;

    @Column(nullable = false, length = 50)
    private String status = "AVAILABLE";

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "daily_rate", precision = 10, scale = 2)
    private BigDecimal dailyRate;
}
