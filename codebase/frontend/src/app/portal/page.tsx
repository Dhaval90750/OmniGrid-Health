"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function PatientPortalDashboard() {
  const router = useRouter();
  const [data, setData] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  // MOCK UUID for the patient (In real app, this comes from auth context)
  const patientId = "00000000-0000-0000-0000-000000000000";

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const res = await api.get(`/portal/dashboard/${patientId}`);
      setData(res.data);
    } catch (e) {
      console.error(e);
      // Fallback Mock Data if Backend isn't fully seeded for Portal
      setData({
        patientName: "David Chen",
        uhid: "MED-2026-8842",
        appointments: [
          { id: "APT-9921", doctor: "Dr. Sarah Miller", dept: "Cardiology", date: "Tomorrow, 10:00 AM", type: "Teleconsultation", status: "Confirmed" }
        ],
        prescriptions: [
          { id: "RX-1042", doctor: "Dr. R. Iyer", date: "Oct 15, 2026", status: "Active" }
        ],
        labReports: [
          { id: "RPT-101", title: "Complete Blood Count", date: "Oct 12, 2026", status: "Final" },
          { id: "RPT-102", title: "Chest X-Ray", date: "Oct 10, 2026", status: "Final" }
        ]
      });
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-text-secondary">Loading your portal...</div>;
  }

  if (!data) return null;

  return (
    <div className="max-w-5xl mx-auto space-y-8 pb-12">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h1 className="text-3xl font-bold text-text-primary">Welcome, {data.patientName}!</h1>
          <p className="text-text-secondary text-sm">UHID: {data.uhid}</p>
        </div>
        <Button variant="primary">Book New Appointment</Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Appointments Column */}
        <div className="space-y-6">
          <h2 className="text-xl font-semibold text-text-primary">Upcoming Appointments</h2>
          {data.appointments?.length === 0 && <div className="text-sm text-text-secondary">No upcoming appointments.</div>}
          
          {data.appointments?.map((apt: any) => (
            <Card key={apt.id} className="border-info border-l-4">
              <CardContent className="p-6">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <div className="font-bold text-lg text-text-primary">{apt.doctor}</div>
                    <div className="text-text-secondary text-sm">{apt.dept}</div>
                  </div>
                  <div className="bg-info/10 text-info-dark px-3 py-1 rounded text-xs font-bold">
                    {apt.type}
                  </div>
                </div>
                <div className="font-semibold text-primary mb-4">
                  {apt.date}
                </div>
                <div className="flex gap-2">
                  {apt.type === "Teleconsultation" ? (
                    <Button variant="primary" className="w-full" onClick={() => router.push('/portal/telemedicine')}>Join Video Call</Button>
                  ) : (
                    <Button variant="primary" className="w-full">Get Directions</Button>
                  )}
                  <Button variant="secondary">Reschedule</Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Records Column */}
        <div className="space-y-6">
          
          <h2 className="text-xl font-semibold text-text-primary">Active Prescriptions</h2>
          <Card>
            <CardContent className="p-0">
              <div className="divide-y divide-border">
                {data.prescriptions?.length === 0 && <div className="p-4 text-sm text-text-secondary">No active prescriptions.</div>}
                {data.prescriptions?.map((rx: any) => (
                  <div key={rx.id} className="p-4 flex justify-between items-center hover:bg-surface-hover cursor-pointer transition-colors">
                    <div>
                      <div className="font-bold text-text-primary">{rx.id}</div>
                      <div className="text-xs text-text-secondary">Prescribed by {rx.doctor} on {rx.date}</div>
                    </div>
                    <Badge variant="success">Active</Badge>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          <h2 className="text-xl font-semibold text-text-primary">Recent Test Results</h2>
          <Card>
            <CardContent className="p-0">
              <div className="divide-y divide-border">
                {data.labReports?.length === 0 && <div className="p-4 text-sm text-text-secondary">No recent reports.</div>}
                {data.labReports?.map((rpt: any) => (
                  <div key={rpt.id} className="p-4 flex justify-between items-center hover:bg-surface-hover cursor-pointer transition-colors">
                    <div>
                      <div className="font-bold text-text-primary">{rpt.title}</div>
                      <div className="text-xs text-text-secondary">{rpt.date}</div>
                    </div>
                    <Button variant="secondary" size="sm">Download PDF</Button>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
          
        </div>
      </div>
    </div>
  );
}
