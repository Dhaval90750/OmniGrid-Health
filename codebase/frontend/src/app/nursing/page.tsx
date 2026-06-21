"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

export default function NursingDashboard() {
  const [activeTab, setActiveTab] = useState("ward");
  const [selectedPatient, setSelectedPatient] = useState<any>(null);

  const handleSelectPatient = (patient: any, tab: string) => {
    setSelectedPatient(patient);
    setActiveTab(tab);
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
          Ward List (General Medical)
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'vitals' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('vitals')}
        >
          Vitals Entry
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'mar' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('mar')}
        >
          Medication Admin Record (MAR)
        </button>
      </div>

      {/* Content */}
      {activeTab === "ward" && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {/* Patient Card 1 */}
          <Card>
            <CardContent className="p-4">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <div className="font-bold text-lg">Rahul Sharma (42M)</div>
                  <div className="text-sm text-text-secondary">Bed: GM-01 | Dr. Anjali Desai</div>
                </div>
                <Badge variant="warning">High Risk Fall</Badge>
              </div>
              <div className="grid grid-cols-2 gap-2 text-sm mb-4">
                <div className="p-2 bg-surface rounded border border-border">
                  <div className="text-text-secondary text-xs mb-1">Last HR</div>
                  <div className="font-medium">88 bpm</div>
                </div>
                <div className="p-2 bg-surface rounded border border-border">
                  <div className="text-text-secondary text-xs mb-1">Last BP</div>
                  <div className="font-medium">130/85</div>
                </div>
              </div>
              <div className="flex gap-2">
                <Button variant="secondary" size="sm" className="flex-1" onClick={() => handleSelectPatient({ name: 'Rahul Sharma', bed: 'GM-01' }, 'vitals')}>Vitals</Button>
                <Button variant="primary" size="sm" className="flex-1" onClick={() => handleSelectPatient({ name: 'Rahul Sharma', bed: 'GM-01' }, 'mar')}>MAR</Button>
              </div>
            </CardContent>
          </Card>

          {/* Patient Card 2 */}
          <Card>
            <CardContent className="p-4">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <div className="font-bold text-lg">Sneha Patel (28F)</div>
                  <div className="text-sm text-text-secondary">Bed: GM-02 | Dr. Vikram Singh</div>
                </div>
                <Badge variant="success">Stable</Badge>
              </div>
              <div className="grid grid-cols-2 gap-2 text-sm mb-4">
                <div className="p-2 bg-surface rounded border border-border">
                  <div className="text-text-secondary text-xs mb-1">Last Temp</div>
                  <div className="font-medium">98.6 °F</div>
                </div>
                <div className="p-2 bg-surface rounded border border-border">
                  <div className="text-text-secondary text-xs mb-1">SpO2</div>
                  <div className="font-medium">99%</div>
                </div>
              </div>
              <div className="flex gap-2">
                <Button variant="secondary" size="sm" className="flex-1" onClick={() => handleSelectPatient({ name: 'Sneha Patel', bed: 'GM-02' }, 'vitals')}>Vitals</Button>
                <Button variant="primary" size="sm" className="flex-1" onClick={() => handleSelectPatient({ name: 'Sneha Patel', bed: 'GM-02' }, 'mar')}>MAR</Button>
              </div>
            </CardContent>
          </Card>
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
                    <input type="number" className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">Heart Rate (bpm)</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">SpO2 (%)</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">BP Systolic (mmHg)</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">BP Diastolic (mmHg)</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">Respiratory Rate</label>
                    <input type="number" className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" />
                  </div>
                </div>
                <div className="flex justify-end gap-4">
                  <Button variant="secondary" onClick={() => setActiveTab('ward')}>Cancel</Button>
                  <Button variant="primary" onClick={() => alert("Vitals saved!")}>Save Vitals</Button>
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
                      <tr className="border-b border-surface-hover">
                        <td className="p-3 text-text-secondary">Today, 08:00 AM</td>
                        <td className="p-3">98.4 °F</td>
                        <td className="p-3">78 bpm</td>
                        <td className="p-3">120/80</td>
                        <td className="p-3">98%</td>
                      </tr>
                      <tr className="border-b border-surface-hover">
                        <td className="p-3 text-text-secondary">Yesterday, 08:00 PM</td>
                        <td className="p-3">99.1 °F</td>
                        <td className="p-3">82 bpm</td>
                        <td className="p-3">125/82</td>
                        <td className="p-3">97%</td>
                      </tr>
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
                        <th className="p-3">Next Due</th>
                        <th className="p-3">Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr className="border-b border-surface-hover">
                        <td className="p-3 font-medium">Paracetamol 500mg Tab</td>
                        <td className="p-3">500mg, Oral</td>
                        <td className="p-3">TDS (8 AM, 2 PM, 8 PM)</td>
                        <td className="p-3 text-error font-bold">Today, 2:00 PM (Overdue)</td>
                        <td className="p-3">
                           <Button variant="primary" size="sm" onClick={() => alert("Marked as Given!")}>Mark Given</Button>
                        </td>
                      </tr>
                      <tr className="border-b border-surface-hover bg-success/5">
                        <td className="p-3 font-medium">Ceftriaxone 1g Injection</td>
                        <td className="p-3">1g, IV</td>
                        <td className="p-3">BD (8 AM, 8 PM)</td>
                        <td className="p-3 text-success font-bold">Given at 8:15 AM</td>
                        <td className="p-3">
                           <Button variant="secondary" size="sm" disabled>Completed</Button>
                        </td>
                      </tr>
                      <tr className="border-b border-surface-hover">
                        <td className="p-3 font-medium">Pantoprazole 40mg Inj</td>
                        <td className="p-3">40mg, IV</td>
                        <td className="p-3">OD (8 AM)</td>
                        <td className="p-3 text-text-secondary">Tomorrow, 8:00 AM</td>
                        <td className="p-3">
                           <Button variant="secondary" size="sm" disabled>Not Due</Button>
                        </td>
                      </tr>
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
