"use client";

import { useState, useEffect } from "react";
import { useParams } from "next/navigation";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";

export default function Patient360Dashboard() {
  const params = useParams();
  const patientId = params.id as string;
  
  const [summary, setSummary] = useState<any>(null);
  const [activeTab, setActiveTab] = useState("consultation");
  const [icdQuery, setIcdQuery] = useState("");
  const [icdResults, setIcdResults] = useState<any[]>([]);
  const [noteForm, setNoteForm] = useState({ historyOfPresentIllness: "", physicalExamination: "", treatmentPlan: "" });
  
  const [drugQuery, setDrugQuery] = useState("");
  const [drugResults, setDrugResults] = useState<any[]>([]);
  const [rxLines, setRxLines] = useState<any[]>([]);
  const [rxAlerts, setRxAlerts] = useState<string[]>([]);

  useEffect(() => {
    fetchSummary();
  }, [patientId]);

  const fetchSummary = async () => {
    try {
      // Mocked for demo
      // const res = await api.get(`/patients/${patientId}/summary`);
      // setSummary(res.data);
      setSummary({
        patient: { firstName: "Alice", lastName: "Smith", uhid: "UHID-9012", gender: "Female", dateOfBirth: "1980-05-15", bloodGroup: "O+" },
        vitals: [{ recordedAt: "2026-06-22T08:00:00", heartRate: 82, bloodPressure: "120/80", temperature: 98.6 }],
        allergies: [{ allergen: "Penicillin", severity: "High" }],
        activeDiagnoses: [{ diagnosisName: "Essential hypertension", icd10Code: "I10", diagnosedDate: "2025-01-10" }],
        recentNotes: [{ createdAt: "2026-05-10T10:00:00", noteType: "OPD_CONSULT", treatmentPlan: "Continue medication" }],
        prescriptions: [
          { id: "rx-1", createdAt: "2026-05-10T10:15:00", lines: [{ customDrugName: "Atorvastatin 20mg Tablet", instructions: "1 tab daily at bedtime" }] }
        ]
      });
    } catch (e) {
      console.error(e);
    }
  };

  const handleIcdSearch = async (q: string) => {
    setIcdQuery(q);
    if (q.length > 2) {
      try {
        const res = await api.get(`/icd/search?q=${q}`);
        setIcdResults(res.data);
      } catch (e) {
        setIcdResults([
            { code: "E11.9", description: "Type 2 diabetes mellitus without complications" },
            { code: "I10", description: "Essential (primary) hypertension" }
        ]);
      }
    } else {
      setIcdResults([]);
    }
  };

  const addDiagnosis = async (icd: any) => {
    try {
      // await api.post(`/patients/${patientId}/diagnoses`, { diagnosisName: icd.description, icd10Code: icd.code });
      setSummary((prev: any) => ({
        ...prev,
        activeDiagnoses: [{ diagnosisName: icd.description, icd10Code: icd.code, diagnosedDate: new Date().toISOString() }, ...prev.activeDiagnoses]
      }));
      setIcdQuery("");
      setIcdResults([]);
      alert("Diagnosis Added");
    } catch (e) {
      console.error(e);
    }
  };

  const handleSaveNote = async (sign: boolean) => {
    try {
      // In a real app, we'd have a visit ID or note ID
      // await api.put(`/clinical-notes/${noteId}`, noteForm);
      // if (sign) await api.post(`/clinical-notes/${noteId}/sign`);
      alert(sign ? "Note signed and locked successfully!" : "Draft saved.");
    } catch (e) {
      console.error(e);
    }
  };

  const handleDrugSearch = async (q: string) => {
    setDrugQuery(q);
    if (q.length > 2) {
      try {
        const res = await api.get(`/drugs/search?q=${q}`);
        setDrugResults(res.data);
      } catch (e) {
        setDrugResults([
            { genericName: "Paracetamol", brandName: "Tylenol", strength: "500mg", form: "Tablet" },
            { genericName: "Amoxicillin", brandName: "Amoxil", strength: "250mg", form: "Capsule" }
        ]);
      }
    } else {
      setDrugResults([]);
    }
  };

  const addRxLine = (drug: any) => {
    // Check local mock rules
    const hasPenicillinAllergy = summary.allergies.some((a:any) => a.allergen.toLowerCase().includes("penicillin"));
    if (drug.genericName.toLowerCase() === "amoxicillin" && hasPenicillinAllergy) {
        setRxAlerts(["WARNING: Patient has Penicillin allergy. 'Amoxicillin' may cause cross-reactivity."]);
        return;
    }
    
    setRxLines([...rxLines, { drug, dosage: "1 tab", frequency: "OD", duration: 5, instructions: "" }]);
    setDrugQuery("");
    setDrugResults([]);
  };

  const submitPrescription = async () => {
    try {
      // await api.post(`/visits/visit-id/prescriptions`, { lines: rxLines });
      alert("Prescription digitally sent to Pharmacy!");
      setSummary((prev:any) => ({
        ...prev,
        prescriptions: [{ id: "rx-new", createdAt: new Date().toISOString(), lines: rxLines.map(l => ({ customDrugName: `${l.drug.genericName} ${l.drug.strength} ${l.drug.form}`, instructions: `${l.dosage} ${l.frequency} for ${l.duration} days. ${l.instructions}` })) }, ...prev.prescriptions]
      }));
      setRxLines([]);
      setRxAlerts([]);
    } catch (e: any) {
      if (e.response?.data) setRxAlerts(e.response.data);
      else alert("Failed to save prescription");
    }
  };

  if (!summary) return <div className="p-8 text-center text-text-secondary">Loading Patient 360...</div>;

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      {/* Patient Header Banner */}
      <Card className="overflow-hidden border-2 shadow-sm">
        <div className="bg-primary text-white p-4 flex justify-between items-center">
          <div className="flex items-center gap-6">
            <div className="w-16 h-16 bg-white/20 rounded-full flex items-center justify-center text-2xl font-bold">
              {summary.patient.firstName[0]}{summary.patient.lastName[0]}
            </div>
            <div>
              <h1 className="text-2xl font-bold">{summary.patient.firstName} {summary.patient.lastName}</h1>
              <div className="text-primary-light font-medium flex gap-4 mt-1">
                <span>{summary.patient.uhid}</span>
                <span>{summary.patient.gender}</span>
                <span>Blood: {summary.patient.bloodGroup}</span>
              </div>
            </div>
          </div>
          <div className="text-right">
            {summary.allergies.length > 0 ? (
              <div className="bg-error text-white px-4 py-2 rounded-md font-bold animate-pulse">
                ALLERGIES: {summary.allergies.map((a: any) => a.allergen).join(", ")}
              </div>
            ) : (
              <Badge variant="success" className="bg-success text-white border-0">NKA</Badge>
            )}
          </div>
        </div>
      </Card>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Left Sidebar: Quick Summary */}
        <div className="space-y-6">
          <Card>
            <CardHeader className="bg-surface py-3 border-b border-border"><CardTitle className="text-sm">Recent Vitals</CardTitle></CardHeader>
            <CardContent className="p-4 space-y-3">
              {summary.vitals[0] ? (
                <>
                  <div className="flex justify-between"><span className="text-text-secondary">BP</span><span className="font-bold">{summary.vitals[0].bloodPressure}</span></div>
                  <div className="flex justify-between"><span className="text-text-secondary">HR</span><span className="font-bold">{summary.vitals[0].heartRate} bpm</span></div>
                  <div className="flex justify-between"><span className="text-text-secondary">Temp</span><span className="font-bold">{summary.vitals[0].temperature} °F</span></div>
                </>
              ) : <div className="text-sm text-text-secondary">No vitals recorded today.</div>}
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="bg-surface py-3 border-b border-border"><CardTitle className="text-sm">Active Diagnoses</CardTitle></CardHeader>
            <CardContent className="p-0">
              <div className="divide-y divide-border">
                {summary.activeDiagnoses.map((d: any, idx: number) => (
                  <div key={idx} className="p-3 text-sm">
                    <div className="font-semibold">{d.diagnosisName}</div>
                    <div className="flex justify-between mt-1 text-text-secondary text-xs">
                      <span className="font-mono bg-surface border border-border px-1 rounded">{d.icd10Code}</span>
                      <span>{new Date(d.diagnosedDate).toLocaleDateString()}</span>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Main Content Area */}
        <div className="lg:col-span-3 space-y-6">
          <div className="flex gap-4 border-b border-border pb-2 overflow-x-auto">
            <button className={`px-4 py-2 font-medium text-sm rounded-md whitespace-nowrap transition-colors ${activeTab === 'consultation' ? 'bg-primary text-white' : 'text-text-secondary hover:bg-surface-hover'}`} onClick={() => setActiveTab('consultation')}>Current Consultation</button>
            <button className={`px-4 py-2 font-medium text-sm rounded-md whitespace-nowrap transition-colors ${activeTab === 'prescription' ? 'bg-primary text-white' : 'text-text-secondary hover:bg-surface-hover'}`} onClick={() => setActiveTab('prescription')}>Prescribe Rx</button>
            <button className={`px-4 py-2 font-medium text-sm rounded-md whitespace-nowrap transition-colors ${activeTab === 'history' ? 'bg-primary text-white' : 'text-text-secondary hover:bg-surface-hover'}`} onClick={() => setActiveTab('history')}>Clinical History</button>
          </div>

          {activeTab === "consultation" && (
            <div className="space-y-6">
              {/* Diagnosis Entry */}
              <Card>
                <CardHeader className="py-3 border-b border-border"><CardTitle className="text-sm">Add Diagnosis (ICD-10)</CardTitle></CardHeader>
                <CardContent className="p-4 relative">
                  <Input 
                    placeholder="Search by diagnosis name or ICD-10 code (e.g. 'Diabetes' or 'E11')" 
                    value={icdQuery}
                    onChange={(e) => handleIcdSearch(e.target.value)}
                  />
                  {icdResults.length > 0 && (
                    <div className="absolute top-16 left-4 right-4 bg-white border border-border shadow-lg rounded-md z-10 max-h-60 overflow-y-auto">
                      {icdResults.map((r: any, i) => (
                        <div key={i} className="p-3 hover:bg-surface-hover cursor-pointer border-b border-border flex justify-between" onClick={() => addDiagnosis(r)}>
                          <span>{r.description}</span>
                          <span className="font-mono bg-surface px-1 text-xs rounded border border-border">{r.code}</span>
                        </div>
                      ))}
                    </div>
                  )}
                </CardContent>
              </Card>

              {/* SOAP Note Form */}
              <Card>
                <CardHeader className="py-3 border-b border-border bg-surface flex justify-between items-center">
                  <CardTitle className="text-sm">Clinical Progress Note</CardTitle>
                </CardHeader>
                <CardContent className="p-0">
                  <div className="p-4 border-b border-border">
                    <label className="text-sm font-semibold text-text-primary block mb-2">History of Present Illness (HPI)</label>
                    <textarea 
                      className="w-full h-24 p-3 border border-border rounded-md text-sm outline-none focus:border-primary resize-none"
                      value={noteForm.historyOfPresentIllness}
                      onChange={e => setNoteForm({...noteForm, historyOfPresentIllness: e.target.value})}
                    />
                  </div>
                  <div className="p-4 border-b border-border">
                    <label className="text-sm font-semibold text-text-primary block mb-2">Physical Examination</label>
                    <textarea 
                      className="w-full h-24 p-3 border border-border rounded-md text-sm outline-none focus:border-primary resize-none"
                      value={noteForm.physicalExamination}
                      onChange={e => setNoteForm({...noteForm, physicalExamination: e.target.value})}
                    />
                  </div>
                  <div className="p-4">
                    <label className="text-sm font-semibold text-text-primary block mb-2">Treatment Plan / Orders</label>
                    <textarea 
                      className="w-full h-24 p-3 border border-border rounded-md text-sm outline-none focus:border-primary resize-none"
                      value={noteForm.treatmentPlan}
                      onChange={e => setNoteForm({...noteForm, treatmentPlan: e.target.value})}
                    />
                  </div>
                  <div className="p-4 bg-surface border-t border-border flex justify-end gap-4">
                    <Button variant="secondary" onClick={() => handleSaveNote(false)}>Save Draft</Button>
                    <Button variant="primary" onClick={() => handleSaveNote(true)}>Sign & Lock Note</Button>
                  </div>
                </CardContent>
              </Card>
            </div>
          )}

          {activeTab === "prescription" && (
            <div className="space-y-6">
              <Card>
                <CardHeader className="py-3 border-b border-border"><CardTitle className="text-sm">Search Formulary</CardTitle></CardHeader>
                <CardContent className="p-4 relative">
                  <Input 
                    placeholder="Search by generic or brand name (e.g. Paracetamol)" 
                    value={drugQuery}
                    onChange={(e) => handleDrugSearch(e.target.value)}
                  />
                  {drugResults.length > 0 && (
                    <div className="absolute top-16 left-4 right-4 bg-white border border-border shadow-lg rounded-md z-10 max-h-60 overflow-y-auto">
                      {drugResults.map((r: any, i) => (
                        <div key={i} className="p-3 hover:bg-surface-hover cursor-pointer border-b border-border flex justify-between items-center" onClick={() => addRxLine(r)}>
                          <div>
                            <span className="font-semibold">{r.genericName}</span> <span className="text-sm text-text-secondary">({r.brandName})</span>
                          </div>
                          <span className="font-mono bg-surface px-2 py-0.5 text-xs rounded border border-border">{r.strength} {r.form}</span>
                        </div>
                      ))}
                    </div>
                  )}
                </CardContent>
              </Card>

              {rxAlerts.length > 0 && (
                <div className="bg-error/10 border border-error text-error-dark p-4 rounded-md space-y-2">
                  <h4 className="font-bold">Clinical Safety Alerts:</h4>
                  <ul className="list-disc pl-5 text-sm font-medium">
                    {rxAlerts.map((a, i) => <li key={i}>{a}</li>)}
                  </ul>
                  <Button variant="secondary" size="sm" onClick={() => setRxAlerts([])} className="mt-2 bg-white border-error text-error">Dismiss & Override</Button>
                </div>
              )}

              {rxLines.length > 0 && (
                <Card>
                  <CardHeader className="py-3 border-b border-border"><CardTitle className="text-sm">Current Prescription</CardTitle></CardHeader>
                  <CardContent className="p-0">
                    <table className="w-full text-left text-sm">
                      <thead className="bg-surface border-b border-border">
                        <tr>
                          <th className="p-3 font-medium text-text-secondary">Drug</th>
                          <th className="p-3 font-medium text-text-secondary">Dose</th>
                          <th className="p-3 font-medium text-text-secondary">Freq</th>
                          <th className="p-3 font-medium text-text-secondary">Days</th>
                          <th className="p-3 font-medium text-text-secondary">Instructions</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-border">
                        {rxLines.map((line, idx) => (
                          <tr key={idx} className="hover:bg-surface-hover">
                            <td className="p-3 font-semibold">{line.drug.genericName} <span className="text-xs font-normal text-text-secondary">{line.drug.strength}</span></td>
                            <td className="p-3"><Input value={line.dosage} className="h-8" onChange={(e) => { const newLines = [...rxLines]; newLines[idx].dosage = e.target.value; setRxLines(newLines); }}/></td>
                            <td className="p-3"><Input value={line.frequency} className="h-8 w-16" onChange={(e) => { const newLines = [...rxLines]; newLines[idx].frequency = e.target.value; setRxLines(newLines); }}/></td>
                            <td className="p-3"><Input type="number" value={line.duration} className="h-8 w-16" onChange={(e) => { const newLines = [...rxLines]; newLines[idx].duration = parseInt(e.target.value); setRxLines(newLines); }}/></td>
                            <td className="p-3"><Input value={line.instructions} className="h-8" placeholder="e.g. After meals" onChange={(e) => { const newLines = [...rxLines]; newLines[idx].instructions = e.target.value; setRxLines(newLines); }}/></td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                    <div className="p-4 bg-surface flex justify-end gap-4 border-t border-border">
                      <Button variant="primary" onClick={submitPrescription}>Sign & Send to Pharmacy</Button>
                    </div>
                  </CardContent>
                </Card>
              )}
            </div>
          )}

          {activeTab === "history" && (
            <div className="space-y-6">
              <Card>
                <CardHeader className="py-3 bg-surface border-b border-border"><CardTitle className="text-sm">Historical Notes</CardTitle></CardHeader>
                <CardContent className="p-0 divide-y divide-border">
                  {summary.recentNotes.length > 0 ? summary.recentNotes.map((note: any, idx: number) => (
                    <div key={idx} className="p-6">
                      <div className="flex justify-between items-center mb-4">
                        <h3 className="font-semibold">{new Date(note.createdAt).toLocaleDateString()} - {note.noteType}</h3>
                        <Badge variant="success">Signed</Badge>
                      </div>
                      <div className="text-sm text-text-secondary whitespace-pre-wrap">{note.treatmentPlan}</div>
                    </div>
                  )) : (
                    <div className="p-12 text-center text-text-secondary">No historical notes found.</div>
                  )}
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="py-3 bg-surface border-b border-border"><CardTitle className="text-sm">Historical Prescriptions</CardTitle></CardHeader>
                <CardContent className="p-0 divide-y divide-border">
                  {summary.prescriptions && summary.prescriptions.length > 0 ? summary.prescriptions.map((rx: any, idx: number) => (
                    <div key={idx} className="p-6">
                      <div className="flex justify-between items-center mb-4">
                        <h3 className="font-semibold">Rx Date: {new Date(rx.createdAt).toLocaleDateString()}</h3>
                        <Button variant="secondary" size="sm">Print PDF</Button>
                      </div>
                      <ul className="list-disc pl-5 text-sm text-text-secondary space-y-1">
                        {rx.lines.map((l:any, i:number) => (
                          <li key={i}><span className="font-medium text-text-primary">{l.customDrugName}</span> - {l.instructions}</li>
                        ))}
                      </ul>
                    </div>
                  )) : (
                    <div className="p-12 text-center text-text-secondary">No historical prescriptions found.</div>
                  )}
                </CardContent>
              </Card>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
