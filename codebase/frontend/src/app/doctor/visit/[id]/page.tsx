"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";
import { AllergyBanner } from "@/components/AllergyBanner";
import { api } from "@/lib/api";

export default function ClinicalWorkspace() {
  const { id } = useParams();
  const router = useRouter();
  
  const [visit, setVisit] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [hpi, setHpi] = useState("");
  const [pastMedicalHistory, setPastMedicalHistory] = useState("");
  const [familyHistory, setFamilyHistory] = useState("");
  const [socialHistory, setSocialHistory] = useState("");
  const [reviewOfSystems, setReviewOfSystems] = useState("");
  const [subjectiveNotes, setSubjectiveNotes] = useState("");
  const [objectiveNotes, setObjectiveNotes] = useState("");
  const [assessmentNotes, setAssessmentNotes] = useState("");
  const [plan, setPlan] = useState("");
  
  // New State for Diagnosis & Rx
  const [diagnosis, setDiagnosis] = useState("");
  const [diagnosisResults, setDiagnosisResults] = useState<any[]>([]);
  const [selectedDiagnoses, setSelectedDiagnoses] = useState<any[]>([]);

  const [rxDrug, setRxDrug] = useState("");
  const [rxDrugResults, setRxDrugResults] = useState<any[]>([]);
  const [rxDosage, setRxDosage] = useState("");
  const [rxFreq, setRxFreq] = useState("OD");
  const [rxDuration, setRxDuration] = useState("5");
  const [prescriptions, setPrescriptions] = useState<any[]>([]);
  
  const [errorAlerts, setErrorAlerts] = useState<string[]>([]);

  useEffect(() => {
    async function loadVisit() {
      try {
        const res = await api.get(`/visits/${id}`);
        setVisit(res.data);
      } catch (err) {
        console.error("Failed to load visit details", err);
      } finally {
        setLoading(false);
      }
    }
    if (id) {
      loadVisit();
    }
  }, [id]);

  const calculateAge = (dobString: string) => {
    if (!dobString) return "";
    const dob = new Date(dobString);
    const diffMs = Date.now() - dob.getTime();
    const ageDate = new Date(diffMs);
    return Math.abs(ageDate.getUTCFullYear() - 1970);
  };

  const searchIcd10 = async (q: string) => {
    setDiagnosis(q);
    if (q.length > 2) {
      const res = await api.get(`/icd/search?q=${q}`);
      setDiagnosisResults(res.data);
    } else {
      setDiagnosisResults([]);
    }
  };

  const suggestIcdWithAI = async () => {
    try {
      setLoading(true);
      const combinedText = `${subjectiveNotes} ${hpi} ${objectiveNotes} ${assessmentNotes}`;
      const res = await api.post("/ai/icd-suggest", { clinicalText: combinedText });
      
      if (res.data && res.data.suggestions) {
        setDiagnosisResults(res.data.suggestions.map((s: any) => ({
          id: s.code,
          code: s.code,
          description: s.description,
          confidence: s.confidence
        })));
      }
    } catch (e) {
      console.error(e);
      alert("Failed to get AI suggestions");
    } finally {
      setLoading(false);
    }
  };

  const handleSignAndLock = async () => {
    if (!window.confirm("Are you sure you want to sign and lock this chart? No further edits will be allowed.")) return;
    try {
      setLoading(true);
      await api.post(`/visits/${id}/notes/sign`, {
        subjectiveNotes,
        objectiveNotes,
        assessmentNotes,
        plan,
        diagnoses: selectedDiagnoses,
        prescriptions
      });
      alert("Chart officially signed and locked.");
      router.push("/doctor/dashboard");
    } catch (e) {
      console.error(e);
      alert("Failed to sign and lock chart.");
      setLoading(false);
    }
  };

  const handleSaveDraft = async () => {
    try {
      setLoading(true);
      await api.post(`/visits/${id}/notes`, {
        subjectiveNotes,
        objectiveNotes,
        assessmentNotes,
        plan,
        diagnoses: selectedDiagnoses,
        prescriptions
      });
      alert("Draft Saved Successfully");
    } catch (e) {
      console.error(e);
      alert("Failed to save draft");
    } finally {
      setLoading(false);
    }
  };

  const addDiagnosis = (diag: any) => {
    if (!selectedDiagnoses.find(d => d.code === diag.code || d.id === diag.id)) {
      setSelectedDiagnoses([...selectedDiagnoses, diag]);
    }
    setDiagnosis("");
    setDiagnosisResults([]);
  };

  const searchDrugs = async (q: string) => {
    setRxDrug(q);
    if (q.length > 2) {
      const res = await api.get(`/search/drugs?q=${q}`);
      setRxDrugResults(res.data);
    } else {
      setRxDrugResults([]);
    }
  };

  const selectDrug = (drug: any) => {
    setRxDrug(drug.genericName);
    setRxDrugResults([]);
  };

  const handleAddRx = () => {
    if (rxDrug && rxDosage) {
      setPrescriptions([...prescriptions, { 
        customDrugName: rxDrug, 
        dosage: rxDosage, 
        route: "Oral", 
        frequency: rxFreq, 
        durationDays: parseInt(rxDuration) 
      }]);
      setRxDrug("");
      setRxDosage("");
    }
  };

  const handleSave = async () => {
    try {
      setErrorAlerts([]);
      // Save Notes
      await api.post(`/visits/${id}/notes`, {
        historyOfPresentIllness: hpi,
        pastMedicalHistory: pastMedicalHistory,
        familyHistory: familyHistory,
        socialHistory: socialHistory,
        reviewOfSystems: reviewOfSystems,
        subjectiveNotes: subjectiveNotes,
        objectiveNotes: objectiveNotes,
        assessmentNotes: assessmentNotes,
        treatmentPlan: plan,
      });

      // Save Prescriptions
      if (prescriptions.length > 0) {
        await api.post(`/visits/${id}/prescriptions`, {
          lines: prescriptions,
          notes: plan
        });
      }

      alert("Clinical Encounter Finalized!");
    } catch (err: any) {
      console.error("Failed to save clinical note/prescription", err);
      if (err.response?.status === 400 && Array.isArray(err.response.data)) {
        setErrorAlerts(err.response.data);
      } else {
        alert("Failed to save clinical note.");
      }
    }
  };

  const downloadPrescription = async () => {
    try {
      const response = await api.get(`/visits/${id}/prescription-pdf`, { responseType: "blob" });
      const url = window.URL.createObjectURL(new Blob([response.data], { type: "application/pdf" }));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `prescription_${id}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.parentNode?.removeChild(link);
    } catch (error) {
      console.error("Failed to download prescription", error);
      alert("Error downloading prescription PDF. Did you save it first?");
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-text-secondary">Loading clinical workspace...</div>;
  }

  if (!visit || !visit.patient) {
    return <div className="p-8 text-center text-error">Visit/Patient details not found.</div>;
  }

  const patient = visit.patient;
  const age = calculateAge(patient.dateOfBirth);
  const genderLetter = patient.gender?.[0]?.toUpperCase() || "";
  const patientInfo = `${patient.firstName} ${patient.lastName} (${age}${genderLetter})`;

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      
      <AllergyBanner patientId={patient.id} />

      {/* Context Bar */}
      <div className="bg-primary-light text-primary-dark p-4 rounded-lg flex justify-between items-center shadow-sm">
        <div className="flex items-center gap-6">
          <div>
            <div className="text-xs opacity-80 uppercase tracking-wider">Patient</div>
            <div className="font-bold text-lg">{patientInfo}</div>
          </div>
          <div>
            <div className="text-xs opacity-80 uppercase tracking-wider">UHID</div>
            <div className="font-bold">{patient.uhid}</div>
          </div>
          <div>
            <div className="text-xs opacity-80 uppercase tracking-wider">Allergies</div>
            <Badge variant="error">{patient.bloodGroup ? `Blood Group: ${patient.bloodGroup}` : "Penicillin"}</Badge>
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
            <CardHeader><CardTitle>Subjective & History (S)</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div>
                <label className="text-xs font-medium text-text-secondary">History of Present Illness (HPI)</label>
                <textarea 
                  className="w-full h-24 p-3 border border-border rounded-md focus:border-primary outline-none resize-y"
                  placeholder="Patient presents with..."
                  value={hpi}
                  onChange={(e) => setHpi(e.target.value)}
                />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="text-xs font-medium text-text-secondary">Past Medical History</label>
                  <textarea className="w-full h-20 p-2 border border-border rounded-md focus:border-primary outline-none resize-y" value={pastMedicalHistory} onChange={e => setPastMedicalHistory(e.target.value)} />
                </div>
                <div>
                  <label className="text-xs font-medium text-text-secondary">Family History</label>
                  <textarea className="w-full h-20 p-2 border border-border rounded-md focus:border-primary outline-none resize-y" value={familyHistory} onChange={e => setFamilyHistory(e.target.value)} />
                </div>
                <div>
                  <label className="text-xs font-medium text-text-secondary">Social History</label>
                  <textarea className="w-full h-20 p-2 border border-border rounded-md focus:border-primary outline-none resize-y" value={socialHistory} onChange={e => setSocialHistory(e.target.value)} />
                </div>
                <div>
                  <label className="text-xs font-medium text-text-secondary">Review of Systems</label>
                  <textarea className="w-full h-20 p-2 border border-border rounded-md focus:border-primary outline-none resize-y" value={reviewOfSystems} onChange={e => setReviewOfSystems(e.target.value)} />
                </div>
              </div>
              <div>
                <label className="text-xs font-medium text-text-secondary">Additional Subjective Notes</label>
                <textarea className="w-full h-16 p-2 border border-border rounded-md focus:border-primary outline-none resize-y" value={subjectiveNotes} onChange={e => setSubjectiveNotes(e.target.value)} />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Objective (O)</CardTitle></CardHeader>
            <CardContent>
              <textarea 
                className="w-full h-32 p-3 border border-border rounded-md focus:border-primary outline-none resize-y"
                placeholder="Physical Examination, Vitals findings..."
                value={objectiveNotes}
                onChange={(e) => setObjectiveNotes(e.target.value)}
              />
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Assessment (A)</CardTitle></CardHeader>
            <CardContent>
              <textarea 
                className="w-full h-24 p-3 border border-border rounded-md focus:border-primary outline-none resize-y"
                placeholder="Diagnostic reasoning..."
                value={assessmentNotes}
                onChange={(e) => setAssessmentNotes(e.target.value)}
              />
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Diagnoses</CardTitle></CardHeader>
            <CardContent>
              <div className="relative mb-4">
                <div className="flex gap-2 mb-2 items-center">
                  <Input 
                    placeholder="Search ICD-10 Code or Description..." 
                    value={diagnosis} 
                    onChange={(e) => searchIcd10(e.target.value)}
                  />
                  <Button variant="secondary" onClick={suggestIcdWithAI} disabled={loading}>
                    ✨ AI Suggest
                  </Button>
                </div>
                
                {diagnosisResults.length > 0 && (
                  <div className="absolute z-10 w-full bg-surface border border-border mt-1 rounded-md shadow-lg max-h-48 overflow-y-auto">
                    {diagnosisResults.map((diag, i) => (
                      <div key={i} className="p-2 hover:bg-background-secondary cursor-pointer border-b text-sm flex justify-between" onClick={() => addDiagnosis(diag)}>
                        <span><span className="font-bold text-primary">{diag.code}</span> - {diag.description}</span>
                        {diag.confidence && <Badge variant="info" className="text-xs">{(diag.confidence * 100).toFixed(0)}% Match</Badge>}
                      </div>
                    ))}
                  </div>
                )}
              </div>
              <div className="flex gap-2 flex-wrap">
                {selectedDiagnoses.map(d => (
                  <Badge key={d.id} variant="info">{d.code} : {d.description}</Badge>
                ))}
                {selectedDiagnoses.length === 0 && <span className="text-sm text-text-tertiary">No diagnoses added.</span>}
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Prescription Writer</CardTitle></CardHeader>
            <CardContent>
              {errorAlerts.length > 0 && (
                <div className="mb-4 p-3 bg-error/10 border border-error/20 text-error rounded-md">
                  <h4 className="font-semibold mb-1">Clinical Safety Alerts:</h4>
                  <ul className="list-disc pl-5 text-sm space-y-1">
                    {errorAlerts.map((msg, i) => <li key={i}>{msg}</li>)}
                  </ul>
                </div>
              )}
              
              <div className="grid grid-cols-1 md:grid-cols-5 gap-4 mb-4">
                <div className="md:col-span-2 relative">
                  <label className="text-xs text-text-secondary">Drug Name</label>
                  <input type="text" className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" placeholder="Search Drugs..." value={rxDrug} onChange={(e) => searchDrugs(e.target.value)} />
                  {rxDrugResults.length > 0 && (
                    <div className="absolute z-10 w-full mt-1 bg-white border border-border rounded-md shadow-lg max-h-48 overflow-y-auto">
                      {rxDrugResults.map((d: any) => (
                        <div key={d.id} className="p-2 hover:bg-surface cursor-pointer text-sm" onClick={() => selectDrug(d)}>
                          <span className="font-semibold">{d.genericName}</span> ({d.brandName}) - {d.strength}
                        </div>
                      ))}
                    </div>
                  )}
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
                          <td className="p-3 font-medium">{rx.customDrugName}</td>
                          <td className="p-3">{rx.dosage}</td>
                          <td className="p-3">{rx.frequency}</td>
                          <td className="p-3">{rx.durationDays} Days</td>
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

          <div className="flex justify-end gap-4 mt-8 pb-10">
          <Button variant="outline" onClick={() => router.back()}>Cancel</Button>
          <Button variant="secondary" onClick={handleSaveDraft} disabled={loading}>Save Draft</Button>
          <Button variant="primary" onClick={handleSignAndLock} disabled={loading}>
            Sign & Lock Chart
          </Button>
        </div>
      </div>
      </div>
    </div>
  );
}
