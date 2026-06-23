package com.medcore.his.domain.ot;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "implant_logs")
@Getter
@Setter
public class ImplantLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private OtBooking booking;

    @Column(name = "implant_name", nullable = false)
    private String implantName;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "lot_number", nullable = false)
    private String lotNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "used_quantity")
    private Integer usedQuantity = 1;
}
