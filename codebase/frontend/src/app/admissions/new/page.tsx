"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";

export default function NewAdmission() {
  const router = useRouter();
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    patientUhid: "",
    doctorId: "",
    wardId: "",
    bedId: "",
    provisionalDiagnosis: ""
  });

  const [patient, setPatient] = useState<any>(null);
  const [availableBeds, setAvailableBeds] = useState<any[]>([]);
  const [doctors, setDoctors] = useState<any[]>([]);
  const [wards, setWards] = useState<any[]>([]);
  
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchMetadata();
  }, []);

  const fetchMetadata = async () => {
    try {
      const [docRes, wardRes] = await Promise.all([
        api.get("/staff?role=DOCTOR"),
        api.get("/wards")
      ]);
      setDoctors(docRes.data);
      setWards(wardRes.data);
      if (docRes.data.length > 0) setFormData(f => ({ ...f, doctorId: docRes.data[0].id }));
      if (wardRes.data.length > 0) setFormData(f => ({ ...f, wardId: wardRes.data[0].id }));
    } catch (e) {
      console.error(e);
    }
  };

  const handlePatientSearch = async () => {
    setLoading(true);
    try {
      const res = await api.get(`/patients/search?uhid=${formData.patientUhid}`);
      if (res.data && res.data.length > 0) {
        setPatient(res.data[0]);
        setStep(2);
      } else {
        alert("Patient not found");
      }
    } catch (e) {
      alert("Error searching for patient");
    } finally {
      setLoading(false);
    }
  };

  const fetchAvailableBeds = async () => {
    try {
      const matrixRes = await api.get("/admin/bed-matrix");
      const wardData = matrixRes.data.wards || [];
      const selectedWard = wardData.find((w: any) => w.wardId === formData.wardId);
      if (selectedWard) {
        setAvailableBeds(selectedWard.beds.filter((b: any) => b.status === "AVAILABLE"));
      } else {
        setAvailableBeds([]);
      }
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    if (step === 3 && formData.wardId) {
      fetchAvailableBeds();
    }
  }, [step, formData.wardId]);

  const submitAdmission = async () => {
    setLoading(true);
    try {
      await api.post("/admissions", {
        patient: { id: patient.id },
        admittingDoctor: { id: formData.doctorId },
        ward: { id: formData.wardId },
        bed: { id: formData.bedId },
        admissionReason: formData.provisionalDiagnosis
      });
      alert("Patient successfully admitted to bed!");
      router.push("/admissions");
    } catch (e) {
      alert("Failed to admit patient");
      console.error(e);
    } finally {
      setLoading(false);
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
                <label className="text-sm font-medium">Enter UHID</label>
                <div className="flex gap-2">
                  <Input 
                    placeholder="e.g. UHID-1002" 
                    value={formData.patientUhid} 
                    onChange={e => setFormData({...formData, patientUhid: e.target.value})} 
                  />
                  <Button onClick={handlePatientSearch} disabled={loading}>{loading ? 'Searching...' : 'Lookup'}</Button>
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
                  <div className="text-sm text-text-secondary">{patient.uhid} • {patient.gender} • {patient.dateOfBirth}</div>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium mb-1 block">Admitting Doctor</label>
                  <select 
                    className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm outline-none focus:border-primary bg-white"
                    value={formData.doctorId}
                    onChange={e => setFormData({...formData, doctorId: e.target.value})}
                  >
                    {doctors.map(d => (
                      <option key={d.id} value={d.id}>Dr. {d.firstName} {d.lastName}</option>
                    ))}
                  </select>
                </div>
                <div className="col-span-2">
                  <label className="text-sm font-medium mb-1 block">Provisional Diagnosis / Reason for Admission</label>
                  <Input 
                    placeholder="e.g. Acute Appendicitis" 
                    value={formData.provisionalDiagnosis} 
                    onChange={e => setFormData({...formData, provisionalDiagnosis: e.target.value})} 
                  />
                </div>
              </div>
              
              <div className="flex justify-end gap-3 pt-4 border-t border-border">
                <Button variant="secondary" onClick={() => setStep(1)}>Back</Button>
                <Button onClick={() => setStep(3)} disabled={!formData.provisionalDiagnosis}>Next: Bed Assignment</Button>
              </div>
            </div>
          )}

          {step === 3 && (
            <div className="space-y-6">
              <h3 className="text-lg font-semibold text-center mb-6">Step 3: Bed Assignment</h3>
              
              <div>
                <label className="text-sm font-medium mb-1 block">Requested Ward</label>
                <select 
                  className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm outline-none focus:border-primary bg-white mb-4"
                  value={formData.wardId} 
                  onChange={e => { setFormData({...formData, wardId: e.target.value, bedId: ""}); }}
                >
                  {wards.map(w => (
                    <option key={w.id} value={w.id}>{w.name}</option>
                  ))}
                </select>

                <label className="text-sm font-medium mb-2 block">Select Available Bed</label>
                <div className="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-6 gap-4">
                  {availableBeds.map(bed => (
                    <div 
                      key={bed.id} 
                      className={`p-4 border rounded-md cursor-pointer text-center font-bold ${formData.bedId === bed.id ? 'bg-primary text-white border-primary' : 'bg-success/10 hover:border-success border-success/30 text-success-dark'}`}
                      onClick={() => setFormData({...formData, bedId: bed.id})}
                    >
                      {bed.bedId}
                    </div>
                  ))}
                  {availableBeds.length === 0 && (
                    <div className="col-span-full p-4 text-center text-error border border-error/50 bg-error/5 rounded-md">
                      No beds available in this ward.
                    </div>
                  )}
                </div>
              </div>

              <div className="flex justify-between items-center pt-6 border-t border-border">
                <Button variant="secondary" onClick={() => setStep(2)}>Back</Button>
                <Button variant="primary" disabled={!formData.bedId || loading} onClick={submitAdmission}>
                  {loading ? 'Processing...' : 'Confirm Admission'}
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
