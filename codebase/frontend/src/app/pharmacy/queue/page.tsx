"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";

export default function PharmacyQueue() {
  const prescriptions = [
    { id: "RX-9082", patient: "Jane Doe (UHID-2033)", doctor: "Dr. Smith", department: "General Medicine", items: 4, time: "10 mins ago" },
    { id: "RX-9083", patient: "Robert King (UHID-4022)", doctor: "Dr. Adams", department: "Cardiology", items: 2, time: "5 mins ago" },
  ];

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Outpatient Dispensing Queue</h2>
          <p className="text-text-secondary text-sm">Fulfill electronic prescriptions sent directly from doctors.</p>
        </div>
      </div>

      <Card>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">Prescription ID</th>
                <th className="p-4 font-medium text-text-secondary">Patient</th>
                <th className="p-4 font-medium text-text-secondary">Doctor & Dept</th>
                <th className="p-4 font-medium text-text-secondary text-center">Items</th>
                <th className="p-4 font-medium text-text-secondary">Time Elapsed</th>
                <th className="p-4 font-medium text-text-secondary text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {prescriptions.map(rx => (
                <tr key={rx.id} className="hover:bg-surface-hover transition-colors">
                  <td className="p-4 font-mono font-medium text-primary">{rx.id}</td>
                  <td className="p-4 font-semibold">{rx.patient}</td>
                  <td className="p-4">
                    <div>{rx.doctor}</div>
                    <div className="text-xs text-text-secondary">{rx.department}</div>
                  </td>
                  <td className="p-4 text-center">
                    <Badge variant="default">{rx.items} Drugs</Badge>
                  </td>
                  <td className="p-4 text-warning-dark">{rx.time}</td>
                  <td className="p-4 text-right">
                    <Button variant="primary" size="sm">Fulfill & Bill (FEFO)</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>
    </div>
  );
}
