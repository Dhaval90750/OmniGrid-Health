package com.medcore.his.domain.operations;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "housekeeping_tasks")
@Getter
@Setter
public class HousekeepingTask extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String zone;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, length = 50)
    private String priority = "Routine"; // Routine, Urgent, Emergency

    @Column(nullable = false, length = 50)
    private String status = "Pending"; // Pending, In_Progress, Completed

    @Column(name = "assigned_to", length = 100)
    private String assignedTo;
}
