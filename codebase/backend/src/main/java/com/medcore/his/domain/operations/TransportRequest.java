package com.medcore.his.domain.operations;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transport_requests")
@Getter
@Setter
public class TransportRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient; // Nullable for non-patient transport

    @Column(name = "from_location", nullable = false, length = 100)
    private String fromLocation;

    @Column(name = "to_location", nullable = false, length = 100)
    private String toLocation;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(nullable = false, length = 50)
    private String priority = "Routine";

    @Column(nullable = false, length = 50)
    private String status = "Requested"; // Requested, Dispatched, In_Transit, Completed

    @Column(name = "assigned_porter", length = 100)
    private String assignedPorter;
}
