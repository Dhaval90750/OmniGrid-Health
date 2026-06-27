"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";
import { useAuthStore } from "@/store/useAuthStore";

export default function InventoryDashboard() {
  const [activeTab, setActiveTab] = useState("items");
  const user = useAuthStore(state => state.user);

  // States
  const [items, setItems] = useState<any[]>([]);
  const [vendors, setVendors] = useState<any[]>([]);
  const [purchaseOrders, setPurchaseOrders] = useState<any[]>([]);

  // Modal States
  const [showItemModal, setShowItemModal] = useState(false);
  const [showVendorModal, setShowVendorModal] = useState(false);
  const [showPoModal, setShowPoModal] = useState(false);

  // Form States
  const [newItem, setNewItem] = useState({ itemName: "", itemCode: "", category: "MEDICAL_SUPPLY", unit: "BOX", quantityInStock: 0, reorderLevel: 10, unitPrice: 0 });
  const [newVendor, setNewVendor] = useState({ vendorName: "", contactPerson: "", email: "", phone: "", address: "" });
  const [newPo, setNewPo] = useState({ vendorId: "", items: [{ itemId: "", quantity: 1, unitPrice: 0 }] });

  useEffect(() => {
    if (user) {
      fetchItems();
      fetchVendors();
      fetchPurchaseOrders();
    }
  }, [user]);

  const fetchItems = async () => {
    try {
      const res = await api.get("/inventory/items");
      setItems(res.data);
    } catch (e) {
      setItems([]);
    }
  };

  const fetchVendors = async () => {
    try {
      const res = await api.get("/inventory/vendors");
      setVendors(res.data);
    } catch (e) {
      setVendors([]);
    }
  };

  const fetchPurchaseOrders = async () => {
    try {
      const res = await api.get("/inventory/purchase-orders");
      setPurchaseOrders(res.data);
    } catch (e) {
      setPurchaseOrders([]);
    }
  };

  // Handlers
  const handleCreateItem = async () => {
    try {
      await api.post("/inventory/items", newItem);
      setShowItemModal(false);
      fetchItems();
    } catch (e) {
      alert("Error creating item");
    }
  };

  const handleCreateVendor = async () => {
    try {
      await api.post("/inventory/vendors", newVendor);
      setShowVendorModal(false);
      fetchVendors();
    } catch (e) {
      alert("Error creating vendor");
    }
  };

  const handleCreatePo = async () => {
    try {
      // Calculate total
      let total = 0;
      newPo.items.forEach(i => total += (i.quantity * i.unitPrice));

      await api.post("/inventory/purchase-orders", {
        vendor: { id: newPo.vendorId },
        poNumber: `PO-${Date.now()}`,
        status: "DRAFT",
        totalAmount: total,
        expectedDeliveryDate: new Date(Date.now() + 7*24*60*60*1000).toISOString() // 7 days from now
      });
      setShowPoModal(false);
      fetchPurchaseOrders();
    } catch (e) {
      alert("Error creating PO");
    }
  };

  // PO Item Line handlers
  const addPoLine = () => {
    setNewPo({ ...newPo, items: [...newPo.items, { itemId: "", quantity: 1, unitPrice: 0 }] });
  };
  const updatePoLine = (index: number, field: string, value: any) => {
    const updatedItems = [...newPo.items];
    updatedItems[index] = { ...updatedItems[index], [field]: value };
    setNewPo({ ...newPo, items: updatedItems });
  };
  const removePoLine = (index: number) => {
    const updatedItems = newPo.items.filter((_, i) => i !== index);
    setNewPo({ ...newPo, items: updatedItems });
  };

  if (!user) return <div className="p-8 text-center">Loading...</div>;

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Inventory Command Center</h2>
          <p className="text-text-secondary text-sm">Manage hospital stock, suppliers, and purchase orders.</p>
        </div>
        <div className="flex gap-2">
          {activeTab === 'items' && <Button onClick={() => setShowItemModal(true)}>+ New Item</Button>}
          {activeTab === 'vendors' && <Button onClick={() => setShowVendorModal(true)}>+ New Vendor</Button>}
          {activeTab === 'po' && <Button onClick={() => setShowPoModal(true)}>+ Raise PO</Button>}
        </div>
      </div>

      <div className="flex gap-4 border-b border-border">
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'items' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('items')}
        >
          Item Catalog & Stock
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'vendors' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('vendors')}
        >
          Vendor Management
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'po' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('po')}
        >
          Purchase Orders
        </button>
      </div>

      {activeTab === 'items' && (
        <Card>
          <CardContent className="p-0">
            {items.length === 0 ? (
              <div className="p-8 text-center text-text-secondary">No items found.</div>
            ) : (
              <table className="w-full text-left text-sm border-collapse">
                <thead className="bg-surface border-b border-border">
                  <tr>
                    <th className="p-4">Item Code</th>
                    <th className="p-4">Name</th>
                    <th className="p-4">Category</th>
                    <th className="p-4">Stock</th>
                    <th className="p-4">Reorder Lvl</th>
                    <th className="p-4">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {items.map(item => (
                    <tr key={item.id} className="hover:bg-surface-hover">
                      <td className="p-4 font-mono text-xs">{item.itemCode}</td>
                      <td className="p-4 font-bold">{item.itemName}</td>
                      <td className="p-4">{item.category}</td>
                      <td className="p-4 font-bold">{item.quantityInStock} {item.unit}</td>
                      <td className="p-4">{item.reorderLevel} {item.unit}</td>
                      <td className="p-4">
                        {item.quantityInStock <= item.reorderLevel ? (
                          <Badge variant="error">Low Stock</Badge>
                        ) : (
                          <Badge variant="success">Optimal</Badge>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </CardContent>
        </Card>
      )}

      {activeTab === 'vendors' && (
        <Card>
          <CardContent className="p-0">
            {vendors.length === 0 ? (
              <div className="p-8 text-center text-text-secondary">No vendors found.</div>
            ) : (
              <table className="w-full text-left text-sm border-collapse">
                <thead className="bg-surface border-b border-border">
                  <tr>
                    <th className="p-4">Vendor Name</th>
                    <th className="p-4">Contact Person</th>
                    <th className="p-4">Email</th>
                    <th className="p-4">Phone</th>
                    <th className="p-4">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {vendors.map(v => (
                    <tr key={v.id} className="hover:bg-surface-hover">
                      <td className="p-4 font-bold">{v.vendorName}</td>
                      <td className="p-4">{v.contactPerson}</td>
                      <td className="p-4">{v.email}</td>
                      <td className="p-4">{v.phone}</td>
                      <td className="p-4">
                        {v.isActive ? <Badge variant="success">Active</Badge> : <Badge variant="error">Inactive</Badge>}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </CardContent>
        </Card>
      )}

      {activeTab === 'po' && (
        <Card>
          <CardContent className="p-0">
            {purchaseOrders.length === 0 ? (
              <div className="p-8 text-center text-text-secondary">No purchase orders found.</div>
            ) : (
              <table className="w-full text-left text-sm border-collapse">
                <thead className="bg-surface border-b border-border">
                  <tr>
                    <th className="p-4">PO Number</th>
                    <th className="p-4">Date</th>
                    <th className="p-4">Vendor</th>
                    <th className="p-4">Total Amount</th>
                    <th className="p-4">Expected Delivery</th>
                    <th className="p-4">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {purchaseOrders.map(po => (
                    <tr key={po.id} className="hover:bg-surface-hover">
                      <td className="p-4 font-bold text-primary">{po.poNumber}</td>
                      <td className="p-4">{new Date(po.createdAt).toLocaleDateString()}</td>
                      <td className="p-4">{po.vendor?.vendorName}</td>
                      <td className="p-4 font-bold">₹{po.totalAmount}</td>
                      <td className="p-4">{new Date(po.expectedDeliveryDate).toLocaleDateString()}</td>
                      <td className="p-4"><Badge>{po.status}</Badge></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </CardContent>
        </Card>
      )}

      {/* Item Modal */}
      {showItemModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader><CardTitle>Add New Item</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <Input placeholder="Item Name" value={newItem.itemName} onChange={e => setNewItem({...newItem, itemName: e.target.value})} />
              <Input placeholder="Item Code (e.g. ITM-001)" value={newItem.itemCode} onChange={e => setNewItem({...newItem, itemCode: e.target.value})} />
              <select className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm bg-white" value={newItem.category} onChange={e => setNewItem({...newItem, category: e.target.value})}>
                <option value="MEDICAL_SUPPLY">Medical Supply</option>
                <option value="PHARMACY">Pharmacy</option>
                <option value="SURGICAL">Surgical</option>
                <option value="STATIONERY">Stationery</option>
              </select>
              <div className="grid grid-cols-2 gap-4">
                <Input type="number" placeholder="Initial Stock" value={newItem.quantityInStock} onChange={e => setNewItem({...newItem, quantityInStock: parseInt(e.target.value)})} />
                <Input type="number" placeholder="Reorder Level" value={newItem.reorderLevel} onChange={e => setNewItem({...newItem, reorderLevel: parseInt(e.target.value)})} />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <Input placeholder="Unit (e.g. BOX, PCS)" value={newItem.unit} onChange={e => setNewItem({...newItem, unit: e.target.value})} />
                <Input type="number" placeholder="Unit Price" value={newItem.unitPrice} onChange={e => setNewItem({...newItem, unitPrice: parseFloat(e.target.value)})} />
              </div>
              <div className="flex justify-end gap-2 pt-4 border-t border-border mt-4">
                <Button variant="secondary" onClick={() => setShowItemModal(false)}>Cancel</Button>
                <Button variant="primary" onClick={handleCreateItem}>Save Item</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Vendor Modal */}
      {showVendorModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader><CardTitle>Add New Vendor</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <Input placeholder="Vendor Name" value={newVendor.vendorName} onChange={e => setNewVendor({...newVendor, vendorName: e.target.value})} />
              <Input placeholder="Contact Person" value={newVendor.contactPerson} onChange={e => setNewVendor({...newVendor, contactPerson: e.target.value})} />
              <Input type="email" placeholder="Email" value={newVendor.email} onChange={e => setNewVendor({...newVendor, email: e.target.value})} />
              <Input placeholder="Phone" value={newVendor.phone} onChange={e => setNewVendor({...newVendor, phone: e.target.value})} />
              <Input placeholder="Address" value={newVendor.address} onChange={e => setNewVendor({...newVendor, address: e.target.value})} />
              <div className="flex justify-end gap-2 pt-4 border-t border-border mt-4">
                <Button variant="secondary" onClick={() => setShowVendorModal(false)}>Cancel</Button>
                <Button variant="primary" onClick={handleCreateVendor}>Save Vendor</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* PO Modal */}
      {showPoModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-2xl bg-white">
            <CardHeader><CardTitle>Raise Purchase Order (PO)</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div>
                <label className="text-sm font-bold mb-1 block">Select Vendor</label>
                <select className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm bg-white" value={newPo.vendorId} onChange={e => setNewPo({...newPo, vendorId: e.target.value})}>
                  <option value="">-- Select Vendor --</option>
                  {vendors.map(v => <option key={v.id} value={v.id}>{v.vendorName}</option>)}
                </select>
              </div>

              <div>
                <div className="flex justify-between items-center mb-2">
                  <label className="text-sm font-bold">Line Items</label>
                  <Button variant="secondary" size="sm" onClick={addPoLine}>+ Add Line</Button>
                </div>
                <div className="space-y-2 border border-border p-4 rounded-md max-h-64 overflow-y-auto bg-surface">
                  {newPo.items.map((line, idx) => (
                    <div key={idx} className="flex gap-2 items-center">
                      <select className="flex-1 h-9 px-2 border border-border rounded-md text-sm bg-white" value={line.itemId} onChange={e => updatePoLine(idx, 'itemId', e.target.value)}>
                        <option value="">-- Select Item --</option>
                        {items.map(it => <option key={it.id} value={it.id}>{it.itemName} ({it.unit})</option>)}
                      </select>
                      <Input type="number" placeholder="Qty" className="w-20 h-9" value={line.quantity} onChange={e => updatePoLine(idx, 'quantity', parseInt(e.target.value))} />
                      <Input type="number" placeholder="Price" className="w-24 h-9" value={line.unitPrice} onChange={e => updatePoLine(idx, 'unitPrice', parseFloat(e.target.value))} />
                      <Button variant="outline" size="sm" className="text-error px-2" onClick={() => removePoLine(idx)}>X</Button>
                    </div>
                  ))}
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-4 border-t border-border mt-4">
                <Button variant="secondary" onClick={() => setShowPoModal(false)}>Cancel</Button>
                <Button variant="primary" onClick={handleCreatePo} disabled={!newPo.vendorId || newPo.items.length === 0}>Create Draft PO</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

    </div>
  );
}
