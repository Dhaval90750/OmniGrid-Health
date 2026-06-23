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
import com.medcore.his.domain.billing.InvoiceItem;
import com.medcore.his.domain.billing.Tariff;
import com.medcore.his.repository.TariffRepository;

@Service
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final TariffRepository tariffRepository;

    @Autowired
    public BillingService(InvoiceRepository invoiceRepository, TariffRepository tariffRepository) {
        this.invoiceRepository = invoiceRepository;
        this.tariffRepository = tariffRepository;
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

    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice addInvoiceLine(UUID invoiceId, UUID tariffId, int quantity) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
                
        Tariff tariff = tariffRepository.findById(tariffId)
                .orElseThrow(() -> new RuntimeException("Tariff not found"));

        InvoiceItem item = new InvoiceItem();
        item.setInvoice(invoice);
        item.setDescription(tariff.getServiceName());
        item.setUnitPrice(tariff.getPrice());
        item.setQuantity(quantity);
        item.setTotalPrice(tariff.getPrice().multiply(BigDecimal.valueOf(quantity)));

        invoice.getItems().add(item);
        
        // Recalculate totals
        BigDecimal total = invoice.getItems().stream()
                .map(InvoiceItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setTotalAmount(total);
        invoice.setNetAmount(total.subtract(invoice.getDiscountAmount()));
        
        return invoiceRepository.save(invoice);
    }
}
