"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";

export default function ImplantRegister() {
  const [showModal, setShowModal] = useState(false);
  const [implants, setImplants] = useState([
    { id: "IMP-910", date: "2026-06-23", booking: "OTB-403", patient: "James Bond", implant: "DePuy Synthes Corail Hip Stem", lot: "LOT-88219B", expiry: "2030-12-01" }
  ]);
  const [barcodeInput, setBarcodeInput] = useState("");

  const handleScan = (e: React.FormEvent) => {
    e.preventDefault();
    if (!barcodeInput) return;
    
    // Simulate barcode extraction
    const parts = barcodeInput.split('|');
    const newImplant = {
      id: `IMP-${Math.floor(Math.random() * 1000)}`,
      date: new Date().toISOString().split('T')[0],
      booking: "OTB-NEW", // Simulated
      patient: "Unknown (Scan)",
      implant: parts[0] || "Generic Implant",
      lot: parts[1] || `LOT-${Math.floor(Math.random() * 10000)}`,
      expiry: parts[2] || "2035-12-31"
    };

    setImplants([newImplant, ...implants]);
    setShowModal(false);
    setBarcodeInput("");
    alert("Implant successfully scanned and registered!");
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Implant Register</h2>
          <p className="text-text-secondary text-sm">Regulatory tracking for prosthetics and surgical implants.</p>
        </div>
        <Button variant="primary" onClick={() => setShowModal(true)}>Scan Barcode (Add Implant)</Button>
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

      {/* Barcode Scan Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-[400px]">
            <CardHeader><CardTitle>Scan Implant Barcode</CardTitle></CardHeader>
            <CardContent>
              <form onSubmit={handleScan} className="space-y-4">
                <p className="text-sm text-text-secondary mb-2">Focus your scanner or type the lot string manually (e.g. ImplantName|LOT123|Expiry)</p>
                <Input 
                  required 
                  autoFocus
                  placeholder="Scan barcode..." 
                  value={barcodeInput} 
                  onChange={e => setBarcodeInput(e.target.value)} 
                />
                
                <div className="flex justify-end gap-2 pt-4">
                  <Button type="button" variant="secondary" onClick={() => setShowModal(false)}>Cancel</Button>
                  <Button type="submit" variant="primary">Process Scan</Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
