"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function PharmacyDashboard() {
  const [stats, setStats] = useState<any>({ totalStockValue: 0, dispensedToday: 0, lowStockItems: 0, expiringItems: 0 });
  const [lowStock, setLowStock] = useState<any[]>([]);
  const [expiring, setExpiring] = useState<any[]>([]);
  
  // Dispense State
  const [searchUhid, setSearchUhid] = useState("");
  const [activePrescriptions, setActivePrescriptions] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      // Mock data for UI since backend logic might return empty initially
      setStats({
        totalStockValue: 125000,
        dispensedToday: 42,
        lowStockItems: 5,
        expiringItems: 2
      });

      setLowStock([
        { id: "1", drugName: "Amoxicillin 250mg", currentQty: 15, reorderLevel: 50 },
        { id: "2", drugName: "Ibuprofen 400mg", currentQty: 20, reorderLevel: 100 }
      ]);

      setExpiring([
        { id: "3", drugName: "Tetanus Toxoid", batch: "B-2023", expiryDate: "2026-07-15", qty: 10 }
      ]);
    } catch (e) {
      console.error(e);
    }
  };

  const searchPrescriptions = async () => {
    if (!searchUhid) return;
    setLoading(true);
    try {
      // 1. Search Patient by UHID to get ID
      const pRes = await api.get(`/patients/search?uhid=${searchUhid}`);
      if (pRes.data && pRes.data.length > 0) {
        const patientId = pRes.data[0].id;
        // 2. Fetch Prescriptions
        const rxRes = await api.get(`/patients/${patientId}/prescriptions`);
        // Filter out already fully dispensed ones (assuming status field or just showing all for MVP)
        setActivePrescriptions(rxRes.data.filter((r: any) => r.status !== 'DISPENSED'));
      } else {
        alert("Patient not found.");
        setActivePrescriptions([]);
      }
    } catch (e) {
      alert("Error finding prescriptions.");
    } finally {
      setLoading(false);
    }
  };

  const dispensePrescription = async (prescriptionId: string) => {
    try {
      await api.post(`/pharmacy/dispense`, {
        prescriptionId,
        dispensedDate: new Date().toISOString()
      });
      alert("Prescription Dispensed Successfully!");
      // Remove from list
      setActivePrescriptions(activePrescriptions.filter(p => p.id !== prescriptionId));
      
      // Update stats mock
      setStats({ ...stats, dispensedToday: stats.dispensedToday + 1 });
    } catch (e) {
      alert("Error dispensing prescription.");
    }
  };

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="border-b border-border pb-4 flex justify-between items-end">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Pharmacy & Dispensary</h2>
          <p className="text-text-secondary text-sm">Manage inventory and dispense patient prescriptions.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        {/* Quick Stats */}
        <div className="md:col-span-4 grid grid-cols-1 md:grid-cols-4 gap-4">
          <Card>
            <CardContent className="p-4 flex flex-col items-center justify-center">
              <div className="text-sm text-text-secondary">Dispensed Today</div>
              <div className="text-3xl font-bold text-primary">{stats.dispensedToday}</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4 flex flex-col items-center justify-center">
              <div className="text-sm text-text-secondary">Total Stock Value</div>
              <div className="text-3xl font-bold text-success">${stats.totalStockValue.toLocaleString()}</div>
            </CardContent>
          </Card>
          <Card className="border-error/20 bg-error/5">
            <CardContent className="p-4 flex flex-col items-center justify-center">
              <div className="text-sm text-error">Low Stock Alerts</div>
              <div className="text-3xl font-bold text-error">{stats.lowStockItems}</div>
            </CardContent>
          </Card>
          <Card className="border-warning/20 bg-warning/5">
            <CardContent className="p-4 flex flex-col items-center justify-center">
              <div className="text-sm text-warning-dark">Expiring Soon (30d)</div>
              <div className="text-3xl font-bold text-warning-dark">{stats.expiringItems}</div>
            </CardContent>
          </Card>
        </div>

        {/* Dispensing Panel */}
        <Card className="md:col-span-3 border-border">
          <CardHeader className="bg-surface border-b border-border">
            <CardTitle>Prescription Dispensing</CardTitle>
          </CardHeader>
          <CardContent className="p-6">
            <div className="flex gap-4 mb-6 max-w-md">
              <Input 
                placeholder="Scan Patient Wristband or Enter UHID..." 
                value={searchUhid}
                onChange={e => setSearchUhid(e.target.value)}
              />
              <Button onClick={searchPrescriptions} disabled={loading}>{loading ? "Searching..." : "Lookup Rx"}</Button>
            </div>

            {activePrescriptions.length === 0 ? (
              <div className="text-center p-12 border-2 border-dashed border-border rounded-lg text-text-secondary">
                <div className="text-4xl mb-3">💊</div>
                <p>Enter a patient UHID to load active prescriptions.</p>
              </div>
            ) : (
              <div className="space-y-6">
                <h3 className="font-semibold text-lg border-b border-border pb-2">Active Prescriptions for {activePrescriptions[0]?.patient?.firstName} {activePrescriptions[0]?.patient?.lastName}</h3>
                
                {activePrescriptions.map(rx => (
                  <Card key={rx.id} className="border-primary/20 shadow-sm">
                    <CardHeader className="py-3 px-4 bg-primary/5 flex flex-row justify-between items-center">
                      <div>
                        <div className="font-bold text-primary-dark">Rx #{rx.id.substring(0,8).toUpperCase()}</div>
                        <div className="text-xs text-text-secondary">Prescribed by Dr. {rx.doctor?.lastName} on {new Date(rx.createdAt).toLocaleString()}</div>
                      </div>
                      <Button onClick={() => dispensePrescription(rx.id)}>Dispense All & Deduct Stock</Button>
                    </CardHeader>
                    <CardContent className="p-0">
                      <table className="w-full text-left border-collapse text-sm">
                        <thead>
                          <tr className="bg-surface text-text-secondary">
                            <th className="py-2 px-4 font-medium">Drug Name</th>
                            <th className="py-2 px-4 font-medium">Strength & Form</th>
                            <th className="py-2 px-4 font-medium">Sig (Instructions)</th>
                            <th className="py-2 px-4 font-medium">Status</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-border">
                          {rx.lines?.map((line: any) => (
                            <tr key={line.id}>
                              <td className="py-3 px-4 font-bold">{line.customDrugName}</td>
                              <td className="py-3 px-4">{line.dosageStrength} {line.dosageForm}</td>
                              <td className="py-3 px-4">{line.route} • {line.frequency} • {line.duration} <br/><span className="text-xs italic text-text-secondary">{line.instructions}</span></td>
                              <td className="py-3 px-4"><Badge variant="warning">Pending</Badge></td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* Inventory Alerts Sidebar */}
        <div className="md:col-span-1 space-y-6">
          <Card className="border-error/20">
            <CardHeader className="py-3 bg-error/5 text-error">
              <CardTitle className="text-sm font-bold flex items-center gap-2">⚠️ Low Stock Alerts</CardTitle>
            </CardHeader>
            <CardContent className="p-0">
              <ul className="divide-y divide-error/10">
                {lowStock.map(item => (
                  <li key={item.id} className="p-3 text-sm">
                    <div className="font-semibold text-text-primary">{item.drugName}</div>
                    <div className="flex justify-between mt-1 text-xs">
                      <span className="text-error font-bold">Qty: {item.currentQty}</span>
                      <span className="text-text-secondary">Min: {item.reorderLevel}</span>
                    </div>
                  </li>
                ))}
              </ul>
            </CardContent>
          </Card>

          <Card className="border-warning/20">
            <CardHeader className="py-3 bg-warning/5 text-warning-dark">
              <CardTitle className="text-sm font-bold flex items-center gap-2">⏳ Expiring Soon</CardTitle>
            </CardHeader>
            <CardContent className="p-0">
              <ul className="divide-y divide-warning/10">
                {expiring.map(item => (
                  <li key={item.id} className="p-3 text-sm">
                    <div className="font-semibold text-text-primary">{item.drugName}</div>
                    <div className="flex justify-between mt-1 text-xs">
                      <span className="text-warning-dark font-bold">Exp: {new Date(item.expiryDate).toLocaleDateString()}</span>
                      <span className="text-text-secondary">Batch: {item.batch}</span>
                    </div>
                  </li>
                ))}
              </ul>
            </CardContent>
          </Card>
        </div>

      </div>
    </div>
  );
}
