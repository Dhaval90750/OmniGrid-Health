package com.medcore.his.domain.patient;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_consents")
@Getter
@Setter
public class PatientConsent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "consent_type", nullable = false, length = 100)
    private String consentType; // GENERAL, SURGICAL, BLOOD, RESEARCH

    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @Column(name = "signed_by", length = 100)
    private String signedBy; // Patient, Guardian, Proxy

    @Column(name = "relation_to_patient", length = 50)
    private String relationToPatient; // Self, Parent, Spouse

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "signature_hash", length = 256)
    private String signatureHash;
    
    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(nullable = false)
    private Boolean isRevoked = false;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
}
