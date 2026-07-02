/* eslint-disable @next/next/no-img-element */
"use client";

import { useState, useEffect, useCallback } from "react";
import { useParams, useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";
import { AllergyBanner } from "@/components/AllergyBanner";
import { api } from "@/lib/api";

export default function PatientProfile() {
  const { id } = useParams();
  const router = useRouter();
  const [patient, setPatient] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [newAllergen, setNewAllergen] = useState("");
  const [newSeverity, setNewSeverity] = useState("Mild");
  const [newReaction, setNewReaction] = useState("");
  const [isAddingAllergy, setIsAddingAllergy] = useState(false);
  
  const [activeTab, setActiveTab] = useState("DEMOGRAPHICS");
  const [diagnoses, setDiagnoses] = useState<any[]>([]);
  const [clinicalNotes, setClinicalNotes] = useState<any[]>([]);

  const fetchPatientDetails = useCallback(async () => {
    try {
      const response = await api.get(`/patients/${id}`);
      setPatient(response.data);
      
      try {
        const diagRes = await api.get(`/patients/${id}/diagnoses`);
        setDiagnoses(diagRes.data);
        
        const notesRes = await api.get(`/clinical-notes/patient/${id}`);
        setClinicalNotes(notesRes.data);
      } catch (e) {
        console.error("Failed to fetch clinical data", e);
      }
      
    } catch (error) {
      console.error("Failed to fetch patient", error);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    if (id) {
      fetchPatientDetails();
    }
  }, [id, fetchPatientDetails]);

  const handlePrintWristband = async () => {
    try {
      const response = await api.get(`/patients/${id}/qr-pdf`, { responseType: "blob" });
      const url = window.URL.createObjectURL(new Blob([response.data], { type: "application/pdf" }));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `wristband_${id}.pdf`);
      document.body.appendChild(link);
      link.click();
    } catch (error) {
      console.error("Failed to download PDF", error);
    }
  };

  const startVisit = async () => {
    try {
      const docRes = await api.get("/staff?role=DOCTOR");
      const doctorId = docRes.data.length > 0 ? docRes.data[0].id : null;
      if (!doctorId) {
        alert("No doctor found to start visit");
        return;
      }
      const response = await api.post("/visits", {
        patientId: id,
        doctorId: doctorId,
        tokenNumber: 0,
      });
      router.push(`/doctor/visit/${response.data.id}`);
    } catch (error) {
      console.error("Failed to start visit", error);
      alert("Failed to start visit.");
    }
  };

  const handleAddAllergy = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newAllergen) return;
    
    try {
      await api.post(`/allergies/patient/${id}`, {
        allergen: newAllergen,
        severity: newSeverity,
        reaction: newReaction
      });
      setIsAddingAllergy(false);
      setNewAllergen("");
      setNewReaction("");
      // Refresh patient to trigger re-renders if necessary
      fetchPatientDetails();
    } catch (error) {
      console.error("Failed to add allergy", error);
      alert("Error adding allergy.");
    }
  };

  if (loading) return <div className="p-8 text-text-secondary">Loading patient profile...</div>;
  if (!patient) return <div className="p-8 text-error">Patient not found.</div>;

  return (
    <div className="max-w-6xl space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button variant="secondary" onClick={() => router.push("/patients")}>← Back</Button>
          <h2 className="text-2xl font-semibold text-text-primary">Patient Profile</h2>
        </div>
        <div className="flex gap-2">
          <Button variant="secondary" onClick={() => router.push(`/admissions/new?patientId=${id}`)}>Admit Patient</Button>
          <Button variant="primary" onClick={startVisit}>Start OPD Visit</Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Core Identity & QR */}
        <div className="lg:col-span-1 space-y-6">
          <Card className="border-primary/20 shadow-md">
          <CardContent className="p-6 flex flex-col items-center text-center">
            <div className="w-24 h-24 bg-primary-light text-primary rounded-full flex items-center justify-center text-3xl font-bold mb-4 overflow-hidden">
              {patient.photoBase64 ? (
                <img src={patient.photoBase64} alt="Patient" className="w-full h-full object-cover" />
              ) : (
                (patient.firstName?.[0] || "") + (patient.lastName?.[0] || "")
              )}
            </div>
            <h3 className="text-xl font-bold text-text-primary">{(patient.firstName || "") + (patient.middleName ? " " + patient.middleName : "") + " " + (patient.lastName || "")}</h3>
            <div className="text-sm text-text-secondary mb-2">{patient.gender} • {patient.dateOfBirth}</div>
            
            <div className="bg-surface px-4 py-2 rounded-md border border-border w-full flex justify-between items-center mb-6 mt-4">
              <span className="text-text-secondary text-sm font-medium">UHID</span>
              <span className="text-primary font-bold">{patient.uhid}</span>
            </div>

            {/* QR Code Display */}
            {patient.qrCodeBase64 && (
              <div className="border border-border p-2 rounded-lg bg-white inline-block">
                <img src={patient.qrCodeBase64} alt="Patient QR Code" className="w-40 h-40" />
                <p className="text-xs text-text-secondary mt-1">Scan for records</p>
              </div>
            )}
            
            <Button variant="secondary" className="w-full mt-6" onClick={handlePrintWristband}>
              Print Wristband
            </Button>
            <Button variant="secondary" className="w-full mt-2" onClick={() => alert("Registration Slip PDF feature in progress")}>
              Print Registration Slip
            </Button>
          </CardContent>
        </Card>
        
        <Card>
           <CardHeader><CardTitle>Allergies</CardTitle></CardHeader>
           <CardContent>
             <AllergyBanner patientId={id as string} />
             
             {isAddingAllergy ? (
               <form onSubmit={handleAddAllergy} className="mt-4 space-y-3 bg-surface p-3 rounded-md border border-border">
                 <Input placeholder="Allergen (e.g. Penicillin)" value={newAllergen} onChange={(e) => setNewAllergen(e.target.value)} required />
                 <div className="flex gap-2">
                   <select className="flex-1 p-2 border border-border rounded-md text-sm" value={newSeverity} onChange={(e) => setNewSeverity(e.target.value)}>
                     <option>Mild</option>
                     <option>Moderate</option>
                     <option>Severe</option>
                   </select>
                   <Input placeholder="Reaction" value={newReaction} onChange={(e) => setNewReaction(e.target.value)} className="flex-2" />
                 </div>
                 <div className="flex justify-end gap-2">
                   <Button variant="secondary" size="sm" type="button" onClick={() => setIsAddingAllergy(false)}>Cancel</Button>
                   <Button variant="primary" size="sm" type="submit">Save</Button>
                 </div>
               </form>
             ) : (
               <Button variant="secondary" className="w-full mt-2" onClick={() => setIsAddingAllergy(true)}>
                 + Add Allergy
               </Button>
             )}
           </CardContent>
        </Card>
      </div>

        {/* Details Sections */}
        <div className="lg:col-span-2 space-y-6">
          <div className="flex border-b border-border bg-surface rounded-t-lg px-2 pt-2">
            <button className={`px-4 py-3 text-sm font-medium border-b-2 ${activeTab === 'DEMOGRAPHICS' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`} onClick={() => setActiveTab('DEMOGRAPHICS')}>
              Demographics
            </button>
            <button className={`px-4 py-3 text-sm font-medium border-b-2 ${activeTab === 'DIAGNOSES' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`} onClick={() => setActiveTab('DIAGNOSES')}>
              Diagnoses & Conditions
            </button>
            <button className={`px-4 py-3 text-sm font-medium border-b-2 ${activeTab === 'NOTES' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`} onClick={() => setActiveTab('NOTES')}>
              Clinical Notes
            </button>
          </div>

          {activeTab === 'DEMOGRAPHICS' && (
            <>
              <Card>
                <CardHeader><CardTitle>Demographics & Contact</CardTitle></CardHeader>
                <CardContent>
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-y-6 gap-x-4">
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Mobile Number</div>
                      <div className="font-medium text-text-primary">{patient.mobileNumber}</div>
                    </div>
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Email</div>
                      <div className="font-medium text-text-primary">{patient.email || "—"}</div>
                    </div>
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Blood Group</div>
                      <div className="font-medium text-text-primary">
                        {patient.bloodGroup ? <Badge variant="error">{patient.bloodGroup}</Badge> : "—"}
                      </div>
                    </div>
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Marital Status</div>
                      <div className="font-medium text-text-primary">{patient.maritalStatus || "—"}</div>
                    </div>
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Nationality</div>
                      <div className="font-medium text-text-primary">{patient.nationality || "—"}</div>
                    </div>
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Language</div>
                      <div className="font-medium text-text-primary">{patient.primaryLanguage || "—"}</div>
                    </div>
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Aadhaar (National ID)</div>
                      <div className="font-medium text-text-primary">{patient.nationalId || "—"}</div>
                    </div>
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Passport Number</div>
                      <div className="font-medium text-text-primary">{patient.passportNumber || "—"}</div>
                    </div>
                    <div>
                      <div className="text-xs text-text-secondary mb-1">ABHA ID</div>
                      <div className="font-medium text-text-primary">{patient.abhaId || "—"}</div>
                    </div>
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Religion</div>
                      <div className="font-medium text-text-primary">{patient.religion || "—"}</div>
                    </div>
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Occupation</div>
                      <div className="font-medium text-text-primary">{patient.occupation || "—"}</div>
                    </div>
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Referred By</div>
                      <div className="font-medium text-text-primary">{patient.referredBy || "—"}</div>
                    </div>
                    <div className="col-span-2 md:col-span-3">
                      <div className="text-xs text-text-secondary mb-1">Address</div>
                      <div className="font-medium text-text-primary">
                        {patient.addressLine1}
                        {patient.addressLine2 ? `, ${patient.addressLine2}` : ""}
                        {`, ${patient.city}, ${patient.state}`}
                        {patient.country ? `, ${patient.country}` : ""}
                        {patient.zipCode ? ` - ${patient.zipCode}` : ""}
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="flex justify-between items-center flex-row">
                  <CardTitle>Emergency Contact</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Name</div>
                      <div className="font-medium text-text-primary">{patient.emergencyContactName || "—"}</div>
                    </div>
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Relationship</div>
                      <div className="font-medium text-text-primary">{patient.emergencyContactRelation || "—"}</div>
                    </div>
                    <div>
                      <div className="text-xs text-text-secondary mb-1">Phone</div>
                      <div className="font-medium text-text-primary">{patient.emergencyContactPhone || "—"}</div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </>
          )}

          {activeTab === 'DIAGNOSES' && (
            <Card>
              <CardHeader><CardTitle>Problem List & Diagnoses</CardTitle></CardHeader>
              <CardContent>
                {diagnoses.length === 0 ? (
                  <div className="p-8 text-center text-text-secondary bg-surface rounded-md border border-dashed border-border">
                    No diagnoses recorded for this patient.
                  </div>
                ) : (
                  <div className="space-y-4">
                    {diagnoses.map((d: any) => (
                      <div key={d.id} className="p-4 border border-border rounded-md hover:bg-surface transition-colors flex justify-between items-start">
                        <div>
                          <div className="font-semibold text-lg text-text-primary flex items-center gap-2">
                            {d.diagnosisName} <Badge variant="secondary">{d.icd10Code}</Badge>
                          </div>
                          <div className="text-sm text-text-secondary mt-1">
                            Diagnosed on {new Date(d.diagnosedDate).toLocaleDateString()}
                          </div>
                        </div>
                        <div className="text-right">
                          <Badge variant={d.status === "Active" ? "error" : "default"}>{d.status}</Badge>
                          <div className="text-xs text-text-secondary mt-1">{d.type}</div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          )}

          {activeTab === 'NOTES' && (
            <Card>
              <CardHeader><CardTitle>Clinical Notes History</CardTitle></CardHeader>
              <CardContent>
                {clinicalNotes.length === 0 ? (
                  <div className="p-8 text-center text-text-secondary bg-surface rounded-md border border-dashed border-border">
                    No clinical notes recorded for this patient.
                  </div>
                ) : (
                  <div className="space-y-6">
                    {clinicalNotes.map((note: any) => (
                      <div key={note.id} className="p-5 border border-border rounded-md bg-surface space-y-4">
                        <div className="flex justify-between items-center border-b border-border pb-3">
                          <div>
                            <div className="font-semibold">Visit Note</div>
                            <div className="text-xs text-text-secondary">{new Date(note.createdAt).toLocaleString()}</div>
                          </div>
                          {note.finalized ? <Badge variant="success">Signed</Badge> : <Badge variant="warning">Draft</Badge>}
                        </div>
                        
                        {note.historyOfPresentIllness && (
                          <div>
                            <div className="text-xs font-semibold uppercase text-text-secondary mb-1">History of Present Illness</div>
                            <div className="text-sm text-text-primary whitespace-pre-wrap">{note.historyOfPresentIllness}</div>
                          </div>
                        )}
                        
                        {note.physicalExamination && (
                          <div>
                            <div className="text-xs font-semibold uppercase text-text-secondary mb-1">Physical Examination</div>
                            <div className="text-sm text-text-primary whitespace-pre-wrap">{note.physicalExamination}</div>
                          </div>
                        )}
                        
                        {note.treatmentPlan && (
                          <div>
                            <div className="text-xs font-semibold uppercase text-text-secondary mb-1">Treatment Plan</div>
                            <div className="text-sm text-text-primary whitespace-pre-wrap">{note.treatmentPlan}</div>
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
}
