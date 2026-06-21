package com.medcore.his.service;

import com.medcore.his.domain.billing.Invoice;
import com.medcore.his.domain.billing.Payment;
import com.medcore.his.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class BillingService {

    private final InvoiceRepository invoiceRepository;

    @Autowired
    public BillingService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public Payment recordPayment(UUID invoiceId, Payment payment) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Generate receipt number (mock)
        payment.setReceiptNumber("REC-" + System.currentTimeMillis());
        payment.setInvoice(invoice);

        // Update Invoice Amount Paid
        BigDecimal newAmountPaid = invoice.getAmountPaid().add(payment.getAmount());
        invoice.setAmountPaid(newAmountPaid);

        // Update Invoice Status
        if (newAmountPaid.compareTo(invoice.getNetAmount()) >= 0) {
            invoice.setStatus("PAID");
        } else if (newAmountPaid.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus("PARTIAL");
        }

        invoice.getPayments().add(payment);
        invoiceRepository.save(invoice);

        return payment;
    }

    @Transactional(readOnly = true)
    public List<Invoice> getPendingInvoices() {
        return invoiceRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }
}
