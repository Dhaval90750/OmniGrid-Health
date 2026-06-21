"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

export default function IcuDashboard() {
  const [activeTab, setActiveTab] = useState("grid");
  const [selectedPatient, setSelectedPatient] = useState<any>(null);

  // GCS State
  const [eye, setEye] = useState(4);
  const [verbal, setVerbal] = useState(5);
  const [motor, setMotor] = useState(6);

  const handleSelectPatient = (patient: any, tab: string) => {
    setSelectedPatient(patient);
    setActiveTab(tab);
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">ICU Central Station</h1>
          <p className="text-text-secondary text-sm">Intensive care monitoring, hourly charting, and clinical scoring</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-border">
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'grid' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => { setActiveTab('grid'); setSelectedPatient(null); }}
        >
          ICU Bed Grid
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'flowsheet' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('flowsheet')}
        >
          Hourly Flow-Sheet
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'scoring' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('scoring')}
        >
          Clinical Scoring (GCS)
        </button>
      </div>

      {/* Content */}
      {activeTab === "grid" && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {/* ICU Bed 1 */}
          <Card className="border-error border-2 relative overflow-hidden">
            <div className="absolute top-0 right-0 bg-error text-white text-xs font-bold px-2 py-1 rounded-bl">CRITICAL</div>
            <CardContent className="p-4">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <div className="font-bold text-lg">Amit Kumar (55M)</div>
                  <div className="text-sm text-text-secondary">Bed: ICU-01 | Dr. R. Iyer</div>
                </div>
              </div>
              <div className="grid grid-cols-2 gap-2 text-sm mb-4">
                <div className="p-2 bg-error/10 rounded border border-error/30">
                  <div className="text-text-secondary text-xs mb-1">MAP</div>
                  <div className="font-bold text-error text-lg">55 mmHg</div>
                </div>
                <div className="p-2 bg-surface rounded border border-border">
                  <div className="text-text-secondary text-xs mb-1">Ventilator</div>
                  <div className="font-medium">SIMV | FiO2 60%</div>
                </div>
                <div className="p-2 bg-surface rounded border border-border">
                  <div className="text-text-secondary text-xs mb-1">SpO2</div>
                  <div className="font-medium text-error">88%</div>
                </div>
                <div className="p-2 bg-surface rounded border border-border">
                  <div className="text-text-secondary text-xs mb-1">Latest GCS</div>
                  <div className="font-bold">E2 V2 M4 (8)</div>
                </div>
              </div>
              <div className="flex gap-2">
                <Button variant="secondary" size="sm" className="flex-1" onClick={() => handleSelectPatient({ name: 'Amit Kumar', bed: 'ICU-01' }, 'flowsheet')}>Flow-Sheet</Button>
                <Button variant="primary" size="sm" className="flex-1" onClick={() => handleSelectPatient({ name: 'Amit Kumar', bed: 'ICU-01' }, 'scoring')}>GCS Score</Button>
              </div>
            </CardContent>
          </Card>

          {/* ICU Bed 2 */}
          <Card>
            <CardContent className="p-4">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <div className="font-bold text-lg">Priya Sharma (34F)</div>
                  <div className="text-sm text-text-secondary">Bed: ICU-02 | Dr. S. Gupta</div>
                </div>
              </div>
              <div className="grid grid-cols-2 gap-2 text-sm mb-4">
                <div className="p-2 bg-surface rounded border border-border">
                  <div className="text-text-secondary text-xs mb-1">MAP</div>
                  <div className="font-medium text-success">75 mmHg</div>
                </div>
                <div className="p-2 bg-surface rounded border border-border">
                  <div className="text-text-secondary text-xs mb-1">Ventilator</div>
                  <div className="font-medium">Room Air (Weaned)</div>
                </div>
                <div className="p-2 bg-surface rounded border border-border">
                  <div className="text-text-secondary text-xs mb-1">SpO2</div>
                  <div className="font-medium">98%</div>
                </div>
                <div className="p-2 bg-surface rounded border border-border">
                  <div className="text-text-secondary text-xs mb-1">Latest GCS</div>
                  <div className="font-bold text-success">E4 V5 M6 (15)</div>
                </div>
              </div>
              <div className="flex gap-2">
                <Button variant="secondary" size="sm" className="flex-1" onClick={() => handleSelectPatient({ name: 'Priya Sharma', bed: 'ICU-02' }, 'flowsheet')}>Flow-Sheet</Button>
                <Button variant="primary" size="sm" className="flex-1" onClick={() => handleSelectPatient({ name: 'Priya Sharma', bed: 'ICU-02' }, 'scoring')}>GCS Score</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {activeTab === "flowsheet" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Hourly Intensive Flow-Sheet</CardTitle>
              {selectedPatient && <Badge variant="info">Patient: {selectedPatient.name} ({selectedPatient.bed})</Badge>}
            </div>
          </CardHeader>
          <CardContent>
            {selectedPatient ? (
              <div className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-6 bg-surface p-4 rounded-md border border-border">
                  <div className="col-span-full font-bold border-b border-border pb-2">Hemodynamics & Vitals</div>
                  <div>
                    <label className="block text-xs text-text-secondary mb-1">Heart Rate</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary" />
                  </div>
                  <div>
                    <label className="block text-xs text-text-secondary mb-1">BP Systolic</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary" />
                  </div>
                  <div>
                    <label className="block text-xs text-text-secondary mb-1">BP Diastolic</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary" />
                  </div>
                  <div>
                    <label className="block text-xs text-text-secondary mb-1">MAP (Auto)</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md text-sm outline-none bg-surface-hover" disabled placeholder="e.g. 65" />
                  </div>

                  <div className="col-span-full font-bold border-b border-border pb-2 mt-4">Respiratory / Ventilator</div>
                  <div>
                    <label className="block text-xs text-text-secondary mb-1">Mode</label>
                    <select className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary">
                      <option>Room Air</option>
                      <option>AC/VC</option>
                      <option>SIMV</option>
                      <option>CPAP/PSV</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-xs text-text-secondary mb-1">FiO2 (%)</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary" />
                  </div>
                  <div>
                    <label className="block text-xs text-text-secondary mb-1">PEEP (cmH2O)</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary" />
                  </div>
                  <div>
                    <label className="block text-xs text-text-secondary mb-1">SpO2 (%)</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary" />
                  </div>

                  <div className="col-span-full font-bold border-b border-border pb-2 mt-4">Fluid Balance (Hourly)</div>
                  <div>
                    <label className="block text-xs text-text-secondary mb-1">Total Intake (ml)</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary" placeholder="IV fluids, feeds..." />
                  </div>
                  <div>
                    <label className="block text-xs text-text-secondary mb-1">Total Output (ml)</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary" placeholder="Urine, drains..." />
                  </div>
                </div>
                
                <div className="flex justify-end gap-4">
                  <Button variant="secondary" onClick={() => setActiveTab('grid')}>Cancel</Button>
                  <Button variant="primary" onClick={() => alert("Hourly Chart Saved!")}>Save Chart (10:00 AM)</Button>
                </div>
              </div>
            ) : (
              <div className="py-12 text-center text-text-secondary">Please select a patient from the Bed Grid.</div>
            )}
          </CardContent>
        </Card>
      )}

      {activeTab === "scoring" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Glasgow Coma Scale (GCS)</CardTitle>
              {selectedPatient && <Badge variant="info">Patient: {selectedPatient.name} ({selectedPatient.bed})</Badge>}
            </div>
          </CardHeader>
          <CardContent>
            {selectedPatient ? (
              <div className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                  {/* Eye Response */}
                  <div className="space-y-2">
                    <h3 className="font-bold border-b border-border pb-2">Eye Response (E)</h3>
                    {[
                      { val: 4, label: "Spontaneous" },
                      { val: 3, label: "To sound" },
                      { val: 2, label: "To pressure" },
                      { val: 1, label: "None" }
                    ].map(opt => (
                      <div 
                        key={opt.val}
                        className={`p-3 border rounded-md cursor-pointer text-sm transition-colors ${eye === opt.val ? 'bg-primary-light border-primary font-medium text-primary-dark' : 'border-border hover:bg-surface-hover'}`}
                        onClick={() => setEye(opt.val)}
                      >
                        {opt.val} - {opt.label}
                      </div>
                    ))}
                  </div>

                  {/* Verbal Response */}
                  <div className="space-y-2">
                    <h3 className="font-bold border-b border-border pb-2">Verbal Response (V)</h3>
                    {[
                      { val: 5, label: "Oriented" },
                      { val: 4, label: "Confused" },
                      { val: 3, label: "Words" },
                      { val: 2, label: "Sounds" },
                      { val: 1, label: "None" },
                      { val: 0, label: "Intubated (T)" } // Usually denoted as 1T
                    ].map(opt => (
                      <div 
                        key={opt.val}
                        className={`p-3 border rounded-md cursor-pointer text-sm transition-colors ${verbal === opt.val ? 'bg-primary-light border-primary font-medium text-primary-dark' : 'border-border hover:bg-surface-hover'}`}
                        onClick={() => setVerbal(opt.val)}
                      >
                        {opt.val === 0 ? "1T" : opt.val} - {opt.label}
                      </div>
                    ))}
                  </div>

                  {/* Motor Response */}
                  <div className="space-y-2">
                    <h3 className="font-bold border-b border-border pb-2">Motor Response (M)</h3>
                    {[
                      { val: 6, label: "Obeys commands" },
                      { val: 5, label: "Localising" },
                      { val: 4, label: "Normal flexion" },
                      { val: 3, label: "Abnormal flexion" },
                      { val: 2, label: "Extension" },
                      { val: 1, label: "None" }
                    ].map(opt => (
                      <div 
                        key={opt.val}
                        className={`p-3 border rounded-md cursor-pointer text-sm transition-colors ${motor === opt.val ? 'bg-primary-light border-primary font-medium text-primary-dark' : 'border-border hover:bg-surface-hover'}`}
                        onClick={() => setMotor(opt.val)}
                      >
                        {opt.val} - {opt.label}
                      </div>
                    ))}
                  </div>
                </div>

                <div className="bg-surface p-4 rounded-md border border-border flex justify-between items-center mt-6">
                  <div>
                    <div className="text-sm text-text-secondary">Total GCS Score</div>
                    <div className="text-2xl font-bold">
                      {verbal === 0 ? `${eye + 1 + motor}T` : (eye + verbal + motor)} / 15
                    </div>
                  </div>
                  <Button variant="primary" onClick={() => alert("GCS Score Logged!")}>Save Score</Button>
                </div>
              </div>
            ) : (
              <div className="py-12 text-center text-text-secondary">Please select a patient from the Bed Grid.</div>
            )}
          </CardContent>
        </Card>
      )}

    </div>
  );
}
