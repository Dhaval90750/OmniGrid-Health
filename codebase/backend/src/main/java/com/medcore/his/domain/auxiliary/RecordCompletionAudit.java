package com.medcore.his.domain.auxiliary;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import com.medcore.his.domain.staff.StaffProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "record_completion_audits")
@Getter
@Setter
public class RecordCompletionAudit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "audited_by_id", nullable = false)
    private StaffProfile auditedBy;

    @Column(nullable = false)
    private LocalDateTime auditDate;

    @Column(nullable = false)
    private boolean isIcd10Coded = false;

    @Column(nullable = false)
    private boolean isDischargeSummarySigned = false;

    @Column(nullable = false)
    private boolean isConsentFormAttached = false;

    @Column(columnDefinition = "TEXT")
    private String deficiencies;

    @Column(nullable = false)
    private String status = "INCOMPLETE"; // INCOMPLETE, PENDING_SIGNATURE, COMPLETE
}
