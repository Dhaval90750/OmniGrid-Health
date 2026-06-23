"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";

export default function RadiologyOrderCPOE() {
  const [patient, setPatient] = useState("");
  const [modality, setModality] = useState("");
  const [study, setStudy] = useState("");
  const [indication, setIndication] = useState("");
  const [urgency, setUrgency] = useState("Routine");
  const [transport, setTransport] = useState("Walking");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    alert(`Order Placed: ${modality} ${study} for ${patient}. Urgency: ${urgency}`);
    setPatient(""); setModality(""); setStudy(""); setIndication("");
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Radiology CPOE</h2>
          <p className="text-text-secondary text-sm">Computerized Physician Order Entry for Imaging.</p>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>New Imaging Order</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="text-xs font-medium text-text-secondary mb-1 block">Patient Search</label>
                <Input 
                  placeholder="Scan QR or type UHID/Name..." 
                  value={patient} 
                  onChange={e => setPatient(e.target.value)} 
                  required 
                />
              </div>
              <div>
                <label className="text-xs font-medium text-text-secondary mb-1 block">Modality</label>
                <select 
                  className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary"
                  value={modality}
                  onChange={e => setModality(e.target.value)}
                  required
                >
                  <option value="">Select Modality...</option>
                  <option value="X-Ray">X-Ray (CR/DR)</option>
                  <option value="CT">Computed Tomography (CT)</option>
                  <option value="MRI">Magnetic Resonance (MRI)</option>
                  <option value="USG">Ultrasound (USG)</option>
                  <option value="NM">Nuclear Medicine</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="col-span-2">
                <label className="text-xs font-medium text-text-secondary mb-1 block">Study / Protocol</label>
                <Input 
                  placeholder="e.g. CT Head without Contrast" 
                  value={study} 
                  onChange={e => setStudy(e.target.value)} 
                  required 
                />
              </div>
            </div>

            <div>
              <label className="text-xs font-medium text-text-secondary mb-1 block">Clinical Indication / Reason for Exam</label>
              <textarea 
                className="w-full h-24 p-3 border border-border rounded-md focus:border-primary outline-none resize-none text-sm"
                placeholder="Brief clinical history..."
                value={indication}
                onChange={e => setIndication(e.target.value)}
                required
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="text-xs font-medium text-text-secondary mb-1 block">Urgency</label>
                <div className="flex gap-4">
                  <label className="flex items-center gap-2 text-sm">
                    <input type="radio" name="urgency" checked={urgency === 'Routine'} onChange={() => setUrgency('Routine')} />
                    Routine
                  </label>
                  <label className="flex items-center gap-2 text-sm text-error font-medium">
                    <input type="radio" name="urgency" checked={urgency === 'Stat'} onChange={() => setUrgency('Stat')} />
                    STAT (Emergency)
                  </label>
                </div>
              </div>
              <div>
                <label className="text-xs font-medium text-text-secondary mb-1 block">Patient Transport</label>
                <select 
                  className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary"
                  value={transport}
                  onChange={e => setTransport(e.target.value)}
                >
                  <option value="Walking">Walking</option>
                  <option value="Wheelchair">Wheelchair</option>
                  <option value="Stretcher">Stretcher</option>
                  <option value="Portable">Portable (Do at Bedside)</option>
                </select>
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-4 border-t border-border">
              <Button type="button" variant="secondary" onClick={() => {}}>Cancel</Button>
              <Button type="submit" variant="primary">Place Order</Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
