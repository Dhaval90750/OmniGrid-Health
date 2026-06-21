package com.medcore.his.domain.lab;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lab_samples")
@Getter
@Setter
public class LabSample extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private LabOrder order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_id", nullable = false)
    private LabTest test;

    @Column(unique = true, length = 100)
    private String barcode;

    @Column(nullable = false, length = 50)
    private String status = "Pending_Collection"; // Pending_Collection, Collected, Received, Rejected

    @Column(name = "collected_at")
    private LocalDateTime collectedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_by")
    private User collectedBy;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;
}
