"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

export default function OtDashboard() {
  const [activeTab, setActiveTab] = useState("schedule");
  const [selectedSurgery, setSelectedSurgery] = useState<any>(null);

  // WHO Checklist State
  const [signIn, setSignIn] = useState(false);
  const [timeOut, setTimeOut] = useState(false);
  const [signOut, setSignOut] = useState(false);

  const handleStartSurgery = (surgery: any) => {
    setSelectedSurgery(surgery);
    setActiveTab("execution");
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Operation Theater Control Center</h1>
          <p className="text-text-secondary text-sm">Manage OT schedules, WHO safety checklists, and operative notes</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-border">
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'schedule' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => { setActiveTab('schedule'); setSelectedSurgery(null); }}
        >
          OT Schedule Grid
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'execution' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('execution')}
        >
          Surgery Execution Panel
        </button>
      </div>

      {/* Content */}
      {activeTab === "schedule" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Today's Surgeries</CardTitle>
              <Button variant="secondary">Book New Surgery</Button>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Time</th>
                  <th className="p-3">OT Room</th>
                  <th className="p-3">Patient</th>
                  <th className="p-3">Procedure</th>
                  <th className="p-3">Surgeon</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Action</th>
                </tr>
              </thead>
              <tbody>
                <tr className="border-b border-surface-hover">
                  <td className="p-3 font-medium">08:00 AM</td>
                  <td className="p-3 font-bold text-primary-dark">OT-1 (Major)</td>
                  <td className="p-3">
                    <div className="font-medium">Suresh Kumar</div>
                    <div className="text-xs text-text-secondary">MED-2026-00102</div>
                  </td>
                  <td className="p-3 font-medium">Laparoscopic Cholecystectomy</td>
                  <td className="p-3">Dr. M. Patel</td>
                  <td className="p-3"><Badge variant="warning">Scheduled</Badge></td>
                  <td className="p-3">
                    <Button variant="primary" size="sm" onClick={() => handleStartSurgery({ 
                      patient: 'Suresh Kumar', 
                      procedure: 'Laparoscopic Cholecystectomy',
                      surgeon: 'Dr. M. Patel',
                      ot: 'OT-1'
                    })}>Start Surgery</Button>
                  </td>
                </tr>
                <tr className="border-b border-surface-hover">
                  <td className="p-3 font-medium">10:30 AM</td>
                  <td className="p-3 font-bold text-primary-dark">OT-2 (Ortho)</td>
                  <td className="p-3">
                    <div className="font-medium">Anita Desai</div>
                    <div className="text-xs text-text-secondary">MED-2026-00088</div>
                  </td>
                  <td className="p-3 font-medium">Total Knee Replacement (R)</td>
                  <td className="p-3">Dr. V. Sharma</td>
                  <td className="p-3"><Badge variant="info">In Progress</Badge></td>
                  <td className="p-3">
                    <Button variant="secondary" size="sm" onClick={() => handleStartSurgery({ 
                      patient: 'Anita Desai', 
                      procedure: 'Total Knee Replacement (R)',
                      surgeon: 'Dr. V. Sharma',
                      ot: 'OT-2'
                    })}>Resume Execution</Button>
                  </td>
                </tr>
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "execution" && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* WHO Checklist Panel */}
          <Card className="lg:col-span-1">
            <CardHeader className="bg-surface border-b border-border pb-4">
              <CardTitle className="text-lg">WHO Safety Checklist</CardTitle>
            </CardHeader>
            <CardContent className="pt-4 space-y-6">
               {selectedSurgery ? (
                 <>
                  {/* Sign In */}
                  <div className={`p-4 border rounded-md transition-colors ${signIn ? 'bg-success/10 border-success' : 'border-border'}`}>
                    <div className="flex items-center gap-2 mb-2">
                      <input type="checkbox" className="w-4 h-4 cursor-pointer" checked={signIn} onChange={(e) => setSignIn(e.target.checked)} />
                      <h3 className="font-bold">Sign In (Before Induction)</h3>
                    </div>
                    <ul className="text-xs text-text-secondary list-disc pl-6 space-y-1">
                      <li>Patient confirmed identity, site, procedure, and consent.</li>
                      <li>Site marked.</li>
                      <li>Anesthesia safety check completed.</li>
                    </ul>
                  </div>

                  {/* Time Out */}
                  <div className={`p-4 border rounded-md transition-colors ${timeOut ? 'bg-success/10 border-success' : 'border-border'}`}>
                    <div className="flex items-center gap-2 mb-2">
                      <input type="checkbox" className="w-4 h-4 cursor-pointer" checked={timeOut} onChange={(e) => setTimeOut(e.target.checked)} disabled={!signIn} />
                      <h3 className="font-bold">Time Out (Before Incision)</h3>
                    </div>
                    <ul className="text-xs text-text-secondary list-disc pl-6 space-y-1">
                      <li>Confirm all team members introduced by name and role.</li>
                      <li>Confirm patient name, procedure, and where incision will be made.</li>
                      <li>Antibiotic prophylaxis given within last 60 mins.</li>
                    </ul>
                  </div>

                  {/* Sign Out */}
                  <div className={`p-4 border rounded-md transition-colors ${signOut ? 'bg-success/10 border-success' : 'border-border'}`}>
                    <div className="flex items-center gap-2 mb-2">
                      <input type="checkbox" className="w-4 h-4 cursor-pointer" checked={signOut} onChange={(e) => setSignOut(e.target.checked)} disabled={!timeOut} />
                      <h3 className="font-bold">Sign Out (Before pt leaves OT)</h3>
                    </div>
                    <ul className="text-xs text-text-secondary list-disc pl-6 space-y-1">
                      <li>Nurse verbally confirms name of procedure.</li>
                      <li>Instrument, sponge and needle counts are correct.</li>
                      <li>Specimen labeled (read specimen labels aloud).</li>
                    </ul>
                  </div>
                 </>
               ) : (
                 <div className="text-center text-text-secondary text-sm">Select a surgery from the schedule.</div>
               )}
            </CardContent>
          </Card>

          {/* Operative Note Panel */}
          <Card className="lg:col-span-2">
            <CardHeader className="flex justify-between items-center border-b border-border pb-4">
              <CardTitle className="text-lg">Operation Note</CardTitle>
              {selectedSurgery && <Badge variant="info">{selectedSurgery.patient} | {selectedSurgery.procedure}</Badge>}
            </CardHeader>
            <CardContent className="pt-4">
              {selectedSurgery ? (
                <div className="space-y-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium mb-1">Pre-Operative Diagnosis</label>
                      <input type="text" className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary" defaultValue="Symptomatic Cholelithiasis" />
                    </div>
                    <div>
                      <label className="block text-sm font-medium mb-1">Post-Operative Diagnosis</label>
                      <input type="text" className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary" defaultValue="Same" />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium mb-1">Procedure Performed</label>
                    <input type="text" className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary" defaultValue={selectedSurgery.procedure} />
                  </div>

                  <div>
                    <label className="block text-sm font-medium mb-1">Operative Findings & Procedure Details</label>
                    <textarea 
                      className="w-full h-48 p-3 border border-border rounded-md focus:border-primary outline-none resize-none text-sm"
                      placeholder="Type detailed operative note here..."
                      defaultValue={`Under GA, pneumoperitoneum created. \nGallbladder was distended with multiple stones. \nCystic artery and duct identified, clipped and divided. \nGallbladder extracted via umbilical port. \nHemostasis achieved.`}
                    />
                  </div>

                  <div className="grid grid-cols-3 gap-4">
                    <div>
                      <label className="block text-sm font-medium mb-1">Estimated Blood Loss (ml)</label>
                      <input type="number" className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary" defaultValue={20} />
                    </div>
                    <div className="col-span-2 flex items-center gap-4 pt-6">
                       <div className="flex items-center gap-2">
                         <input type="checkbox" id="specimen" defaultChecked className="w-4 h-4 cursor-pointer" />
                         <label htmlFor="specimen" className="text-sm font-medium">Specimen Sent for Biopsy</label>
                       </div>
                    </div>
                  </div>

                  <div className="flex justify-end gap-3 pt-4 border-t border-border mt-6">
                    <Button variant="secondary">Save Draft</Button>
                    <Button variant="primary" disabled={!signOut} onClick={() => alert("Surgery Completed & Note Finalized!")}>
                      {signOut ? "Finalize Surgery Record" : "Complete WHO Sign Out First"}
                    </Button>
                  </div>
                </div>
              ) : (
                <div className="py-24 text-center text-text-secondary">Please select a surgery from the schedule to begin execution.</div>
              )}
            </CardContent>
          </Card>
        </div>
      )}

    </div>
  );
}
