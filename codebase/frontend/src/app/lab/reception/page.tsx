"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";

export default function LabReception() {
  const [samples, setSamples] = useState([
    { id: "s-1", barcode: "LAB-2026-A1B2", testName: "Complete Blood Count", patientName: "Alice Smith", collectedAt: "10:15 AM", status: "Pending_Collection" },
    { id: "s-2", barcode: "LAB-2026-X9Y8", testName: "Liver Function Test", patientName: "John Doe", collectedAt: "09:45 AM", status: "Pending_Collection" }
  ]);
  const [rejectReason, setRejectReason] = useState("");
  const [selectedSample, setSelectedSample] = useState<string | null>(null);

  const handleAction = (id: string, action: "accept" | "reject") => {
    if (action === "reject" && !rejectReason) {
      setSelectedSample(id);
      return;
    }
    
    setSamples(samples.filter(s => s.id !== id));
    setSelectedSample(null);
    setRejectReason("");
    // In real app: call API
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Sample Reception Desk</h2>
          <p className="text-text-secondary text-sm">Accept or reject collected samples from the phlebotomy queue.</p>
        </div>
      </div>

      <Card>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">Barcode</th>
                <th className="p-4 font-medium text-text-secondary">Patient</th>
                <th className="p-4 font-medium text-text-secondary">Test Order</th>
                <th className="p-4 font-medium text-text-secondary">Collected At</th>
                <th className="p-4 font-medium text-text-secondary text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {samples.map((sample) => (
                <tr key={sample.id} className="hover:bg-surface-hover">
                  <td className="p-4 font-mono font-medium">{sample.barcode}</td>
                  <td className="p-4 font-semibold">{sample.patientName}</td>
                  <td className="p-4">{sample.testName}</td>
                  <td className="p-4 text-text-secondary">{sample.collectedAt}</td>
                  <td className="p-4 text-right space-x-2">
                    {selectedSample === sample.id ? (
                      <div className="flex items-center gap-2 justify-end">
                        <Input placeholder="Reason for rejection" className="w-48 h-8 text-xs" value={rejectReason} onChange={e => setRejectReason(e.target.value)} />
                        <Button size="sm" variant="danger" onClick={() => handleAction(sample.id, 'reject')}>Confirm</Button>
                        <Button size="sm" variant="secondary" onClick={() => setSelectedSample(null)}>Cancel</Button>
                      </div>
                    ) : (
                      <>
                        <Button size="sm" variant="primary" className="bg-success border-success text-white hover:bg-success-dark" onClick={() => handleAction(sample.id, 'accept')}>Accept</Button>
                        <Button size="sm" variant="danger" onClick={() => setSelectedSample(sample.id)}>Reject</Button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
              {samples.length === 0 && (
                <tr><td colSpan={5} className="p-8 text-center text-text-secondary">No samples awaiting reception.</td></tr>
              )}
            </tbody>
          </table>
        </CardContent>
      </Card>
    </div>
  );
}
