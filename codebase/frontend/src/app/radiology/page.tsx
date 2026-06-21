"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

export default function RadiologyDashboard() {
  const [activeTab, setActiveTab] = useState("worklist");
  const [selectedStudy, setSelectedStudy] = useState<any>(null);

  const handleOpenStudy = (study: any) => {
    setSelectedStudy(study);
    setActiveTab("reporting");
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Radiology Workstation</h1>
          <p className="text-text-secondary text-sm">Manage imaging orders and reports</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-border">
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'worklist' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => { setActiveTab('worklist'); setSelectedStudy(null); }}
        >
          Worklist
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'reporting' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('reporting')}
        >
          Reporting Station
        </button>
      </div>

      {/* Content */}
      {activeTab === "worklist" && (
        <Card>
          <CardHeader><CardTitle>Pending Studies</CardTitle></CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Patient</th>
                  <th className="p-3">Modality</th>
                  <th className="p-3">Study Description</th>
                  <th className="p-3">Indication</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Action</th>
                </tr>
              </thead>
              <tbody>
                <tr className="border-b border-surface-hover">
                  <td className="p-3">
                    <div className="font-medium">Rahul Sharma</div>
                    <div className="text-xs text-text-secondary">UHID: MED-2026-000001</div>
                  </td>
                  <td className="p-3 font-medium">MRI</td>
                  <td className="p-3">MRI Brain</td>
                  <td className="p-3 text-text-secondary truncate max-w-[150px]">Chronic headache, r/o space occupying lesion</td>
                  <td className="p-3"><Badge variant="info">Images Available</Badge></td>
                  <td className="p-3">
                    <Button variant="primary" size="sm" onClick={() => handleOpenStudy({ id: 1, modality: 'MRI', study: 'MRI Brain', patient: 'Rahul Sharma' })}>Open & Report</Button>
                  </td>
                </tr>
                <tr className="border-b border-surface-hover">
                  <td className="p-3">
                    <div className="font-medium">Sneha Patel</div>
                    <div className="text-xs text-text-secondary">UHID: MED-2026-000045</div>
                  </td>
                  <td className="p-3 font-medium">X-Ray</td>
                  <td className="p-3">Chest PA View</td>
                  <td className="p-3 text-text-secondary truncate max-w-[150px]">Persistent cough</td>
                  <td className="p-3"><Badge variant="warning">Scheduled</Badge></td>
                  <td className="p-3">
                    <Button variant="secondary" size="sm" disabled>Open & Report</Button>
                  </td>
                </tr>
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "reporting" && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* DICOM Viewer Placeholder */}
          <Card className="h-[600px] flex flex-col">
            <CardHeader className="border-b pb-4">
              <div className="flex justify-between items-center">
                <CardTitle>DICOM Viewer (Mock)</CardTitle>
                <div className="flex gap-2">
                  <Button variant="secondary" size="sm">Window/Level</Button>
                  <Button variant="secondary" size="sm">Measure</Button>
                  <Button variant="secondary" size="sm">Pan</Button>
                </div>
              </div>
            </CardHeader>
            <CardContent className="flex-1 bg-black flex items-center justify-center p-0 overflow-hidden relative">
              {selectedStudy ? (
                 <div className="text-center">
                    <div className="text-white/50 text-xl font-medium mb-4">MOCK IMAGE VIEWER</div>
                    <div className="text-white/80">{selectedStudy.patient} - {selectedStudy.study}</div>
                    <div className="absolute top-4 left-4 text-white/50 text-xs text-left">
                      <div>Name: {selectedStudy.patient}</div>
                      <div>ID: MED-001</div>
                      <div>DOB: 12/04/1985</div>
                    </div>
                    <div className="absolute bottom-4 right-4 text-white/50 text-xs text-right">
                      <div>{selectedStudy.modality}</div>
                      <div>W: 1500 L: -500</div>
                    </div>
                 </div>
              ) : (
                <div className="text-white/30">No study selected from worklist.</div>
              )}
            </CardContent>
          </Card>

          {/* Reporting Panel */}
          <Card className="h-[600px] flex flex-col">
            <CardHeader className="border-b pb-4">
               <div className="flex justify-between items-center">
                <CardTitle>Radiology Report</CardTitle>
                <select className="p-1 border border-border rounded text-sm outline-none">
                  <option>Load Template...</option>
                  <option>MRI Brain Normal</option>
                  <option>X-Ray Chest Normal</option>
                </select>
               </div>
            </CardHeader>
            <CardContent className="flex-1 overflow-y-auto pt-4 space-y-4">
              {selectedStudy ? (
                <>
                  <div className="bg-surface p-3 rounded text-sm space-y-1 mb-4 border border-border">
                    <div><span className="font-medium">Study:</span> {selectedStudy.study}</div>
                    <div><span className="font-medium">Clinical Indication:</span> Chronic headache</div>
                    <div><span className="font-medium">Technique:</span> Multi-planar, multi-sequence MRI of the brain was performed without IV contrast.</div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium mb-2">Findings</label>
                    <textarea 
                      className="w-full h-48 p-3 border border-border rounded-md focus:border-primary outline-none resize-none text-sm"
                      defaultValue={`The ventricles and basal cisterns are normal in size and configuration. \n\nNo acute intracranial hemorrhage, mass effect, or midline shift. \n\nNormal grey-white matter differentiation. \n\nThe visualized paranasal sinuses and mastoid air cells are clear.`}
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium mb-2 text-primary-dark">Impression</label>
                    <textarea 
                      className="w-full h-24 p-3 border border-border rounded-md focus:border-primary outline-none resize-none font-medium text-sm"
                      defaultValue={`1. Normal MRI of the Brain.\n2. No acute intracranial abnormality.`}
                    />
                  </div>

                  <div className="flex items-center gap-2 mb-4">
                    <input type="checkbox" id="critical" className="rounded" />
                    <label htmlFor="critical" className="text-sm font-medium text-error">Flag as Critical Finding</label>
                  </div>

                  <div className="flex justify-end gap-3 mt-auto">
                    <Button variant="secondary">Save Draft</Button>
                    <Button variant="primary" onClick={() => alert("Report Finalized and digitally signed!")}>Sign & Finalize</Button>
                  </div>
                </>
              ) : (
                <div className="text-text-secondary text-center mt-10">Select a study to begin reporting.</div>
              )}
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
