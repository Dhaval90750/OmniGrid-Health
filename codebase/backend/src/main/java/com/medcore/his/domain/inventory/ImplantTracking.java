package com.medcore.his.domain.inventory;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "implant_tracking")
@Getter
@Setter
public class ImplantTracking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @Column(nullable = false, unique = true, length = 100)
    private String serialNumber;

    @Column(length = 100)
    private String lotNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column
    private LocalDate implantDate;

    @Column(length = 100)
    private String surgeonId;

    @Column(nullable = false)
    private String status = "IN_STOCK"; // IN_STOCK, IMPLANTED, RECALLED, EXPIRED
}
