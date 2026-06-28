package com.medcore.his.service;

import com.medcore.his.domain.inventory.InventoryItem;
import com.medcore.his.domain.inventory.PurchaseOrder;
import com.medcore.his.domain.inventory.Vendor;
import com.medcore.his.repository.InventoryItemRepository;
import com.medcore.his.repository.PurchaseOrderRepository;
import com.medcore.his.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    public InventoryService(InventoryItemRepository inventoryItemRepository, VendorRepository vendorRepository, PurchaseOrderRepository purchaseOrderRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.vendorRepository = vendorRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    public List<InventoryItem> getAllItems() {
        return inventoryItemRepository.findAll();
    }

    @Transactional
    public InventoryItem createItem(InventoryItem item) {
        return inventoryItemRepository.save(item);
    }

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Transactional
    public Vendor createVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder po) {
        return purchaseOrderRepository.save(po);
    }
    
    // -- Purchase Indent Logic --
    @Autowired
    private com.medcore.his.repository.PurchaseIndentRepository purchaseIndentRepository;
    
    public List<com.medcore.his.domain.inventory.PurchaseIndent> getAllPurchaseIndents() {
        return purchaseIndentRepository.findAll();
    }
    
    @Transactional
    public com.medcore.his.domain.inventory.PurchaseIndent createPurchaseIndent(com.medcore.his.domain.inventory.PurchaseIndent indent) {
        if (indent.getLines() != null) {
            indent.getLines().forEach(line -> line.setIndent(indent));
        }
        return purchaseIndentRepository.save(indent);
    }
    
    @Transactional
    public com.medcore.his.domain.inventory.PurchaseIndent approveIndent(java.util.UUID id, String level) {
        com.medcore.his.domain.inventory.PurchaseIndent indent = purchaseIndentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Indent not found"));
            
        if ("HOD".equals(level) && "DRAFT".equals(indent.getStatus())) {
            indent.setStatus("HOD_APPROVED");
        } else if ("FINANCE".equals(level) && "HOD_APPROVED".equals(indent.getStatus())) {
            indent.setStatus("FINANCE_APPROVED");
        }
        return purchaseIndentRepository.save(indent);
    }
    
    // -- GRN Logic --
    @Autowired
    private com.medcore.his.repository.GoodsReceiptNoteRepository grnRepository;
    
    public List<com.medcore.his.domain.inventory.GoodsReceiptNote> getAllGrns() {
        return grnRepository.findAll();
    }
    
    @Transactional
    public com.medcore.his.domain.inventory.GoodsReceiptNote createGrn(com.medcore.his.domain.inventory.GoodsReceiptNote grn) {
        if (grn.getLines() != null) {
            grn.getLines().forEach(line -> {
                line.setGrn(grn);
                // Basic 3-way match logic validation simulation
                if (line.getReceivedQuantity() > line.getOrderedQuantity()) {
                    throw new RuntimeException("Received quantity cannot exceed ordered quantity for 3-way matching");
                }
            });
        }
        return grnRepository.save(grn);
    }
}
