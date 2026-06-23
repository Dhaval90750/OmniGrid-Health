package com.medcore.his.domain.lab;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_results")
@Getter
@Setter
public class LabResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sample_id", nullable = false)
    private LabSample sample;

    @Column(name = "result_value")
    private BigDecimal resultValue;

    @Column(name = "result_text", length = 255)
    private String resultText;

    @Column(name = "is_abnormal", nullable = false)
    private Boolean isAbnormal = false;

    @Column(name = "is_critical", nullable = false)
    private Boolean isCritical = false;

    @Column(name = "delta_warning")
    private String deltaWarning;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorized_by")
    private User authorizedBy;

    @Column(name = "authorized_at")
    private LocalDateTime authorizedAt;
}
