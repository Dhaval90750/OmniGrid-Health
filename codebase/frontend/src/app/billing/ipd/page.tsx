"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";

export default function IPDBilling() {
  const [admissions, setAdmissions] = useState([
    { id: "adm-1", patientName: "Alice Smith", uhid: "UHID-2041", ward: "Intensive Care Unit", bed: "ICU-01", admissionDate: "2026-06-20", advancePaid: 500.00, interimTotal: 1250.00 }
  ]);

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
                    <div className="font-semibold">{adm.patientName}</div>
                    <div className="text-xs text-text-secondary font-mono">{adm.uhid}</div>
                  </td>
                  <td className="p-4">
                    <div>{adm.ward}</div>
                    <div className="text-xs text-text-secondary font-mono">Bed: {adm.bed}</div>
                  </td>
                  <td className="p-4">{adm.admissionDate}</td>
                  <td className="p-4 font-medium text-error-dark">${adm.interimTotal.toFixed(2)}</td>
                  <td className="p-4 font-medium text-success-dark">${adm.advancePaid.toFixed(2)}</td>
                  <td className="p-4">
                    {adm.advancePaid >= adm.interimTotal ? (
                       <Badge variant="success">Covered</Badge>
                    ) : (
                       <Badge variant="default" className="bg-warning/20 text-warning-dark border-warning">Deficit</Badge>
                    )}
                  </td>
                  <td className="p-4 text-right space-x-2">
                    <Button variant="secondary" size="sm">Collect Advance</Button>
                    <Button variant="primary" size="sm">View Interim Bill</Button>
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
