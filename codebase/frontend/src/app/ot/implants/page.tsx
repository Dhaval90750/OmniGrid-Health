"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";

export default function ImplantRegister() {
  const implants = [
    { id: "IMP-910", date: "2026-06-23", booking: "OTB-403", patient: "James Bond", implant: "DePuy Synthes Corail Hip Stem", lot: "LOT-88219B", expiry: "2030-12-01" }
  ];

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Implant Register</h2>
          <p className="text-text-secondary text-sm">Regulatory tracking for prosthetics and surgical implants.</p>
        </div>
        <Button variant="primary">Scan Barcode (Add Implant)</Button>
      </div>

      <Card>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">Date</th>
                <th className="p-4 font-medium text-text-secondary">Booking & Patient</th>
                <th className="p-4 font-medium text-text-secondary">Implant Details</th>
                <th className="p-4 font-medium text-text-secondary">Lot Number</th>
                <th className="p-4 font-medium text-text-secondary">Expiry Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {implants.map(i => (
                <tr key={i.id} className="hover:bg-surface-hover">
                  <td className="p-4 text-text-secondary">{i.date}</td>
                  <td className="p-4">
                    <div className="font-mono text-xs">{i.booking}</div>
                    <div className="font-semibold">{i.patient}</div>
                  </td>
                  <td className="p-4 font-bold text-primary-dark">{i.implant}</td>
                  <td className="p-4 font-mono bg-warning/10 border-l border-r border-warning">{i.lot}</td>
                  <td className="p-4">{i.expiry}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>
    </div>
  );
}
