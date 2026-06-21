package com.medcore.his.domain.billing;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "receipt_number", unique = true, nullable = false, length = 50)
    private String receiptNumber;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod; // CASH, CARD, UPI

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_by")
    private User collectedBy;
}
