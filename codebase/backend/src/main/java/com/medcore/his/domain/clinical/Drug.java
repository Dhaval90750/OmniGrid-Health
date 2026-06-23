package com.medcore.his.domain.clinical;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "drugs")
@Getter
@Setter
public class Drug extends BaseEntity {

    @Column(name = "generic_name", nullable = false, length = 255)
    private String genericName;

    @Column(name = "brand_name", length = 255)
    private String brandName;

    @Column(name = "dosage_form", nullable = false, length = 50)
    private String dosageForm;

    @Column(nullable = false, length = 50)
    private String strength;

    @Column(name = "is_narcotic", nullable = false)
    private Boolean isNarcotic = false;

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel = 100;
}
