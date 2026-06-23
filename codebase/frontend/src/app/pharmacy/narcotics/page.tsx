"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

export default function NarcoticRegister() {
  const records = [
    { id: "NR-102", date: "2026-06-23 11:30 AM", type: "ISSUE", drug: "Morphine Sulfate 10mg/ml Injection", batch: "MOR-X99", qty: "-2", reference: "RX-9011 (Patient: Jane Doe)", user: "Pharm. Alice" },
    { id: "NR-101", date: "2026-06-23 09:00 AM", type: "RECEIPT", drug: "Morphine Sulfate 10mg/ml Injection", batch: "MOR-X99", qty: "+50", reference: "PO-4402 (Supplier: MedPharm Inc)", user: "Pharm. Alice" },
    { id: "NR-100", date: "2026-06-22 14:15 PM", type: "ISSUE", drug: "Fentanyl 50mcg Patch", batch: "FEN-20A", qty: "-1", reference: "OT-990 (OT Dept)", user: "Pharm. Bob" },
  ];

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-error pb-4">
        <div>
          <h2 className="text-2xl font-bold text-error-dark flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
            Narcotic & Controlled Substance Register
          </h2>
          <p className="text-text-secondary text-sm">Strict, immutable ledger for all controlled substance stock movements.</p>
        </div>
      </div>

      <Card className="border-border">
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">Date / Time</th>
                <th className="p-4 font-medium text-text-secondary">Type</th>
                <th className="p-4 font-medium text-text-secondary">Drug & Batch</th>
                <th className="p-4 font-medium text-text-secondary text-center">Qty Change</th>
                <th className="p-4 font-medium text-text-secondary">Reference</th>
                <th className="p-4 font-medium text-text-secondary">Pharmacist</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {records.map(record => (
                <tr key={record.id} className="hover:bg-surface-hover">
                  <td className="p-4 whitespace-nowrap text-text-secondary">{record.date}</td>
                  <td className="p-4">
                    {record.type === "RECEIPT" ? <Badge variant="success">RECEIPT</Badge> : <Badge variant="error">ISSUE</Badge>}
                  </td>
                  <td className="p-4">
                    <div className="font-bold text-text-primary">{record.drug}</div>
                    <div className="text-xs font-mono text-text-secondary">Batch: {record.batch}</div>
                  </td>
                  <td className={`p-4 text-center font-bold ${record.type === 'RECEIPT' ? 'text-success-dark' : 'text-error-dark'}`}>
                    {record.qty}
                  </td>
                  <td className="p-4 font-mono text-xs">{record.reference}</td>
                  <td className="p-4 font-semibold">{record.user}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>
    </div>
  );
}
