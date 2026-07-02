package com.medcore.his.domain.pharmacy;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "drug_interactions")
@Getter
@Setter
public class DrugInteraction extends BaseEntity {

    @Column(name = "primary_drug_generic", nullable = false, length = 255)
    private String primaryDrugGeneric;

    @Column(name = "secondary_drug_generic", nullable = false, length = 255)
    private String secondaryDrugGeneric;

    @Column(name = "severity", nullable = false, length = 50)
    private String severity; // e.g. WARNING, CAUTION, HIGH RISK, CONTRAINDICATED

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
}
