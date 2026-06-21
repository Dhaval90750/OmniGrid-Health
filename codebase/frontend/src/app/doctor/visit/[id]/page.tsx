"use client";

import { useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

export default function ClinicalWorkspace() {
  const { id } = useParams();
  const router = useRouter();
  
  const [hpi, setHpi] = useState("");
  const [examination, setExamination] = useState("");
  const [plan, setPlan] = useState("");
  
  // New State for Diagnosis & Rx
  const [diagnosis, setDiagnosis] = useState("");
  const [rxDrug, setRxDrug] = useState("");
  const [rxDosage, setRxDosage] = useState("");
  const [rxFreq, setRxFreq] = useState("OD");
  const [rxDuration, setRxDuration] = useState("5");
  const [prescriptions, setPrescriptions] = useState<any[]>([]);

  const handleAddRx = () => {
    if (rxDrug && rxDosage) {
      setPrescriptions([...prescriptions, { drug: rxDrug, dosage: rxDosage, freq: rxFreq, duration: rxDuration }]);
      setRxDrug("");
      setRxDosage("");
    }
  };

  const handleSave = () => {
    alert("Clinical Note Saved!");
    router.push("/doctor/dashboard");
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      
      {/* Context Bar */}
      <div className="bg-primary-light text-primary-dark p-4 rounded-lg flex justify-between items-center shadow-sm">
        <div className="flex items-center gap-6">
          <div>
            <div className="text-xs opacity-80 uppercase tracking-wider">Patient</div>
            <div className="font-bold text-lg">Rahul Sharma (34M)</div>
          </div>
          <div>
            <div className="text-xs opacity-80 uppercase tracking-wider">UHID</div>
            <div className="font-bold">MED-2026-000001</div>
          </div>
          <div>
            <div className="text-xs opacity-80 uppercase tracking-wider">Allergies</div>
            <Badge variant="error">Penicillin</Badge>
          </div>
        </div>
        <Button variant="secondary" onClick={() => router.push("/doctor/dashboard")}>End Consult</Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Left Col: Vitals & History */}
        <div className="lg:col-span-1 space-y-6">
          <Card>
            <CardHeader><CardTitle>Vitals (Today)</CardTitle></CardHeader>
            <CardContent className="space-y-3">
              <div className="flex justify-between border-b pb-2">
                <span className="text-text-secondary">BP</span>
                <span className="font-medium">120/80 mmHg</span>
              </div>
              <div className="flex justify-between border-b pb-2">
                <span className="text-text-secondary">Temp</span>
                <span className="font-medium">98.6 °F</span>
              </div>
              <div className="flex justify-between border-b pb-2">
                <span className="text-text-secondary">SpO2</span>
                <span className="font-medium">99%</span>
              </div>
              <div className="flex justify-between">
                <span className="text-text-secondary">Pulse</span>
                <span className="font-medium">72 bpm</span>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Lab Orders</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-2">
                <select className="flex-1 p-2 border border-border rounded-md focus:border-primary outline-none text-sm">
                  <option value="">Select Test...</option>
                  <option value="CBC">Complete Blood Count (CBC)</option>
                  <option value="FBS">Fasting Blood Sugar (FBS)</option>
                  <option value="LIP">Lipid Profile</option>
                  <option value="KFT">Kidney Function Test</option>
                </select>
                <Button variant="secondary" onClick={() => alert("Test Ordered")}>Order</Button>
              </div>
              
              <div className="space-y-2">
                <div className="flex justify-between items-center p-2 bg-surface rounded-md border border-border text-sm">
                  <span className="font-medium">Complete Blood Count</span>
                  <Badge variant="warning">Sample Pending</Badge>
                </div>
                <div className="flex justify-between items-center p-2 bg-surface rounded-md border border-border text-sm">
                  <span className="font-medium">Lipid Profile</span>
                  <Badge variant="success">Completed</Badge>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Radiology Orders</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-2">
                <select className="flex-1 p-2 border border-border rounded-md focus:border-primary outline-none text-sm">
                  <option value="">Select Modality...</option>
                  <option value="X-Ray">X-Ray</option>
                  <option value="CT">CT Scan</option>
                  <option value="MRI">MRI</option>
                  <option value="USG">Ultrasound</option>
                </select>
              </div>
              <div className="flex gap-2">
                 <input type="text" className="flex-1 p-2 border border-border rounded-md focus:border-primary outline-none text-sm" placeholder="Study (e.g. Chest PA)" />
                 <Button variant="secondary" onClick={() => alert("Radiology Study Ordered")}>Order</Button>
              </div>
              
              <div className="space-y-2">
                <div className="flex justify-between items-center p-2 bg-surface rounded-md border border-border text-sm">
                  <div>
                    <div className="font-medium">MRI Brain</div>
                    <div className="text-xs text-text-secondary">Indication: Headache</div>
                  </div>
                  <Badge variant="warning">Scheduled</Badge>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Right Col: Clinical Note Entry */}
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <CardHeader><CardTitle>History of Present Illness (HPI)</CardTitle></CardHeader>
            <CardContent>
              <textarea 
                className="w-full h-32 p-3 border border-border rounded-md focus:border-primary outline-none resize-y"
                placeholder="Patient presents with..."
                value={hpi}
                onChange={(e) => setHpi(e.target.value)}
              />
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Physical Examination</CardTitle></CardHeader>
            <CardContent>
              <textarea 
                className="w-full h-32 p-3 border border-border rounded-md focus:border-primary outline-none resize-y"
                placeholder="O/E..."
                value={examination}
                onChange={(e) => setExamination(e.target.value)}
              />
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Diagnoses</CardTitle></CardHeader>
            <CardContent>
              <div className="flex gap-4 mb-4">
                <input 
                  type="text"
                  className="flex-1 p-2 border border-border rounded-md focus:border-primary outline-none"
                  placeholder="Search ICD-10 or type diagnosis..."
                  value={diagnosis}
                  onChange={(e) => setDiagnosis(e.target.value)}
                />
                <Button variant="secondary" onClick={() => alert("Added Diagnosis")}>+ Add</Button>
              </div>
              <div className="flex gap-2">
                <Badge variant="info">Primary: Essential Hypertension (I10)</Badge>
                <Badge variant="warning">Provisional: Type 2 Diabetes (E11)</Badge>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Prescription Writer</CardTitle></CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-5 gap-4 mb-4">
                <div className="md:col-span-2">
                  <label className="text-xs text-text-secondary">Drug Name</label>
                  <input type="text" className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" placeholder="e.g. Paracetamol 650mg" value={rxDrug} onChange={(e) => setRxDrug(e.target.value)} />
                </div>
                <div>
                  <label className="text-xs text-text-secondary">Dosage</label>
                  <input type="text" className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" placeholder="e.g. 1 Tab" value={rxDosage} onChange={(e) => setRxDosage(e.target.value)} />
                </div>
                <div>
                  <label className="text-xs text-text-secondary">Frequency</label>
                  <select className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" value={rxFreq} onChange={(e) => setRxFreq(e.target.value)}>
                    <option value="OD">OD (Once)</option>
                    <option value="BD">BD (Twice)</option>
                    <option value="TDS">TDS (Thrice)</option>
                    <option value="QID">QID (Four times)</option>
                    <option value="SOS">SOS (As needed)</option>
                  </select>
                </div>
                <div>
                  <label className="text-xs text-text-secondary">Days</label>
                  <input type="number" className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" placeholder="Days" value={rxDuration} onChange={(e) => setRxDuration(e.target.value)} />
                </div>
              </div>
              <Button variant="secondary" onClick={handleAddRx} className="w-full mb-6">+ Add to Prescription</Button>

              {prescriptions.length > 0 && (
                <div className="border border-border rounded-md overflow-hidden">
                  <table className="w-full text-left text-sm">
                    <thead className="bg-surface border-b border-border">
                      <tr>
                        <th className="p-3">Drug</th>
                        <th className="p-3">Dosage</th>
                        <th className="p-3">Freq</th>
                        <th className="p-3">Duration</th>
                      </tr>
                    </thead>
                    <tbody>
                      {prescriptions.map((rx, idx) => (
                        <tr key={idx} className="border-b border-surface-hover">
                          <td className="p-3 font-medium">{rx.drug}</td>
                          <td className="p-3">{rx.dosage}</td>
                          <td className="p-3">{rx.freq}</td>
                          <td className="p-3">{rx.duration} Days</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Treatment Plan</CardTitle></CardHeader>
            <CardContent>
              <textarea 
                className="w-full h-32 p-3 border border-border rounded-md focus:border-primary outline-none resize-y"
                placeholder="Other plan notes, diet, follow-up..."
                value={plan}
                onChange={(e) => setPlan(e.target.value)}
              />
            </CardContent>
          </Card>

          <div className="flex justify-end gap-4">
            <Button variant="secondary">Save Draft</Button>
            <Button variant="primary" onClick={handleSave}>Sign & Finalize Encouter</Button>
          </div>
        </div>

      </div>
    </div>
  );
}
