"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function DoctorDashboard() {
  const [queue, setQueue] = useState<any[]>([]);
  const [activeVisit, setActiveVisit] = useState<any>(null);

  // Hardcoded doctor ID for demo purposes
  const DOCTOR_ID = "1"; 

  useEffect(() => {
    fetchQueue();
  }, []);

  const fetchQueue = async () => {
    try {
      // const res = await api.get(`/staff/doctors/${DOCTOR_ID}/queue`);
      // setQueue(res.data);
      setQueue([
        { id: "v1", tokenNumber: 12, patient: { name: "Alice Smith", uhid: "UHID-9012" }, status: "WAITING", chiefComplaint: "Chest Pain" },
        { id: "v2", tokenNumber: 13, patient: { name: "Bob Johnson", uhid: "UHID-9013" }, status: "WAITING", chiefComplaint: "Follow up" },
        { id: "v3", tokenNumber: 14, patient: { name: "Charlie Brown", uhid: "UHID-9014" }, status: "WAITING", chiefComplaint: "Palpitations" }
      ]);
    } catch (e) {
      console.error(e);
    }
  };

  const updateStatus = async (visitId: string, status: string, queueStatus: string) => {
    try {
      // await api.put(`/visits/${visitId}/status`, { status, queueStatus });
      
      if (status === "COMPLETED") {
        setQueue(queue.filter(v => v.id !== visitId));
        setActiveVisit(null);
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
                  onClick={() => setActiveVisit(visit)}
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
                    <Button size="sm" variant="primary" onClick={() => updateStatus(activeVisit.id, "COMPLETED", "DONE")}>Complete Visit</Button>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="p-6 flex-1">
                <div className="space-y-6">
                  <div>
                    <h3 className="text-sm font-semibold mb-2">Chief Complaint</h3>
                    <div className="p-3 bg-surface rounded-md border border-border text-sm">
                      {activeVisit.chiefComplaint}
                    </div>
                  </div>

                  <div>
                    <h3 className="text-sm font-semibold mb-2">Clinical Notes</h3>
                    <textarea 
                      className="w-full h-40 p-3 border border-border rounded-md text-sm outline-none focus:border-primary resize-none"
                      placeholder="Type clinical notes, history, and examination findings here..."
                    ></textarea>
                  </div>

                  <div className="flex gap-4">
                    <Button variant="secondary" className="flex-1">Order Labs</Button>
                    <Button variant="secondary" className="flex-1">Order Radiology</Button>
                    <Button variant="secondary" className="flex-1">Prescribe Rx</Button>
                  </div>
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
