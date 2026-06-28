package com.medcore.his.domain.auxiliary;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "antibiograms")
@Getter
@Setter
public class Antibiogram extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "infection_id", nullable = false)
    private InfectionReport infection;

    @Column(nullable = false, length = 100)
    private String organism;

    @Column(nullable = false, length = 100)
    private String antibioticTested;

    @Column(nullable = false, length = 50)
    private String sensitivity; // SENSITIVE, RESISTANT, INTERMEDIATE

    @Column(nullable = false)
    private LocalDate testDate;
}
