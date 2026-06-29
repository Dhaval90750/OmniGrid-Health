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
    private final com.medcore.his.repository.PaymentRepository paymentRepository;
    private final com.medcore.his.repository.InsuranceClaimRepository insuranceClaimRepository;
    private final com.medcore.his.repository.IpdBillRepository ipdBillRepository;

    @Autowired
    public BillingService(InvoiceRepository invoiceRepository, TariffRepository tariffRepository, 
                          com.medcore.his.repository.PaymentRepository paymentRepository,
                          com.medcore.his.repository.InsuranceClaimRepository insuranceClaimRepository,
                          com.medcore.his.repository.IpdBillRepository ipdBillRepository) {
        this.invoiceRepository = invoiceRepository;
        this.tariffRepository = tariffRepository;
        this.paymentRepository = paymentRepository;
        this.insuranceClaimRepository = insuranceClaimRepository;
        this.ipdBillRepository = ipdBillRepository;
    }

    @Transactional
    public Payment recordPayment(UUID invoiceId, Payment payment) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Generate receipt number based on count
        long nextReceiptNum = paymentRepository.count() + 1;
        payment.setReceiptNumber(String.format("REC-%06d", nextReceiptNum));
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

        return paymentRepository.save(payment); // Also save the payment via its repo to avoid duplicate receipt numbers in parallel saves
    }

    @Transactional(readOnly = true)
    public List<Invoice> getPendingInvoices() {
        return invoiceRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    @Transactional(readOnly = true)
    public List<com.medcore.his.domain.billing.IpdBill> getPendingIpdBills() {
        return ipdBillRepository.findByStatus("Draft");
    }

    @Transactional(readOnly = true)
    public List<com.medcore.his.domain.billing.InsuranceClaim> getAllClaims() {
        return insuranceClaimRepository.findAll();
    }

    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        long nextInvNum = invoiceRepository.count() + 1;
        invoice.setInvoiceNumber(String.format("INV-%06d", nextInvNum));
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

    @Autowired
    private com.medcore.his.repository.PatientRepository patientRepository;

    @Transactional
    public Invoice addChargeToPatient(UUID patientId, String description, BigDecimal unitPrice, int quantity) {
        // 1. Check if an active PENDING invoice exists
        List<Invoice> pendingInvoices = invoiceRepository.findByStatusOrderByCreatedAtDesc("PENDING");
        Invoice activeInvoice = pendingInvoices.stream()
                .filter(inv -> inv.getPatient().getId().equals(patientId))
                .findFirst()
                .orElse(null);

        // 2. If not, create a new one
        if (activeInvoice == null) {
            activeInvoice = new Invoice();
            long nextInvNum = invoiceRepository.count() + 1;
            activeInvoice.setInvoiceNumber(String.format("INV-%06d", nextInvNum));
            activeInvoice.setPatient(patientRepository.findById(patientId).orElseThrow(() -> new RuntimeException("Patient not found")));
            activeInvoice.setStatus("PENDING");
            activeInvoice = invoiceRepository.save(activeInvoice);
        }

        // 3. Add the line item
        InvoiceItem item = new InvoiceItem();
        item.setInvoice(activeInvoice);
        item.setDescription(description);
        item.setUnitPrice(unitPrice);
        item.setQuantity(quantity);
        item.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)));

        activeInvoice.getItems().add(item);

        // 4. Recalculate totals
        BigDecimal total = activeInvoice.getItems().stream()
                .map(InvoiceItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        activeInvoice.setTotalAmount(total);
        activeInvoice.setNetAmount(total.subtract(activeInvoice.getDiscountAmount()));

        return invoiceRepository.save(activeInvoice);
    }
}
