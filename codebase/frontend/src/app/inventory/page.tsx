"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

export default function InventoryDashboard() {
  const [activeTab, setActiveTab] = useState("items");

  // Dummy data
  const items = [
    { code: "ITM-001", name: "Paracetamol 500mg", category: "Pharmaceuticals", stock: 1500, reorder: 500, uom: "Strip" },
    { code: "ITM-002", name: "Surgical Masks (N95)", category: "Consumables", stock: 200, reorder: 500, uom: "Box" },
    { code: "ITM-003", name: "Titanium Knee Implant (L)", category: "Implants", stock: 12, reorder: 10, uom: "Piece" }
  ];

  const vendors = [
    { code: "VND-100", name: "MediCorp Supplies", contact: "Rajiv Menon", phone: "+91-9876543210", rating: 4.5 },
    { code: "VND-101", name: "Surgicals India", contact: "Amitabh Das", phone: "+91-9876543211", rating: 3.8 }
  ];

  const pos = [
    { no: "PO-2026-045", vendor: "MediCorp Supplies", amount: "₹45,000", status: "Draft", date: "2026-06-21" },
    { no: "PO-2026-044", vendor: "Surgicals India", amount: "₹1,20,000", status: "Dispatched", date: "2026-06-18" }
  ];

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Supply Chain & Inventory</h1>
          <p className="text-text-secondary text-sm">Manage item master, vendor relationships, and purchase orders</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-border">
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'items' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('items')}
        >
          Item Master
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'vendors' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('vendors')}
        >
          Vendor Directory
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'pos' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('pos')}
        >
          Purchase Orders
        </button>
      </div>

      {/* Content */}
      {activeTab === "items" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Hospital Catalog (Items)</CardTitle>
              <Button variant="secondary">Add New Item</Button>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Item Code</th>
                  <th className="p-3">Item Name</th>
                  <th className="p-3">Category</th>
                  <th className="p-3">Stock Level</th>
                  <th className="p-3">Status</th>
                </tr>
              </thead>
              <tbody>
                {items.map(item => (
                  <tr key={item.code} className="border-b border-surface-hover">
                    <td className="p-3 font-medium">{item.code}</td>
                    <td className="p-3">{item.name}</td>
                    <td className="p-3"><Badge variant="info">{item.category}</Badge></td>
                    <td className="p-3 font-medium">
                      {item.stock} {item.uom} <span className="text-xs text-text-secondary block">(Reorder: {item.reorder})</span>
                    </td>
                    <td className="p-3">
                      {item.stock <= item.reorder ? <Badge variant="error">Low Stock</Badge> : <Badge variant="success">Healthy</Badge>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "vendors" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Approved Vendors</CardTitle>
              <Button variant="secondary">Onboard Vendor</Button>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Vendor Code</th>
                  <th className="p-3">Vendor Name</th>
                  <th className="p-3">Contact Person</th>
                  <th className="p-3">Phone</th>
                  <th className="p-3">Performance Rating</th>
                </tr>
              </thead>
              <tbody>
                {vendors.map(v => (
                  <tr key={v.code} className="border-b border-surface-hover">
                    <td className="p-3 font-medium">{v.code}</td>
                    <td className="p-3 font-bold">{v.name}</td>
                    <td className="p-3">{v.contact}</td>
                    <td className="p-3">{v.phone}</td>
                    <td className="p-3">
                      <div className="flex items-center gap-1">
                        <span className="text-warning">★</span> <span className="font-medium">{v.rating}/5.0</span>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "pos" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Purchase Orders</CardTitle>
              <Button variant="primary">Generate New PO</Button>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">PO Number</th>
                  <th className="p-3">Date</th>
                  <th className="p-3">Vendor</th>
                  <th className="p-3">Total Amount</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Action</th>
                </tr>
              </thead>
              <tbody>
                {pos.map(po => (
                  <tr key={po.no} className="border-b border-surface-hover">
                    <td className="p-3 font-medium text-primary-dark">{po.no}</td>
                    <td className="p-3">{po.date}</td>
                    <td className="p-3 font-bold">{po.vendor}</td>
                    <td className="p-3">{po.amount}</td>
                    <td className="p-3">
                      {po.status === "Draft" ? <Badge variant="warning">Draft</Badge> : <Badge variant="info">Dispatched</Badge>}
                    </td>
                    <td className="p-3">
                      <Button variant="secondary" size="sm">View details</Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

    </div>
  );
}
