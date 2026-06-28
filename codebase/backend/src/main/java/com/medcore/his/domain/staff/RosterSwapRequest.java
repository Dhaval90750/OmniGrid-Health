package com.medcore.his.domain.staff;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roster_swap_requests")
@Getter
@Setter
public class RosterSwapRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "roster_id", nullable = false)
    private DutyRoster roster;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private StaffProfile requestedBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_staff_id", nullable = false)
    private StaffProfile targetStaff;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, ACCEPTED_BY_TARGET, APPROVED, REJECTED
}
