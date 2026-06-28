package com.medcore.his.domain.inventory;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_indents")
@Getter
@Setter
public class PurchaseIndent extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String indentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private com.medcore.his.domain.master.Department department;

    @Column(nullable = false)
    private String status = "DRAFT"; // DRAFT, HOD_APPROVED, FINANCE_APPROVED, REJECTED, PO_GENERATED

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @OneToMany(mappedBy = "indent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseIndentLine> lines = new ArrayList<>();
}
