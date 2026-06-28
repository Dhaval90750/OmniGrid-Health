package com.medcore.his.domain.staff;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "staff_profiles")
@Getter
@Setter
public class StaffProfile extends BaseEntity {

    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, length = 100)
    private String role; // Consultant, Resident, Intern, Nurse, Admin

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "contact_number", length = 50)
    private String contactNumber;

    @Column(length = 100)
    private String email;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private StaffProfile supervisor;
}
