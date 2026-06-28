package com.medcore.his.domain.staff;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "resident_profiles")
@Getter
@Setter
public class ResidentProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private StaffProfile staff;

    @Column(nullable = false, length = 100)
    private String programName; // MD Medicine, MS Surgery

    @Column(nullable = false)
    private int yearOfResidency; // 1, 2, 3

    @Column(length = 200)
    private String thesisTopic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private StaffProfile thesisGuide;

    @Column(nullable = false)
    private LocalDate expectedCompletionDate;
}
