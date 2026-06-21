package com.medcore.his.domain.inventory;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "inventory_vendors")
@Getter
@Setter
public class Vendor extends BaseEntity {

    @Column(name = "vendor_code", nullable = false, unique = true)
    private String vendorCode;

    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;
}
