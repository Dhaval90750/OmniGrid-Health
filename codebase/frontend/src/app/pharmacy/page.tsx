"use client";

import { useState } from "react";
import { api } from "@/lib/api";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";

export default function PharmacyDashboard() {
  const [prescriptions, setPrescriptions] = useState<any[]>([
    { id: "RX-501", patientName: "Alice Walker", date: "2026-07-02", status: "PENDING", items: [
      { drug: "Paracetamol 500mg", quantity: 10, batch: "B-231 (Exp: 2027-01)", fefoAlert: false },
      { drug: "Amoxicillin 250mg", quantity: 15, batch: "B-098 (Exp: 2026-08)", fefoAlert: true } // near expiry
    ]},
    { id: "RX-502", patientName: "Bob Smith", date: "2026-07-02", status: "PENDING", items: [
      { drug: "Atorvastatin 20mg", quantity: 30, batch: "B-444 (Exp: 2028-11)", fefoAlert: false }
    ]}
  ]);

  const [cdsAlerts, setCdsAlerts] = useState<any[]>([
    { prescriptionId: "RX-501", message: "Patient has reported Penicillin allergy. Amoxicillin cross-reactivity warning.", severity: "HIGH RISK" }
  ]);

  const handleDispense = async (rxId: string) => {
    try {
      // In a real app, this calls the backend which processes the inventory FEFO deduction
      // await api.post(`/pharmacy/dispense/${rxId}`);
      alert(`Prescription ${rxId} dispensed successfully. Inventory deducted based on FEFO.`);
      setPrescriptions(prescriptions.map(p => p.id === rxId ? { ...p, status: "DISPENSED" } : p));
    } catch (e) {
      console.error(e);
      alert("Failed to dispense prescription.");
    }
  };

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-text-primary">Pharmacy & Dispensing</h1>
        <p className="text-text-secondary mt-1">Manage prescriptions, inventory (FEFO), and clinical decision alerts.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="border-l-4 border-l-warning">
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary">Pending Rx</div>
            <div className="text-3xl font-bold text-text-primary mt-2">12</div>
          </CardContent>
        </Card>
        <Card className="border-l-4 border-l-success">
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary">Dispensed Today</div>
            <div className="text-3xl font-bold text-text-primary mt-2">45</div>
          </CardContent>
        </Card>
        <Card className="border-l-4 border-l-error">
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary">CDS Safety Alerts</div>
            <div className="text-3xl font-bold text-text-primary mt-2">3</div>
          </CardContent>
        </Card>
        <Card className="border-l-4 border-l-primary">
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary">Auto-Reorder Indents</div>
            <div className="text-3xl font-bold text-text-primary mt-2">5</div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="md:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Prescription Queue</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {prescriptions.map((rx, i) => (
                  <div key={i} className="border border-border rounded-md p-4">
                    <div className="flex justify-between items-center mb-3">
                      <div>
                        <span className="font-bold text-lg text-primary">{rx.id}</span>
                        <span className="ml-3 text-text-secondary">{rx.patientName}</span>
                      </div>
                      <Badge variant={rx.status === 'DISPENSED' ? 'success' : 'secondary'}>{rx.status}</Badge>
                    </div>
                    
                    <div className="bg-background-secondary p-3 rounded-md mb-3">
                      <table className="w-full text-sm text-left">
                        <thead>
                          <tr className="text-text-secondary">
                            <th className="pb-2">Drug</th>
                            <th className="pb-2">Qty</th>
                            <th className="pb-2 text-right">FEFO Batch Selected</th>
                          </tr>
                        </thead>
                        <tbody>
                          {rx.items.map((item: any, j: number) => (
                            <tr key={j} className="border-t border-border">
                              <td className="py-2">{item.drug}</td>
                              <td className="py-2">{item.quantity}</td>
                              <td className="py-2 text-right">
                                {item.fefoAlert ? (
                                  <span className="text-warning-dark font-medium flex items-center justify-end gap-1">
                                    ⚠️ {item.batch}
                                  </span>
                                ) : (
                                  <span className="text-text-secondary">{item.batch}</span>
                                )}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                    
                    <div className="flex justify-end mt-2">
                      {rx.status === 'PENDING' ? (
                        <Button variant="primary" onClick={() => handleDispense(rx.id)}>Dispense & Deduct Inventory</Button>
                      ) : (
                        <Button variant="outline" disabled>Dispensed</Button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="space-y-6">
          <Card className="border-error">
            <CardHeader className="bg-error/10 pb-4">
              <CardTitle className="text-error flex items-center gap-2">
                🛑 Clinical Decision Support Alerts
              </CardTitle>
            </CardHeader>
            <CardContent className="pt-4">
              {cdsAlerts.length > 0 ? (
                <div className="space-y-3">
                  {cdsAlerts.map((alert, i) => (
                    <div key={i} className="p-3 bg-error/5 border border-error/20 rounded-md text-sm">
                      <div className="font-bold text-error mb-1">{alert.prescriptionId} - {alert.severity}</div>
                      <div className="text-text-primary">{alert.message}</div>
                      <Button variant="outline" className="mt-2 w-full text-xs h-8">Contact Prescribing Doctor</Button>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-sm text-success text-center py-4">No active safety alerts.</div>
              )}
            </CardContent>
          </Card>
          
          <Card>
            <CardHeader>
              <CardTitle>Inventory Alerts</CardTitle>
            </CardHeader>
            <CardContent>
              <ul className="text-sm space-y-2">
                <li className="flex justify-between border-b pb-1">
                  <span className="text-warning-dark">Amoxicillin 250mg</span>
                  <span className="text-text-secondary">Expiring in 30 days</span>
                </li>
                <li className="flex justify-between border-b pb-1">
                  <span className="text-error">Ibuprofen 400mg</span>
                  <span className="text-text-secondary">Below reorder level (12 left)</span>
                </li>
              </ul>
              <Button variant="secondary" className="w-full mt-4 text-sm">Generate Purchase Indent</Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
