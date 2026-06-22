"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";

export default function NewAdmission() {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    patientUhid: "",
    departmentId: "",
    doctorId: "",
    wardCategory: "General",
    bedId: "",
    provisionalDiagnosis: ""
  });

  const [patient, setPatient] = useState<any>(null);
  const [availableBeds, setAvailableBeds] = useState<any[]>([]);

  const handlePatientSearch = async () => {
    try {
      // Mock patient lookup
      setPatient({ firstName: "Alice", lastName: "Smith", uhid: formData.patientUhid, gender: "Female", age: 45 });
      setStep(2);
    } catch (e) {
      alert("Patient not found");
    }
  };

  const fetchAvailableBeds = async () => {
    try {
      // Mock available beds
      setAvailableBeds([
        { id: "b1", bedNumber: "GEN-101", status: "AVAILABLE" },
        { id: "b2", bedNumber: "GEN-102", status: "AVAILABLE" }
      ]);
    } catch (e) {
      console.error(e);
    }
  };

  const submitAdmission = async () => {
    try {
      // await api.post("/admissions", formData);
      alert("Patient successfully admitted to bed!");
      window.location.href = "/operations/beds";
    } catch (e) {
      alert("Failed to admit patient");
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="border-b border-border pb-4">
        <h2 className="text-2xl font-semibold text-text-primary">Inpatient Admission</h2>
        <p className="text-text-secondary text-sm">Assign a patient to an available hospital bed.</p>
      </div>

      <div className="flex gap-4">
        <div className={`flex-1 h-2 rounded-full ${step >= 1 ? 'bg-primary' : 'bg-surface'}`}></div>
        <div className={`flex-1 h-2 rounded-full ${step >= 2 ? 'bg-primary' : 'bg-surface'}`}></div>
        <div className={`flex-1 h-2 rounded-full ${step >= 3 ? 'bg-primary' : 'bg-surface'}`}></div>
      </div>

      <Card>
        <CardContent className="p-8">
          {step === 1 && (
            <div className="space-y-4 max-w-md mx-auto">
              <h3 className="text-lg font-semibold text-center mb-6">Step 1: Patient Identification</h3>
              <div className="space-y-2">
                <label className="text-sm font-medium">Scan QR or Enter UHID</label>
                <div className="flex gap-2">
                  <Input 
                    placeholder="e.g. UHID-1002" 
                    value={formData.patientUhid} 
                    onChange={e => setFormData({...formData, patientUhid: e.target.value})} 
                  />
                  <Button onClick={handlePatientSearch}>Lookup</Button>
                </div>
              </div>
            </div>
          )}

          {step === 2 && patient && (
            <div className="space-y-6">
              <h3 className="text-lg font-semibold text-center mb-6">Step 2: Admission Details</h3>
              
              <div className="bg-primary/5 p-4 rounded-md border border-primary/20 flex justify-between items-center">
                <div>
                  <div className="font-bold text-lg text-primary-dark">{patient.firstName} {patient.lastName}</div>
                  <div className="text-sm text-text-secondary">{patient.uhid} • {patient.gender} • {patient.age} years</div>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium mb-1 block">Admitting Department</label>
                  <select className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm outline-none focus:border-primary bg-white">
                    <option>General Medicine</option>
                    <option>Cardiology</option>
                    <option>Orthopedics</option>
                  </select>
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Admitting Doctor</label>
                  <select className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm outline-none focus:border-primary bg-white">
                    <option>Dr. Adams</option>
                    <option>Dr. Lee</option>
                  </select>
                </div>
                <div className="col-span-2">
                  <label className="text-sm font-medium mb-1 block">Provisional Diagnosis / Reason for Admission</label>
                  <Input placeholder="e.g. Acute Appendicitis" value={formData.provisionalDiagnosis} onChange={e => setFormData({...formData, provisionalDiagnosis: e.target.value})} />
                </div>
              </div>
              
              <div className="flex justify-end gap-3 pt-4 border-t border-border">
                <Button variant="secondary" onClick={() => setStep(1)}>Back</Button>
                <Button onClick={() => { setStep(3); fetchAvailableBeds(); }}>Next: Bed Assignment</Button>
              </div>
            </div>
          )}

          {step === 3 && (
            <div className="space-y-6">
              <h3 className="text-lg font-semibold text-center mb-6">Step 3: Bed Assignment</h3>
              
              <div>
                <label className="text-sm font-medium mb-1 block">Requested Ward Category</label>
                <select 
                  className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm outline-none focus:border-primary bg-white mb-4"
                  value={formData.wardCategory} 
                  onChange={e => { setFormData({...formData, wardCategory: e.target.value}); fetchAvailableBeds(); }}
                >
                  <option>General</option>
                  <option>ICU</option>
                  <option>Maternity</option>
                </select>

                <label className="text-sm font-medium mb-2 block">Select Available Bed</label>
                <div className="grid grid-cols-3 gap-4">
                  {availableBeds.map(bed => (
                    <div 
                      key={bed.id} 
                      className={`p-4 border rounded-md cursor-pointer text-center font-bold ${formData.bedId === bed.id ? 'bg-primary text-white border-primary' : 'bg-surface hover:border-primary border-border'}`}
                      onClick={() => setFormData({...formData, bedId: bed.id})}
                    >
                      {bed.bedNumber}
                    </div>
                  ))}
                  {availableBeds.length === 0 && (
                    <div className="col-span-3 p-4 text-center text-error border border-error/50 bg-error/5 rounded-md">
                      No beds available in this category.
                    </div>
                  )}
                </div>
              </div>

              <div className="flex justify-between items-center pt-6 border-t border-border">
                <Button variant="secondary" onClick={() => setStep(2)}>Back</Button>
                <Button variant="primary" disabled={!formData.bedId} onClick={submitAdmission}>Confirm Admission</Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
