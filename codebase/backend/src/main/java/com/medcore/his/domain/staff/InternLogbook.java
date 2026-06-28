package com.medcore.his.domain.staff;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "intern_logbooks")
@Getter
@Setter
public class InternLogbook extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rotation_id", nullable = false)
    private InternRotation rotation;

    @Column(nullable = false)
    private LocalDate logDate;

    @Column(nullable = false, length = 200)
    private String procedureObserved;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private boolean supervisorSignedOff = false;
}
