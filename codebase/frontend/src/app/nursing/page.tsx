"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";

export default function NursingDashboard() {
  const [activeTab, setActiveTab] = useState("ward");
  const [selectedPatient, setSelectedPatient] = useState<any>(null);
  const [occupiedBeds, setOccupiedBeds] = useState<any[]>([]);

  // Vitals State
  const [vitalsHistory, setVitalsHistory] = useState<any[]>([]);
  const [newVitals, setNewVitals] = useState({
    temperature: "",
    heartRate: "",
    respiratoryRate: "",
    bloodPressureSystolic: "",
    bloodPressureDiastolic: "",
    oxygenSaturation: ""
  });

  // MAR State
  const [marSchedule, setMarSchedule] = useState<any[]>([]);

  useEffect(() => {
    fetchOccupiedBeds();
  }, []);

  const fetchOccupiedBeds = async () => {
    try {
      const res = await api.get("/beds/dashboard");
      // Dashboard returns wards which contain beds
      const wards = res.data;
      const patients: any[] = [];
      wards.forEach((ward: any) => {
        ward.beds.forEach((bed: any) => {
          if (bed.status === "OCCUPIED" && bed.currentAdmission) {
            patients.push({
              patientId: bed.currentAdmission.patient.id,
              name: `${bed.currentAdmission.patient.firstName} ${bed.currentAdmission.patient.lastName}`,
              bed: bed.bedNumber,
              ward: ward.name,
              doctor: bed.currentAdmission.admittingDoctor?.user?.firstName + " " + bed.currentAdmission.admittingDoctor?.user?.lastName,
              admissionId: bed.currentAdmission.id
            });
          }
        });
      });
      setOccupiedBeds(patients);
    } catch (e) {
      console.error(e);
      alert("Failed to load occupied beds.");
    }
  };

  const fetchVitals = async (patientId: string) => {
    try {
      const res = await api.get(`/nursing/vitals/patient/${patientId}`);
      setVitalsHistory(res.data);
    } catch (e) {
      console.error(e);
      setVitalsHistory([]);
    }
  };

  const fetchMar = async (patientId: string) => {
    try {
      const res = await api.get(`/nursing/mar/patient/${patientId}`);
      setMarSchedule(res.data);
    } catch (e) {
      console.error(e);
      alert("Failed to load MAR.");
    }
  };

  const handleSelectPatient = (patient: any, tab: string) => {
    setSelectedPatient(patient);
    setActiveTab(tab);
    if (tab === "vitals") {
      fetchVitals(patient.patientId);
    } else if (tab === "mar") {
      fetchMar(patient.patientId);
    }
  };

  const handleSaveVitals = async () => {
    if (!selectedPatient) return;
    try {
      const payload = {
        patient: { id: selectedPatient.patientId },
        recordedBy: { id: "1" }, // Current nurse mock
        temperature: parseFloat(newVitals.temperature),
        heartRate: parseInt(newVitals.heartRate),
        respiratoryRate: parseInt(newVitals.respiratoryRate),
        bloodPressureSystolic: parseInt(newVitals.bloodPressureSystolic),
        bloodPressureDiastolic: parseInt(newVitals.bloodPressureDiastolic),
        oxygenSaturation: parseInt(newVitals.oxygenSaturation),
      };
      await api.post("/nursing/vitals", payload);
      alert("Vitals saved!");
      setNewVitals({ temperature: "", heartRate: "", respiratoryRate: "", bloodPressureSystolic: "", bloodPressureDiastolic: "", oxygenSaturation: "" });
      fetchVitals(selectedPatient.patientId);
    } catch (e) {
      alert("Failed to save vitals");
    }
  };

  const handleMarkGiven = async (marItem: any) => {
    try {
      await api.post("/nursing/mar", {
        patient: { id: selectedPatient.patientId },
        administeredBy: { id: "1" },
        drugName: marItem.drugName,
        dose: marItem.doseRoute,
        notes: "Administered as per schedule"
      });
      alert("Marked as Given!");
      fetchMar(selectedPatient.patientId);
    } catch (e) {
      alert("Failed to record MAR");
    }
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Nursing Station</h1>
          <p className="text-text-secondary text-sm">Ward management, vitals monitoring, and MAR</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-border">
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'ward' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => { setActiveTab('ward'); setSelectedPatient(null); }}
        >
          Ward List
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'vitals' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => { if(selectedPatient) handleSelectPatient(selectedPatient, 'vitals'); else setActiveTab('vitals'); }}
        >
          Vitals Entry
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'mar' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => { if(selectedPatient) handleSelectPatient(selectedPatient, 'mar'); else setActiveTab('mar'); }}
        >
          Medication Admin Record (MAR)
        </button>
      </div>

      {/* Content */}
      {activeTab === "ward" && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {occupiedBeds.length === 0 ? (
            <div className="col-span-3 py-12 text-center text-text-secondary">No patients are currently admitted in your wards.</div>
          ) : (
            occupiedBeds.map((patient: any, idx: number) => (
              <Card key={idx}>
                <CardContent className="p-4">
                  <div className="flex justify-between items-start mb-4">
                    <div>
                      <div className="font-bold text-lg">{patient.name}</div>
                      <div className="text-sm text-text-secondary">Bed: {patient.bed} | {patient.doctor}</div>
                    </div>
                    <Badge variant="info">Admitted</Badge>
                  </div>
                  <div className="flex gap-2 mt-6">
                    <Button variant="secondary" size="sm" className="flex-1" onClick={() => handleSelectPatient(patient, 'vitals')}>Vitals</Button>
                    <Button variant="primary" size="sm" className="flex-1" onClick={() => handleSelectPatient(patient, 'mar')}>MAR</Button>
                  </div>
                </CardContent>
              </Card>
            ))
          )}
        </div>
      )}

      {activeTab === "vitals" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Vitals Entry</CardTitle>
              {selectedPatient && <Badge variant="info">Patient: {selectedPatient.name} ({selectedPatient.bed})</Badge>}
            </div>
          </CardHeader>
          <CardContent>
            {selectedPatient ? (
              <div className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                  <div>
                    <label className="block text-sm font-medium mb-1">Temperature (°F)</label>
                    <Input type="number" value={newVitals.temperature} onChange={e => setNewVitals({...newVitals, temperature: e.target.value})} />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">Heart Rate (bpm)</label>
                    <Input type="number" value={newVitals.heartRate} onChange={e => setNewVitals({...newVitals, heartRate: e.target.value})} />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">SpO2 (%)</label>
                    <Input type="number" value={newVitals.oxygenSaturation} onChange={e => setNewVitals({...newVitals, oxygenSaturation: e.target.value})} />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">BP Systolic (mmHg)</label>
                    <Input type="number" value={newVitals.bloodPressureSystolic} onChange={e => setNewVitals({...newVitals, bloodPressureSystolic: e.target.value})} />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">BP Diastolic (mmHg)</label>
                    <Input type="number" value={newVitals.bloodPressureDiastolic} onChange={e => setNewVitals({...newVitals, bloodPressureDiastolic: e.target.value})} />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">Respiratory Rate</label>
                    <Input type="number" value={newVitals.respiratoryRate} onChange={e => setNewVitals({...newVitals, respiratoryRate: e.target.value})} />
                  </div>
                </div>
                <div className="flex justify-end gap-4">
                  <Button variant="secondary" onClick={() => setActiveTab('ward')}>Cancel</Button>
                  <Button variant="primary" onClick={handleSaveVitals}>Save Vitals</Button>
                </div>

                <div className="mt-8 border-t border-border pt-6">
                  <h3 className="font-bold mb-4">Recent Vitals Trend</h3>
                  <table className="w-full text-left text-sm border border-border rounded-md overflow-hidden">
                    <thead className="bg-surface border-b border-border">
                      <tr>
                        <th className="p-3">Time</th>
                        <th className="p-3">Temp</th>
                        <th className="p-3">HR</th>
                        <th className="p-3">BP</th>
                        <th className="p-3">SpO2</th>
                      </tr>
                    </thead>
                    <tbody>
                      {vitalsHistory.length === 0 ? (
                        <tr><td colSpan={5} className="p-3 text-center text-text-secondary">No recent vitals found.</td></tr>
                      ) : (
                        vitalsHistory.map((v, i) => (
                          <tr key={i} className="border-b border-surface-hover">
                            <td className="p-3 text-text-secondary">{new Date(v.createdAt).toLocaleString()}</td>
                            <td className="p-3">{v.temperature ? v.temperature + " °F" : "-"}</td>
                            <td className="p-3">{v.heartRate ? v.heartRate + " bpm" : "-"}</td>
                            <td className="p-3">{v.bloodPressureSystolic ? `${v.bloodPressureSystolic}/${v.bloodPressureDiastolic}` : "-"}</td>
                            <td className="p-3">{v.oxygenSaturation ? v.oxygenSaturation + "%" : "-"}</td>
                          </tr>
                        ))
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            ) : (
              <div className="py-12 text-center text-text-secondary">Please select a patient from the Ward List first.</div>
            )}
          </CardContent>
        </Card>
      )}

      {activeTab === "mar" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Medication Administration Record (MAR)</CardTitle>
              {selectedPatient && <Badge variant="info">Patient: {selectedPatient.name} ({selectedPatient.bed})</Badge>}
            </div>
          </CardHeader>
          <CardContent>
            {selectedPatient ? (
              <div className="space-y-4">
                <div className="border border-border rounded-md overflow-hidden">
                  <table className="w-full text-left text-sm">
                    <thead className="bg-surface border-b border-border">
                      <tr>
                        <th className="p-3">Drug</th>
                        <th className="p-3">Dose & Route</th>
                        <th className="p-3">Frequency</th>
                        <th className="p-3">Status</th>
                        <th className="p-3">Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {marSchedule.map((item, idx) => (
                        <tr key={idx} className={`border-b border-surface-hover ${item.status === 'GIVEN' ? 'bg-success/5' : ''}`}>
                          <td className="p-3 font-medium">{item.drugName}</td>
                          <td className="p-3">{item.dose || item.doseRoute}</td>
                          <td className="p-3">{item.frequency || "OD"}</td>
                          <td className="p-3">
                            {item.status === "GIVEN" ? (
                              <span className="text-success font-bold">Given at {new Date(item.createdAt || Date.now()).toLocaleTimeString()}</span>
                            ) : (
                              <span className="text-error font-bold">Pending</span>
                            )}
                          </td>
                          <td className="p-3">
                             <Button 
                               variant={item.status === "GIVEN" ? "secondary" : "primary"} 
                               size="sm" 
                               onClick={() => handleMarkGiven(item)}
                               disabled={item.status === "GIVEN"}
                             >
                               {item.status === "GIVEN" ? "Completed" : "Mark Given"}
                             </Button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            ) : (
              <div className="py-12 text-center text-text-secondary">Please select a patient from the Ward List first.</div>
            )}
          </CardContent>
        </Card>
      )}

    </div>
  );
}
