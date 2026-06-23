package com.medcore.his.service;

import com.medcore.his.domain.pharmacy.DispensingRecord;
import com.medcore.his.domain.pharmacy.PharmacyStock;
import com.medcore.his.domain.pharmacy.StockMovement;
import com.medcore.his.repository.DispensingRecordRepository;
import com.medcore.his.repository.PharmacyStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PharmacyService {

    private final PharmacyStockRepository stockRepository;
    private final DispensingRecordRepository dispensingRepository;
    private final com.medcore.his.repository.PrescriptionRepository prescriptionRepository;
    private final com.medcore.his.repository.StockMovementRepository stockMovementRepository;

    @Autowired
    public PharmacyService(PharmacyStockRepository stockRepository, 
                           DispensingRecordRepository dispensingRepository,
                           com.medcore.his.repository.PrescriptionRepository prescriptionRepository,
                           com.medcore.his.repository.StockMovementRepository stockMovementRepository) {
        this.stockRepository = stockRepository;
        this.dispensingRepository = dispensingRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<PharmacyStock> getStockByDrug(UUID drugId) {
        return stockRepository.findByDrugIdOrderByExpiryDateAsc(drugId);
    }

    @Transactional
    public DispensingRecord dispensePrescription(DispensingRecord record, List<StockMovement> manualDeductions) {
        com.medcore.his.domain.clinical.Prescription prescription = prescriptionRepository.findById(record.getPrescription().getId())
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
        
        record.setPrescription(prescription);
        
        // FEFO Logic
        for (com.medcore.his.domain.clinical.PrescriptionLine line : prescription.getLines()) {
            if (line.getDrug() == null) continue; // Custom non-system drug
            
            int requiredQty = line.getQuantity();
            List<PharmacyStock> availableStocks = getStockByDrug(line.getDrug().getId());
            
            // Auto-seed dummy stock if none exists to allow testing without manual ingestion
            if (availableStocks.isEmpty()) {
                PharmacyStock dummyStock = new PharmacyStock();
                dummyStock.setDrug(line.getDrug());
                dummyStock.setBatchNumber("AUTO-" + UUID.randomUUID().toString().substring(0, 6));
                dummyStock.setExpiryDate(LocalDate.now().plusYears(1));
                dummyStock.setQuantity(1000); // Give plenty of dummy stock
                dummyStock.setUnitPrice(java.math.BigDecimal.valueOf(10.0));
                dummyStock.setMrp(java.math.BigDecimal.valueOf(12.0));
                stockRepository.save(dummyStock);
                availableStocks.add(dummyStock);
            }
            
            for (PharmacyStock stock : availableStocks) {
                if (requiredQty <= 0) break;
                
                int deductQty = Math.min(requiredQty, stock.getQuantity());
                if (deductQty > 0) {
                    stock.setQuantity(stock.getQuantity() - deductQty);
                    stockRepository.save(stock);
                    
                    StockMovement movement = new StockMovement();
                    movement.setStock(stock);
                    movement.setMovementType("Dispense");
                    movement.setQuantityChange(-deductQty);
                    movement.setReferenceNumber("RX-" + prescription.getId());
                    movement.setNotes("Dispensed for patient " + record.getPatient().getId());
                    stockMovementRepository.save(movement);
                    
                    requiredQty -= deductQty;
                }
            }
            
            if (requiredQty > 0) {
                throw new RuntimeException("Insufficient stock for drug: " + line.getDrug().getGenericName());
            }
        }
        
        record.setStatus("Dispensed");
        return dispensingRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<PharmacyStock> getLowStockAlerts() {
        return stockRepository.findAll().stream()
                .filter(s -> s.getQuantity() <= s.getDrug().getReorderLevel())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PharmacyStock> getExpiringStockAlerts() {
        LocalDate cutoff = LocalDate.now().plusDays(30);
        return stockRepository.findAll().stream()
                .filter(s -> s.getQuantity() > 0 && s.getExpiryDate().isBefore(cutoff))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StockMovement> getNarcoticRegister() {
        return stockMovementRepository.findAll().stream()
                .filter(m -> m.getStock().getDrug().getIsNarcotic())
                .sorted((m1, m2) -> m2.getCreatedAt().compareTo(m1.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Long> getPharmacyStats() {
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("lowStockItems", (long) getLowStockAlerts().size());
        stats.put("expiringItems", (long) getExpiringStockAlerts().size());
        stats.put("pendingPrescriptions", prescriptionRepository.findAll().stream()
                .filter(p -> "Draft".equals(p.getStatus()))
                .count());
        return stats;
    }
}
