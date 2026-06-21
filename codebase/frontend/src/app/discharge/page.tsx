"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

export default function DischargeDashboard() {
  const [selectedPatient, setSelectedPatient] = useState<any>(null);
  const [dischargeType, setDischargeType] = useState("Normal");
  
  // Dummy data for admitted patients pending discharge
  const [admittedPatients, setAdmittedPatients] = useState([
    { id: "MED-2026-00105", name: "Sunita Reddy", age: 45, ward: "General Med", bed: "GM-05", admittedDate: "2026-06-15" },
    { id: "MED-2026-00089", name: "Rahul Verma", age: 62, ward: "Cardiology", bed: "CCU-02", admittedDate: "2026-06-10" },
    { id: "MED-2026-00112", name: "Neha Joshi", age: 28, ward: "Maternity", bed: "MAT-01", admittedDate: "2026-06-18" }
  ]);

  const handleDischarge = () => {
    alert(`Patient ${selectedPatient.name} discharged successfully as '${dischargeType}'!`);
    setAdmittedPatients(admittedPatients.filter(p => p.id !== selectedPatient.id));
    setSelectedPatient(null);
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Discharge Lounge</h1>
          <p className="text-text-secondary text-sm">Process patient discharges, finalize clinical summaries, and book follow-ups</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Left Col: Admitted Patients List */}
        <Card className="lg:col-span-1">
          <CardHeader className="bg-surface border-b border-border">
            <CardTitle className="text-lg">Admitted Patients</CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            {admittedPatients.length === 0 ? (
              <div className="p-8 text-center text-text-secondary">No admitted patients.</div>
            ) : (
              <ul className="divide-y divide-border">
                {admittedPatients.map(patient => (
                  <li 
                    key={patient.id}
                    className={`p-4 cursor-pointer transition-colors hover:bg-surface-hover ${selectedPatient?.id === patient.id ? 'bg-primary-light/20 border-l-4 border-primary' : ''}`}
                    onClick={() => setSelectedPatient(patient)}
                  >
                    <div className="flex justify-between items-start mb-1">
                      <div className="font-bold text-text-primary">{patient.name} ({patient.age})</div>
                      <Badge variant="info">{patient.ward}</Badge>
                    </div>
                    <div className="text-xs text-text-secondary mb-2">MRN: {patient.id} | Bed: {patient.bed}</div>
                    <div className="text-xs">Admitted: {patient.admittedDate}</div>
                  </li>
                ))}
              </ul>
            )}
          </CardContent>
        </Card>

        {/* Right Col: Discharge Form */}
        <Card className="lg:col-span-2">
          <CardHeader className="flex justify-between items-center border-b border-border pb-4">
            <CardTitle className="text-lg">Discharge Sign-off</CardTitle>
            {selectedPatient && <Badge variant="info">{selectedPatient.name}</Badge>}
          </CardHeader>
          <CardContent className="pt-4">
            {selectedPatient ? (
              <div className="space-y-6">
                
                {/* Discharge Type */}
                <div>
                  <h3 className="font-bold mb-3 text-sm">Discharge Type</h3>
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                    {["Normal", "DAMA", "LAMA", "Death"].map(type => (
                      <div 
                        key={type}
                        className={`p-3 text-center border rounded-md cursor-pointer text-sm font-medium transition-colors ${dischargeType === type ? (type === 'Death' ? 'bg-error/20 border-error text-error' : 'bg-primary border-primary text-white') : 'border-border text-text-secondary hover:bg-surface-hover'}`}
                        onClick={() => setDischargeType(type)}
                      >
                        {type}
                      </div>
                    ))}
                  </div>
                  {dischargeType === "DAMA" && <p className="text-xs text-error mt-2">* Discharge Against Medical Advice. Ensure legal waiver is signed.</p>}
                  {dischargeType === "LAMA" && <p className="text-xs text-warning mt-2">* Left Against Medical Advice.</p>}
                </div>

                {/* Clinical Summary */}
                <div>
                  <h3 className="font-bold mb-2 text-sm">Final Clinical Summary</h3>
                  <textarea 
                    className="w-full h-32 p-3 border border-border rounded-md focus:border-primary outline-none resize-none text-sm"
                    placeholder="Enter course in hospital, final diagnosis, condition at discharge, and advice on discharge..."
                    defaultValue={`Patient admitted with acute exacerbation of COPD. Managed with IV steroids, nebulization, and oxygen therapy. Condition improved significantly. Chest clear on auscultation. Vitals stable. Discharging on oral medications.`}
                  />
                </div>

                {/* Follow-up */}
                {dischargeType === "Normal" && (
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <h3 className="font-bold mb-2 text-sm">Follow-up Date (Optional)</h3>
                      <input type="date" className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary" />
                    </div>
                    <div>
                      <h3 className="font-bold mb-2 text-sm">Follow-up Doctor</h3>
                      <select className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary">
                        <option>Dr. Anjali Desai (Pulmonology)</option>
                        <option>Dr. Vikram Singh (Cardiology)</option>
                        <option>Dr. R. Iyer (General Medicine)</option>
                      </select>
                    </div>
                  </div>
                )}

                {/* Finalize Button */}
                <div className="pt-6 border-t border-border flex justify-end gap-3">
                  <Button variant="secondary" onClick={() => setSelectedPatient(null)}>Cancel</Button>
                  <Button variant={dischargeType === "Death" ? "secondary" : "primary"} onClick={handleDischarge}>
                    {dischargeType === "Death" ? "Log Mortality Record" : "Finalize Discharge"}
                  </Button>
                </div>

              </div>
            ) : (
              <div className="py-24 text-center text-text-secondary">
                Select an admitted patient from the left panel to initiate discharge.
              </div>
            )}
          </CardContent>
        </Card>

      </div>
    </div>
  );
}
