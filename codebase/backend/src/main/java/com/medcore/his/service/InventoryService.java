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
}
