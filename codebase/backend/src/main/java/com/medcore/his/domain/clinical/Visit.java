package com.medcore.his.domain.clinical;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.master.Department;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "visits")
@Getter
@Setter
public class Visit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "visit_type", nullable = false, length = 50)
    private String visitType = "OPD";

    @Column(nullable = false, length = 50)
    private String status = "SCHEDULED";

    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;

    @Column(name = "chief_complaint", length = 255)
    private String chiefComplaint;

    @Column(name = "token_number")
    private Integer tokenNumber;

    @Column(name = "queue_status", length = 50)
    private String queueStatus = "WAITING";
}
