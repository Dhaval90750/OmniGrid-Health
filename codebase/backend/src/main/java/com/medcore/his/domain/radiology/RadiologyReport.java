package com.medcore.his.domain.radiology;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "radiology_reports")
@Getter
@Setter
public class RadiologyReport extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private RadiologyOrder order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "radiologist_id", nullable = false)
    private User radiologist;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String findings;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String impression;

    @Column(name = "is_critical", nullable = false)
    private Boolean isCritical = false;

    @Column(name = "dicom_study_uid", length = 255)
    private String dicomStudyUid;

    @Column(nullable = false, length = 50)
    private String status = "Draft"; // Draft, Final

    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;
}
