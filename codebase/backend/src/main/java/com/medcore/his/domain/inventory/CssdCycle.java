package com.medcore.his.domain.inventory;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "cssd_cycles")
@Getter
@Setter
public class CssdCycle extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String cycleNumber;

    @Column(nullable = false, length = 100)
    private String machineId;

    @Column(nullable = false)
    private String sterilizationType; // AUTOCLAVE, ETO, PLASMA

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean biologicalIndicatorPassed = false;

    @Column(nullable = false)
    private String status = "IN_PROGRESS"; // IN_PROGRESS, COMPLETED, FAILED
}
