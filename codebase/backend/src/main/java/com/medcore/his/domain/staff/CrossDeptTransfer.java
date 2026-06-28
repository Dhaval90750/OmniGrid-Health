package com.medcore.his.domain.staff;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cross_dept_transfers")
@Getter
@Setter
public class CrossDeptTransfer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private StaffProfile staff;

    @Column(nullable = false, length = 100)
    private String fromDepartment;

    @Column(nullable = false, length = 100)
    private String toDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private StaffProfile approvedBy;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED
}
