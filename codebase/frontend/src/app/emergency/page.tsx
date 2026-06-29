"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function EmergencyDashboard() {
  const router = useRouter();
  const [assessments, setAssessments] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchActiveEmergencies();
  }, []);

  const fetchActiveEmergencies = async () => {
    try {
      const res = await api.get("/emergency/active");
      setAssessments(res.data);
    } catch (e) {
      console.error(e);
      // Fallback Mock Data
      setAssessments([
        { id: "1", patient: { firstName: "John", lastName: "Doe", uhid: "EMG-2026-0001" }, esiLevel: 1, chiefComplaint: "Cardiac Arrest", status: "PENDING" },
        { id: "2", patient: { firstName: "Unknown", lastName: "Male 1", uhid: "EMG-2026-0002" }, esiLevel: 2, chiefComplaint: "Unconscious, Head Trauma", status: "PENDING" }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const getEsiColor = (level: number) => {
    switch(level) {
      case 1: return "bg-red-600 text-white";
      case 2: return "bg-orange-500 text-white";
      case 3: return "bg-yellow-400 text-black";
      case 4: return "bg-green-500 text-white";
      case 5: return "bg-blue-500 text-white";
      default: return "bg-gray-300 text-black";
    }
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <h1 className="text-3xl font-bold text-error">Emergency Department (ER) Dashboard</h1>
        <div className="space-x-4">
          <Button variant="danger" onClick={() => router.push('/emergency/triage')}>+ New Triage Assessment</Button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Triage Queue */}
        <div className="col-span-3">
          <Card>
            <CardHeader>
              <CardTitle>Active ER Patients by Acuity</CardTitle>
            </CardHeader>
            <CardContent>
              {loading ? (
                <div>Loading...</div>
              ) : (
                <div className="space-y-4">
                  {assessments.map((a) => (
                    <div key={a.id} className="flex justify-between items-center p-4 border rounded-md shadow-sm hover:bg-surface-hover">
                      <div className="flex items-center gap-4">
                        <div className={`w-12 h-12 flex items-center justify-center rounded-full font-bold text-xl ${getEsiColor(a.esiLevel)}`}>
                          E{a.esiLevel}
                        </div>
                        <div>
                          <div className="font-bold text-lg">{a.patient?.firstName} {a.patient?.lastName}</div>
                          <div className="text-sm text-text-secondary">{a.patient?.uhid} • {a.chiefComplaint}</div>
                        </div>
                      </div>
                      <div className="flex gap-2">
                        <Button variant="secondary" size="sm">Admit to ER Bed</Button>
                        <Button variant="secondary" size="sm">Send to OT</Button>
                      </div>
                    </div>
                  ))}
                  {assessments.length === 0 && <div className="text-center text-text-secondary py-4">No active emergency patients.</div>}
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
