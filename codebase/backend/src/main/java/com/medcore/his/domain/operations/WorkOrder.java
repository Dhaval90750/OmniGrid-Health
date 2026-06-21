package com.medcore.his.domain.operations;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "work_orders")
@Getter
@Setter
public class WorkOrder extends BaseEntity {

    @Column(name = "ticket_number", nullable = false, unique = true)
    private String ticketNumber;

    @Column(nullable = false, length = 100)
    private String category; // HVAC, Electrical, Plumbing, IT

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String priority = "Medium"; // Low, Medium, High, Critical

    @Column(nullable = false, length = 50)
    private String status = "Open"; // Open, Assigned, In_Progress, Resolved, Closed

    @Column(name = "assigned_technician", length = 100)
    private String assignedTechnician;
}
