package com.medcore.his.controller;

import com.medcore.his.domain.billing.Invoice;
import com.medcore.his.domain.billing.Payment;
import com.medcore.his.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/billing")
public class BillingController {

    private final BillingService billingService;

    @Autowired
    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/invoices/pending")
    public ResponseEntity<List<Invoice>> getPendingInvoices() {
        return ResponseEntity.ok(billingService.getPendingInvoices());
    }

    @PostMapping("/invoices/{id}/pay")
    public ResponseEntity<Payment> collectPayment(@PathVariable UUID id, @RequestBody Payment payment) {
        Payment savedPayment = billingService.recordPayment(id, payment);
        return ResponseEntity.ok(savedPayment);
    }

    @PostMapping("/invoices")
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(billingService.createInvoice(invoice));
    }

    @PostMapping("/invoices/{id}/lines")
    public ResponseEntity<Invoice> addInvoiceLine(
            @PathVariable UUID id, 
            @RequestBody java.util.Map<String, String> payload) {
        UUID tariffId = UUID.fromString(payload.get("tariffId"));
        int quantity = Integer.parseInt(payload.getOrDefault("quantity", "1"));
        return ResponseEntity.ok(billingService.addInvoiceLine(id, tariffId, quantity));
    }
}
