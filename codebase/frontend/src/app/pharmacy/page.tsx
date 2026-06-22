"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function PharmacyDashboard() {
  const [activeTab, setActiveTab] = useState<"queue" | "inventory">("queue");
  const [activePrescription, setActivePrescription] = useState<any>(null);

  const mockPendingPrescriptions = [
    { 
      id: "rx-100", 
      patient: { name: "John Doe", uhid: "UHID-1001" }, 
      doctor: "Dr. Adams", 
      date: "2026-06-22",
      lines: [
        { drug: "Paracetamol 500mg", qty: 20, instructions: "1 tab twice daily for 10 days" },
        { drug: "Amoxicillin 250mg", qty: 15, instructions: "1 cap three times daily for 5 days" }
      ]
    },
    { 
      id: "rx-101", 
      patient: { name: "Jane Smith", uhid: "UHID-2042" }, 
      doctor: "Dr. Lee", 
      date: "2026-06-22",
      lines: [
        { drug: "Ibuprofen 400mg", qty: 10, instructions: "1 tab as needed for pain" }
      ]
    }
  ];

  const handleDispense = async () => {
    try {
      // In a real flow, you'd send dispensing record with prescription ID
      // await api.post(`/pharmacy/dispense`, { prescription: { id: activePrescription.id }, patient: activePrescription.patient, pharmacist: { id: "user-id" } });
      alert("Prescription successfully dispensed! Stock deducted using FEFO rules.");
      setActivePrescription(null);
    } catch (e) {
      console.error(e);
      alert("Error dispensing prescription. Out of stock?");
    }
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Pharmacy & Inventory</h2>
          <p className="text-text-secondary text-sm">Manage dispensing queues and stock levels.</p>
        </div>
      </div>

      <div className="flex gap-4 border-b border-border pb-2">
        <button 
          className={`px-4 py-2 font-medium text-sm rounded-md transition-colors ${activeTab === 'queue' ? 'bg-primary text-white' : 'text-text-secondary hover:bg-surface-hover'}`}
          onClick={() => setActiveTab('queue')}
        >
          Dispensing Queue
        </button>
        <button 
          className={`px-4 py-2 font-medium text-sm rounded-md transition-colors ${activeTab === 'inventory' ? 'bg-primary text-white' : 'text-text-secondary hover:bg-surface-hover'}`}
          onClick={() => setActiveTab('inventory')}
        >
          Inventory Management
        </button>
      </div>

      {activeTab === "queue" && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Queue List */}
          <Card className="md:col-span-1 border-border shadow-sm">
            <CardHeader className="bg-surface border-b border-border py-3">
              <CardTitle className="text-sm font-semibold">Pending Prescriptions</CardTitle>
            </CardHeader>
            <CardContent className="p-0">
              <div className="divide-y divide-border">
                {mockPendingPrescriptions.map((rx) => (
                  <div 
                    key={rx.id} 
                    className={`p-4 cursor-pointer transition-colors ${activePrescription?.id === rx.id ? 'bg-primary/5 border-l-4 border-primary' : 'hover:bg-surface-hover border-l-4 border-transparent'}`}
                    onClick={() => setActivePrescription(rx)}
                  >
                    <div className="flex justify-between items-start mb-2">
                      <span className="font-semibold text-sm text-text-primary">{rx.patient.name}</span>
                      <Badge variant="warning">Pending</Badge>
                    </div>
                    <div className="text-xs text-text-secondary mb-1">{rx.patient.uhid}</div>
                    <div className="text-xs text-text-secondary flex justify-between">
                      <span>{rx.doctor}</span>
                      <span>{rx.date}</span>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* Active Prescription Details */}
          <div className="md:col-span-2 space-y-6">
            {activePrescription ? (
              <Card className="border-border shadow-md">
                <CardHeader className="bg-primary-light text-primary-dark border-b border-primary/20">
                  <div className="flex justify-between items-center">
                    <CardTitle>Rx Details</CardTitle>
                    <span className="font-mono bg-white px-2 py-1 rounded text-xs">{activePrescription.id}</span>
                  </div>
                </CardHeader>
                <CardContent className="p-6 space-y-6">
                  {/* Patient Info */}
                  <div className="flex justify-between bg-surface p-4 rounded-md border border-border">
                    <div>
                      <div className="text-xs text-text-secondary uppercase mb-1">Patient Name</div>
                      <div className="font-semibold">{activePrescription.patient.name}</div>
                    </div>
                    <div className="text-right">
                      <div className="text-xs text-text-secondary uppercase mb-1">UHID</div>
                      <div className="font-mono">{activePrescription.patient.uhid}</div>
                    </div>
                  </div>

                  {/* Medications */}
                  <div>
                    <h3 className="text-sm font-semibold mb-3 border-b border-border pb-2">Prescribed Medications</h3>
                    <div className="border border-border rounded-md overflow-hidden">
                      <table className="w-full text-left text-sm">
                        <thead className="bg-surface border-b border-border">
                          <tr>
                            <th className="p-3 font-medium text-text-secondary">Drug Name</th>
                            <th className="p-3 font-medium text-text-secondary">Instructions</th>
                            <th className="p-3 font-medium text-text-secondary text-right">Qty Req</th>
                            <th className="p-3 font-medium text-text-secondary text-right">Stock Avg</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-border">
                          {activePrescription.lines.map((line: any, idx: number) => (
                            <tr key={idx} className="hover:bg-surface-hover transition-colors">
                              <td className="p-3 font-medium">{line.drug}</td>
                              <td className="p-3 text-text-secondary">{line.instructions}</td>
                              <td className="p-3 text-right font-mono font-bold">{line.qty}</td>
                              <td className="p-3 text-right">
                                <Badge variant="success">In Stock</Badge>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>

                  <div className="flex justify-end gap-4 pt-4 border-t border-border">
                    <Button variant="secondary" onClick={() => setActivePrescription(null)}>Cancel</Button>
                    <Button variant="primary" onClick={handleDispense}>Dispense & Print Label</Button>
                  </div>
                </CardContent>
              </Card>
            ) : (
              <div className="h-full flex items-center justify-center text-text-secondary border-2 border-dashed border-border rounded-lg bg-surface">
                <div className="text-center p-8">
                  <h3 className="text-lg font-medium mb-2">Pharmacy Workspace</h3>
                  <p className="text-sm">Select a prescription from the queue to process dispensing.</p>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {activeTab === "inventory" && (
        <Card>
          <CardContent className="p-12 text-center text-text-secondary">
            Inventory Management Module (Purchasing, Audits, Adjustments) to be implemented in a future phase.
          </CardContent>
        </Card>
      )}
    </div>
  );
}
