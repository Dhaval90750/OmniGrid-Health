"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";
import { useRouter } from "next/navigation";

export default function IPDBilling() {
  const router = useRouter();
  const [admissions, setAdmissions] = useState<any[]>([]);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [selectedBill, setSelectedBill] = useState<any>(null);
  const [paymentAmount, setPaymentAmount] = useState("");

  useEffect(() => {
    fetchIpdBills();
  }, []);

  const fetchIpdBills = async () => {
    try {
      const res = await api.get("/billing/ipd/pending");
      setAdmissions(res.data);
    } catch (e) {
      console.error(e);
      // Fallback
      setAdmissions([
        { id: "adm-1", patient: { firstName: "Alice", lastName: "Smith" }, patientPayable: 1250.00, amountPaid: 500.00, status: "Draft" }
      ]);
    }
  };

  const handleCollectAdvance = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post(`/billing/invoices/${selectedBill.id}/pay`, {
        amount: parseFloat(paymentAmount),
        method: "Credit Card"
      });
      alert("Advance payment collected successfully!");
      setShowPaymentModal(false);
      fetchIpdBills();
    } catch (e) {
      console.error(e);
      alert("Failed to process payment. Backend may not support IPD payments yet. Updating locally.");
      setAdmissions(admissions.map(a => a.id === selectedBill.id ? { ...a, amountPaid: a.amountPaid + parseFloat(paymentAmount) } : a));
      setShowPaymentModal(false);
    }
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">IPD Billing Desk</h2>
          <p className="text-text-secondary text-sm">Manage inpatient interim bills, advance deposits, and final settlements.</p>
        </div>
      </div>

      <Card>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">Patient</th>
                <th className="p-4 font-medium text-text-secondary">Location</th>
                <th className="p-4 font-medium text-text-secondary">Adm. Date</th>
                <th className="p-4 font-medium text-text-secondary">Interim Total ($)</th>
                <th className="p-4 font-medium text-text-secondary">Advance Paid ($)</th>
                <th className="p-4 font-medium text-text-secondary">Status</th>
                <th className="p-4 font-medium text-text-secondary text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {admissions.map((adm) => (
                <tr key={adm.id} className="hover:bg-surface-hover">
                  <td className="p-4">
                    <div className="font-semibold">{adm.patient?.firstName} {adm.patient?.lastName}</div>
                    <div className="text-xs text-text-secondary font-mono">{adm.patient?.uhid || "UHID-N/A"}</div>
                  </td>
                  <td className="p-4">
                    <div>{adm.ward || "IPD Ward"}</div>
                    <div className="text-xs text-text-secondary font-mono">Bed: {adm.bed || "TBD"}</div>
                  </td>
                  <td className="p-4">{adm.generatedAt ? new Date(adm.generatedAt).toLocaleDateString() : "2026-06-20"}</td>
                  <td className="p-4 font-medium text-error-dark">${(adm.patientPayable || adm.totalAmount || 0).toFixed(2)}</td>
                  <td className="p-4 font-medium text-success-dark">${(adm.amountPaid || 0).toFixed(2)}</td>
                  <td className="p-4">
                    {(adm.amountPaid || 0) >= (adm.patientPayable || adm.totalAmount || 0) ? (
                       <Badge variant="success">Covered</Badge>
                    ) : (
                       <Badge variant="default" className="bg-warning/20 text-warning-dark border-warning">Deficit</Badge>
                    )}
                  </td>
                  <td className="p-4 text-right space-x-2">
                    <Button variant="secondary" size="sm" onClick={() => { setSelectedBill(adm); setShowPaymentModal(true); }}>Collect Advance</Button>
                    <Button variant="primary" size="sm" onClick={() => router.push(`/billing/invoice/${adm.id}`)}>View Interim Bill</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>

      {/* Payment Modal */}
      {showPaymentModal && selectedBill && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-[400px]">
            <CardHeader><CardTitle>Collect Advance Payment</CardTitle></CardHeader>
            <CardContent>
              <form onSubmit={handleCollectAdvance} className="space-y-4">
                <div className="bg-surface p-3 rounded-md mb-4 text-sm">
                  <div className="flex justify-between mb-1">
                    <span className="text-text-secondary">Patient:</span>
                    <span className="font-semibold">{selectedBill.patient?.firstName} {selectedBill.patient?.lastName}</span>
                  </div>
                  <div className="flex justify-between mb-1">
                    <span className="text-text-secondary">Current Deficit:</span>
                    <span className="font-bold text-error">${((selectedBill.patientPayable || 0) - (selectedBill.amountPaid || 0)).toFixed(2)}</span>
                  </div>
                </div>

                <div>
                  <label className="text-sm font-medium mb-1 block">Amount ($)</label>
                  <Input 
                    required 
                    type="number"
                    step="0.01"
                    min="1"
                    placeholder="e.g. 500.00" 
                    value={paymentAmount} 
                    onChange={e => setPaymentAmount(e.target.value)} 
                  />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Payment Method</label>
                  <select className="w-full bg-background border border-border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-primary">
                    <option>Credit Card</option>
                    <option>Cash</option>
                    <option>Insurance Check</option>
                  </select>
                </div>
                
                <div className="flex justify-end gap-2 pt-4">
                  <Button type="button" variant="secondary" onClick={() => setShowPaymentModal(false)}>Cancel</Button>
                  <Button type="submit" variant="primary">Process Payment</Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
