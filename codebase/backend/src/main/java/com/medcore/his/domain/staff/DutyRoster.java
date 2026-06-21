package com.medcore.his.domain.staff;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "duty_rosters")
@Getter
@Setter
public class DutyRoster extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private StaffProfile staff;

    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    @Column(name = "shift_type", nullable = false, length = 50)
    private String shiftType; // Morning, Evening, Night, On-Call

    @Column(nullable = false, length = 100)
    private String location; // Ward A, ICU, ER
}
