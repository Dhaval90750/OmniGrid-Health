package com.medcore.his.domain.staff;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "intern_rotations")
@Getter
@Setter
public class InternRotation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "intern_id", nullable = false)
    private StaffProfile intern;

    @Column(nullable = false, length = 100)
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private StaffProfile supervisor;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String status = "SCHEDULED"; // SCHEDULED, IN_PROGRESS, COMPLETED
}
