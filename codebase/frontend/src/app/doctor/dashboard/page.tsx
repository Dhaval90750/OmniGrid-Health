"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";

export default function DoctorDashboard() {
  const [queue, setQueue] = useState<any[]>([]);
  const [activeVisit, setActiveVisit] = useState<any>(null);

  // Note State
  const [hpi, setHpi] = useState("");
  const [examination, setExamination] = useState("");
  const [plan, setPlan] = useState("");
  
  // Diagnosis State
  const [icdSearchTerm, setIcdSearchTerm] = useState("");
  const [icdResults, setIcdResults] = useState<any[]>([]);
  const [diagnoses, setDiagnoses] = useState<any[]>([]);
  const [activeTab, setActiveTab] = useState("SOAP");
  
  // Prescription State
  const [drugSearchTerm, setDrugSearchTerm] = useState("");
  const [drugResults, setDrugResults] = useState<any[]>([]);
  const [prescriptionLines, setPrescriptionLines] = useState<any[]>([]);
  const [drugInteractionAlerts, setDrugInteractionAlerts] = useState<string[]>([]);
  const [newRxLine, setNewRxLine] = useState<any>({
    drugName: "",
    strength: "",
    form: "",
    route: "",
    frequency: "",
    duration: "",
    instructions: ""
  });

  // Lab Order State
  const [labSearchTerm, setLabSearchTerm] = useState("");
  const [labResults, setLabResults] = useState<any[]>([]);
  const [labOrders, setLabOrders] = useState<any[]>([]);

  // Radiology Order State
  const [radModality, setRadModality] = useState("X-Ray");
  const [radStudyName, setRadStudyName] = useState("");
  const [radOrders, setRadOrders] = useState<any[]>([]);

  // Hardcoded doctor ID for demo purposes
  const DOCTOR_ID = "1"; // In a real app, this comes from the logged-in user context

  useEffect(() => {
    fetchQueue();
  }, []);

  const fetchQueue = async () => {
    try {
      // In a real implementation this gets the user's ID from auth context
      const res = await api.get(`/visits/doctor/00000000-0000-0000-0000-000000000000`);
      setQueue(res.data);
    } catch (e) {
      console.error(e);
      // Fallback
      setQueue([
        { id: "v1", tokenNumber: 12, patient: { id: "p1", name: "Alice Smith", uhid: "UHID-9012" }, status: "WAITING", chiefComplaint: "Chest Pain" }
      ]);
    }
  };

  const updateStatus = async (visitId: string, status: string, queueStatus: string) => {
    try {
      await api.put(`/visits/${visitId}/status`, { status, queueStatus });
      
      if (status === "COMPLETED") {
        setQueue(queue.filter(v => v.id !== visitId));
        setActiveVisit(null);
        resetNoteState();
      } else {
        const updated = queue.map(v => v.id === visitId ? { ...v, status, queueStatus } : v);
        setQueue(updated);
        setActiveVisit(updated.find(v => v.id === visitId));
      }
    } catch (e) {
      console.error(e);
      alert("Error updating status");
    }
  };

  const handleCallNext = () => {
    if (queue.length > 0) {
      const next = queue[0];
      updateStatus(next.id, "IN_CONSULTATION", "SERVING");
      resetNoteState();
    }
  };

  const resetNoteState = () => {
    setHpi("");
    setExamination("");
    setPlan("");
    setDiagnoses([]);
    setIcdSearchTerm("");
    setIcdResults([]);
    
    setPrescriptionLines([]);
    setDrugSearchTerm("");
    setDrugResults([]);
    setDrugInteractionAlerts([]);
    setNewRxLine({ drugName: "", strength: "", form: "", route: "", frequency: "", duration: "", instructions: "" });
    
    setLabSearchTerm("");
    setLabResults([]);
    setLabOrders([]);
    
    setRadStudyName("");
    setRadOrders([]);
    
    setActiveTab("SOAP");
  };

  const searchIcd = async (term: string) => {
    setIcdSearchTerm(term);
    if (term.length < 2) {
      setIcdResults([]);
      return;
    }
    try {
      const res = await api.get(`/icd/search?q=${term}`);
      setIcdResults(res.data);
    } catch (e) {
      console.error(e);
    }
  };

  const addDiagnosis = (icd: any) => {
    if (!diagnoses.find(d => d.code === icd.code)) {
      setDiagnoses([...diagnoses, { code: icd.code, description: icd.description, type: "Provisional" }]);
    }
    setIcdSearchTerm("");
    setIcdResults([]);
  };

  const removeDiagnosis = (code: string) => {
    setDiagnoses(diagnoses.filter(d => d.code !== code));
  };

  const searchDrug = async (term: string) => {
    setDrugSearchTerm(term);
    if (term.length < 2) {
      setDrugResults([]);
      return;
    }
    try {
      const res = await api.get(`/drugs/search?q=${term}`);
      setDrugResults(res.data);
    } catch (e) {
      console.error(e);
    }
  };

  const selectDrug = (drug: any) => {
    setNewRxLine({ ...newRxLine, drugName: drug.brandName + " (" + drug.genericName + ")", strength: drug.strength, form: drug.form });
    setDrugSearchTerm("");
    setDrugResults([]);
  };

  const addPrescriptionLine = async () => {
    if (!newRxLine.drugName) return;
    const updatedLines = [...prescriptionLines, { ...newRxLine }];
    setPrescriptionLines(updatedLines);
    
    setNewRxLine({ drugName: "", strength: "", form: "", route: "", frequency: "", duration: "", instructions: "" });
    
    // Check interactions
    try {
      const drugs = updatedLines.map(l => l.drugName);
      // Optional: real API interaction check can go here if implemented on backend side for drafted Rx
      // For now, it will be checked on final submission.
    } catch (e) {
      console.error(e);
    }
  };

  const removeRxLine = (index: number) => {
    const updated = [...prescriptionLines];
    updated.splice(index, 1);
    setPrescriptionLines(updated);
  };

  const searchLab = async (term: string) => {
    setLabSearchTerm(term);
    if (term.length < 2) {
      setLabResults([]);
      return;
    }
    try {
      const res = await api.get(`/lab/tests/search?query=${term}`);
      setLabResults(res.data);
    } catch (e) {
      console.error(e);
    }
  };

  const addLabOrder = (test: any) => {
    if (!labOrders.find(o => o.id === test.id)) {
      setLabOrders([...labOrders, test]);
    }
    setLabSearchTerm("");
    setLabResults([]);
  };

  const removeLabOrder = (id: string) => {
    setLabOrders(labOrders.filter(o => o.id !== id));
  };

  const addRadOrder = () => {
    if (!radStudyName) return;
    setRadOrders([...radOrders, { id: Date.now().toString(), modality: radModality, studyName: radStudyName }]);
    setRadStudyName("");
  };

  const removeRadOrder = (id: string) => {
    setRadOrders(radOrders.filter(o => o.id !== id));
  };

  const handleCompleteVisit = async () => {
    if (!activeVisit) return;
    try {
      // 1. Post Diagnoses
      for (const diag of diagnoses) {
        await api.post(`/patients/${activeVisit.patient.id}/diagnoses`, {
          diagnosisName: diag.description,
          icd10Code: diag.code,
          type: diag.type,
          visitId: activeVisit.id
        });
      }

      // 2. Post Clinical Note 
      await api.post(`/visits/${activeVisit.id}/notes`, {
        historyOfPresentIllness: hpi,
        physicalExamination: examination,
        treatmentPlan: plan
      });

      // 3. Post Prescription
      if (prescriptionLines.length > 0) {
        await api.post(`/visits/${activeVisit.id}/prescriptions`, {
          lines: prescriptionLines.map(l => ({
            customDrugName: l.drugName,
            dosageStrength: l.strength,
            dosageForm: l.form,
            route: l.route,
            frequency: l.frequency,
            duration: l.duration,
            instructions: l.instructions
          }))
        });
      }

      // 4. Post Lab Orders
      if (labOrders.length > 0) {
        await api.post(`/lab/orders`, {
          patient: { id: activeVisit.patient.id },
          visit: { id: activeVisit.id },
          orderingDoctor: { id: DOCTOR_ID },
          tests: labOrders.map(t => ({ id: t.id }))
        });
      }

      // 5. Post Radiology Orders
      for (const order of radOrders) {
        await api.post(`/radiology/orders`, {
          patient: { id: activeVisit.patient.id },
          orderingDoctor: { id: DOCTOR_ID },
          visit: { id: activeVisit.id },
          modality: order.modality,
          studyName: order.studyName,
          priority: "ROUTINE"
        });
      }

      // 6. Remove from local queue UI
      setQueue(queue.filter(v => v.id !== activeVisit.id));
      setActiveVisit(null);
      resetNoteState();
      alert("Visit Completed. Note, Rx, Lab, and Radiology Orders submitted.");
    } catch (e: any) {
      console.error(e);
      if (e.response && e.response.status === 400 && Array.isArray(e.response.data)) {
        setDrugInteractionAlerts(e.response.data);
        alert("Prescription rejected due to safety alerts. Please review the alerts.");
      } else {
        alert("Failed to complete visit. See console for details.");
      }
    }
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Doctor Workspace</h2>
          <p className="text-text-secondary text-sm">Manage your OPD queue and clinical consultations.</p>
        </div>
        <div className="flex gap-4">
          <Card className="bg-surface border-border">
            <CardContent className="p-4 flex items-center gap-4">
              <div className="text-center">
                <div className="text-xs font-semibold text-text-secondary uppercase">Waiting</div>
                <div className="text-xl font-bold">{queue.length}</div>
              </div>
              <div className="h-8 w-px bg-border"></div>
              <div className="text-center">
                <div className="text-xs font-semibold text-text-secondary uppercase">Est. Wait</div>
                <div className="text-xl font-bold">{queue.length * 15}m</div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Queue Panel */}
        <Card className="md:col-span-1 shadow-sm">
          <CardHeader className="bg-surface py-4 border-b border-border">
            <div className="flex justify-between items-center">
              <CardTitle className="text-base font-semibold">Today&apos;s Queue</CardTitle>
              {!activeVisit && queue.length > 0 && (
                <Button size="sm" onClick={handleCallNext}>Call Next</Button>
              )}
            </div>
          </CardHeader>
          <CardContent className="p-0">
            <div className="divide-y divide-border">
              {queue.map((visit) => (
                <div 
                  key={visit.id} 
                  className={`p-4 transition-colors cursor-pointer ${activeVisit?.id === visit.id ? 'bg-primary/5 border-l-4 border-primary' : 'hover:bg-surface-hover border-l-4 border-transparent'}`}
                  onClick={() => { setActiveVisit(visit); resetNoteState(); }}
                >
                  <div className="flex justify-between items-start mb-2">
                    <div className="flex items-center gap-2">
                      <span className="font-bold tabular-nums bg-surface border border-border px-2 py-0.5 rounded text-xs">#{visit.tokenNumber}</span>
                      <span className="font-semibold text-sm">{visit.patient.name}</span>
                    </div>
                    {visit.status === "IN_CONSULTATION" ? <Badge variant="warning">In Room</Badge> : <Badge variant="default">Waiting</Badge>}
                  </div>
                  <div className="text-xs text-text-secondary mb-1">UHID: {visit.patient.uhid}</div>
                  <div className="text-xs text-text-secondary truncate"><span className="font-medium text-text-primary">CC:</span> {visit.chiefComplaint}</div>
                </div>
              ))}
              {queue.length === 0 && (
                <div className="p-8 text-center text-text-secondary text-sm">
                  No pending patients in queue.
                </div>
              )}
            </div>
          </CardContent>
        </Card>

        {/* Active Consultation Panel */}
        <div className="md:col-span-2">
          {activeVisit ? (
            <Card className="h-full border-border shadow-md flex flex-col">
              <CardHeader className="bg-primary-light text-primary-dark border-b border-primary/20">
                <div className="flex justify-between items-center">
                  <div>
                    <CardTitle className="text-lg">Consultation: {activeVisit.patient.name}</CardTitle>
                    <div className="text-sm opacity-80 mt-1">Token #{activeVisit.tokenNumber} • {activeVisit.patient.uhid}</div>
                  </div>
                  <div className="flex gap-2">
                    <Button variant="secondary" size="sm" onClick={() => updateStatus(activeVisit.id, "WAITING", "SKIPPED")}>Skip/Hold</Button>
                    <Button size="sm" variant="primary" onClick={handleCompleteVisit}>Complete Visit</Button>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="p-0 flex-1 flex flex-col">
                <div className="flex border-b border-border">
                  <button className={`px-4 py-3 text-sm font-medium border-b-2 ${activeTab === 'SOAP' ? 'border-primary text-primary' : 'border-transparent text-text-secondary'}`} onClick={() => setActiveTab('SOAP')}>
                    S/O (Notes)
                  </button>
                  <button className={`px-4 py-3 text-sm font-medium border-b-2 ${activeTab === 'ASSESSMENT' ? 'border-primary text-primary' : 'border-transparent text-text-secondary'}`} onClick={() => setActiveTab('ASSESSMENT')}>
                    Assessment (Dx)
                  </button>
                  <button className={`px-4 py-3 text-sm font-medium border-b-2 ${activeTab === 'PLAN' ? 'border-primary text-primary' : 'border-transparent text-text-secondary'}`} onClick={() => setActiveTab('PLAN')}>
                    Plan (Tx)
                  </button>
                </div>
                
                <div className="p-6 overflow-y-auto">
                  {activeTab === 'SOAP' && (
                    <div className="space-y-6">
                      <div>
                        <h3 className="text-sm font-semibold mb-2">Chief Complaint</h3>
                        <div className="p-3 bg-surface rounded-md border border-border text-sm">
                          {activeVisit.chiefComplaint}
                        </div>
                      </div>
                      <div>
                        <h3 className="text-sm font-semibold mb-2">History of Present Illness</h3>
                        <textarea 
                          className="w-full h-32 p-3 border border-border rounded-md text-sm outline-none focus:border-primary resize-none"
                          placeholder="Document the patient's history..."
                          value={hpi}
                          onChange={(e) => setHpi(e.target.value)}
                        ></textarea>
                      </div>
                      <div>
                        <h3 className="text-sm font-semibold mb-2">Physical Examination</h3>
                        <textarea 
                          className="w-full h-32 p-3 border border-border rounded-md text-sm outline-none focus:border-primary resize-none"
                          placeholder="Document examination findings..."
                          value={examination}
                          onChange={(e) => setExamination(e.target.value)}
                        ></textarea>
                      </div>
                    </div>
                  )}

                  {activeTab === 'ASSESSMENT' && (
                    <div className="space-y-6">
                      <div className="relative">
                        <h3 className="text-sm font-semibold mb-2">Search ICD-10 Diagnoses</h3>
                        <Input 
                          placeholder="Type code or description (e.g. asthma, J45...)" 
                          value={icdSearchTerm}
                          onChange={(e) => searchIcd(e.target.value)}
                        />
                        {icdResults.length > 0 && (
                          <div className="absolute z-10 w-full mt-1 bg-white border border-border rounded-md shadow-lg max-h-48 overflow-y-auto">
                            {icdResults.map(res => (
                              <div key={res.code} className="p-3 hover:bg-surface-hover cursor-pointer border-b border-border flex justify-between" onClick={() => addDiagnosis(res)}>
                                <span>{res.description}</span>
                                <Badge variant="secondary">{res.code}</Badge>
                              </div>
                            ))}
                          </div>
                        )}
                      </div>

                      <div>
                        <h3 className="text-sm font-semibold mb-4 border-b border-border pb-2">Active Diagnoses for this Visit</h3>
                        {diagnoses.length === 0 ? (
                          <div className="text-text-secondary text-sm italic">No diagnoses added yet.</div>
                        ) : (
                          <div className="space-y-3">
                            {diagnoses.map(d => (
                              <div key={d.code} className="flex items-center justify-between p-3 border border-border rounded-md bg-surface">
                                <div>
                                  <div className="font-semibold">{d.description} <Badge variant="secondary" className="ml-2">{d.code}</Badge></div>
                                  <div className="text-xs text-text-secondary mt-1">Type: {d.type}</div>
                                </div>
                                <Button variant="outline" size="sm" className="text-error border-error/20" onClick={() => removeDiagnosis(d.code)}>Remove</Button>
                              </div>
                            ))}
                          </div>
                        )}
                      </div>
                    </div>
                  )}

                  {activeTab === 'PLAN' && (
                    <div className="space-y-6">
                       <div>
                        <h3 className="text-sm font-semibold mb-2">Treatment Plan & Instructions</h3>
                        <textarea 
                          className="w-full h-24 p-3 border border-border rounded-md text-sm outline-none focus:border-primary resize-none"
                          placeholder="Document non-pharmacological instructions, lab orders, etc..."
                          value={plan}
                          onChange={(e) => setPlan(e.target.value)}
                        ></textarea>
                      </div>

                      {/* Rx Builder */}
                      <div className="border border-border rounded-md bg-surface p-4">
                        <h3 className="text-sm font-semibold mb-4 flex items-center gap-2">💊 e-Prescription</h3>
                        
                        {drugInteractionAlerts.length > 0 && (
                          <div className="mb-4 p-3 bg-error/10 border border-error text-error rounded-md text-sm">
                            <div className="font-bold mb-1">Safety Alerts Detected:</div>
                            <ul className="list-disc pl-5">
                              {drugInteractionAlerts.map((alert, i) => <li key={i}>{alert}</li>)}
                            </ul>
                          </div>
                        )}

                        <div className="space-y-3 mb-6">
                          <div className="relative">
                            <Input 
                              placeholder="Search drug by generic or brand name..." 
                              value={drugSearchTerm}
                              onChange={(e) => searchDrug(e.target.value)}
                            />
                            {drugResults.length > 0 && (
                              <div className="absolute z-10 w-full mt-1 bg-white border border-border rounded-md shadow-lg max-h-48 overflow-y-auto">
                                {drugResults.map((res: any, idx) => (
                                  <div key={idx} className="p-3 hover:bg-surface-hover cursor-pointer border-b border-border flex justify-between" onClick={() => selectDrug(res)}>
                                    <div><span className="font-bold text-primary">{res.brandName}</span> <span className="text-text-secondary text-sm">({res.genericName})</span></div>
                                    <div className="text-sm">{res.strength} • {res.form}</div>
                                  </div>
                                ))}
                              </div>
                            )}
                          </div>

                          <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                            <Input placeholder="Drug Name" value={newRxLine.drugName} onChange={e => setNewRxLine({...newRxLine, drugName: e.target.value})} className="col-span-2 md:col-span-2 bg-white" />
                            <Input placeholder="Strength" value={newRxLine.strength} onChange={e => setNewRxLine({...newRxLine, strength: e.target.value})} className="bg-white" />
                            <Input placeholder="Form" value={newRxLine.form} onChange={e => setNewRxLine({...newRxLine, form: e.target.value})} className="bg-white" />
                          </div>
                          <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                            <select className="p-2 border border-border rounded-md text-sm bg-white" value={newRxLine.route} onChange={e => setNewRxLine({...newRxLine, route: e.target.value})}>
                              <option value="">Route</option><option>PO (Oral)</option><option>IV</option><option>IM</option><option>Topical</option>
                            </select>
                            <select className="p-2 border border-border rounded-md text-sm bg-white" value={newRxLine.frequency} onChange={e => setNewRxLine({...newRxLine, frequency: e.target.value})}>
                              <option value="">Frequency</option><option>OD (Once)</option><option>BID (Twice)</option><option>TID (Thrice)</option><option>QID (Four times)</option><option>SOS (As needed)</option>
                            </select>
                            <Input placeholder="Duration (e.g. 5 days)" value={newRxLine.duration} onChange={e => setNewRxLine({...newRxLine, duration: e.target.value})} className="bg-white" />
                            <Input placeholder="Instructions" value={newRxLine.instructions} onChange={e => setNewRxLine({...newRxLine, instructions: e.target.value})} className="bg-white" />
                          </div>
                          <div className="flex justify-end">
                            <Button variant="secondary" size="sm" onClick={addPrescriptionLine} disabled={!newRxLine.drugName}>+ Add Drug to Rx</Button>
                          </div>
                        </div>

                        {/* Rx List */}
                        {prescriptionLines.length > 0 && (
                          <div className="border-t border-border pt-4">
                            <h4 className="text-xs font-semibold text-text-secondary uppercase mb-2">Current Prescription</h4>
                            <div className="space-y-2">
                              {prescriptionLines.map((line, idx) => (
                                <div key={idx} className="flex justify-between items-center p-3 bg-white border border-border rounded-md">
                                  <div>
                                    <div className="font-bold text-sm text-primary-dark">{line.drugName} <span className="font-normal text-text-primary">{line.strength} {line.form}</span></div>
                                    <div className="text-xs text-text-secondary mt-1">Sig: {line.route} • {line.frequency} • {line.duration}</div>
                                    {line.instructions && <div className="text-xs italic text-text-tertiary">&quot;{line.instructions}&quot;</div>}
                                  </div>
                                  <Button variant="outline" size="sm" className="text-error border-error/20 px-2" onClick={() => removeRxLine(idx)}>X</Button>
                                </div>
                              ))}
                            </div>
                          </div>
                        )}
                      </div>

                      {/* Lab Orders Builder */}
                      <div className="border border-border rounded-md bg-surface p-4 mt-4">
                        <h3 className="text-sm font-semibold mb-4 flex items-center gap-2">🧪 Lab Orders</h3>
                        
                        <div className="space-y-3 mb-4">
                          <div className="relative">
                            <Input 
                              placeholder="Search lab tests (e.g. CBC, Lipid Profile...)" 
                              value={labSearchTerm}
                              onChange={(e) => searchLab(e.target.value)}
                            />
                            {labResults.length > 0 && (
                              <div className="absolute z-10 w-full mt-1 bg-white border border-border rounded-md shadow-lg max-h-48 overflow-y-auto">
                                {labResults.map((res: any) => (
                                  <div key={res.id} className="p-3 hover:bg-surface-hover cursor-pointer border-b border-border flex justify-between" onClick={() => addLabOrder(res)}>
                                    <div><span className="font-bold text-primary">{res.testCode}</span>: {res.testName}</div>
                                    <div className="text-xs text-text-secondary">{res.category}</div>
                                  </div>
                                ))}
                              </div>
                            )}
                          </div>
                        </div>

                        {/* Lab List */}
                        {labOrders.length > 0 && (
                          <div className="border-t border-border pt-4">
                            <h4 className="text-xs font-semibold text-text-secondary uppercase mb-2">Ordered Tests</h4>
                            <div className="space-y-2">
                              {labOrders.map((order) => (
                                <div key={order.id} className="flex justify-between items-center p-3 bg-white border border-border rounded-md">
                                  <div>
                                    <div className="font-bold text-sm text-primary-dark">{order.testName}</div>
                                    <div className="text-xs text-text-secondary">{order.testCode} • {order.category}</div>
                                  </div>
                                  <Button variant="outline" size="sm" className="text-error border-error/20 px-2" onClick={() => removeLabOrder(order.id)}>X</Button>
                                </div>
                              ))}
                            </div>
                          </div>
                        )}
                      </div>

                      {/* Radiology Orders Builder */}
                      <div className="border border-border rounded-md bg-surface p-4 mt-4">
                        <h3 className="text-sm font-semibold mb-4 flex items-center gap-2">🩻 Radiology Orders</h3>
                        
                        <div className="flex gap-2 mb-4">
                          <select 
                            className="w-32 h-10 px-3 py-2 border border-border rounded-md text-sm bg-white"
                            value={radModality}
                            onChange={(e) => setRadModality(e.target.value)}
                          >
                            <option>X-Ray</option>
                            <option>CT Scan</option>
                            <option>MRI</option>
                            <option>Ultrasound</option>
                          </select>
                          <Input 
                            className="flex-1"
                            placeholder="Study name (e.g. Chest PA, MRI Brain...)" 
                            value={radStudyName}
                            onChange={(e) => setRadStudyName(e.target.value)}
                          />
                          <Button variant="secondary" onClick={addRadOrder}>Add</Button>
                        </div>

                        {/* Rad List */}
                        {radOrders.length > 0 && (
                          <div className="border-t border-border pt-4">
                            <h4 className="text-xs font-semibold text-text-secondary uppercase mb-2">Ordered Studies</h4>
                            <div className="space-y-2">
                              {radOrders.map((order) => (
                                <div key={order.id} className="flex justify-between items-center p-3 bg-white border border-border rounded-md">
                                  <div>
                                    <div className="font-bold text-sm text-primary-dark">{order.studyName}</div>
                                    <div className="text-xs text-text-secondary">{order.modality}</div>
                                  </div>
                                  <Button variant="outline" size="sm" className="text-error border-error/20 px-2" onClick={() => removeRadOrder(order.id)}>X</Button>
                                </div>
                              ))}
                            </div>
                          </div>
                        )}
                      </div>
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          ) : (
             <div className="h-full flex items-center justify-center text-text-secondary border-2 border-dashed border-border rounded-lg bg-surface min-h-[400px]">
              <div className="text-center p-8">
                <div className="text-4xl mb-4">🩺</div>
                <h3 className="text-lg font-medium mb-2">Ready for Next Patient</h3>
                <p className="text-sm mb-6">Select a patient from the queue or call the next token.</p>
                <Button onClick={handleCallNext} disabled={queue.length === 0}>Call Next Patient</Button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
