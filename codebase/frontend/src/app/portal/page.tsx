"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";

export default function PatientPortalDashboard() {
  const appointments = [
    { id: "APT-9921", doctor: "Dr. Sarah Miller", dept: "Cardiology", date: "Tomorrow, 10:00 AM", type: "Teleconsultation", status: "Confirmed" }
  ];

  const reports = [
    { id: "RPT-101", title: "Complete Blood Count", date: "Oct 12, 2026", status: "Final" },
    { id: "RPT-102", title: "Chest X-Ray", date: "Oct 10, 2026", status: "Final" }
  ];

  return (
    <div className="max-w-5xl mx-auto space-y-8 py-8">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h1 className="text-3xl font-bold text-text-primary">Welcome, David Chen!</h1>
          <p className="text-text-secondary text-sm">UHID: 7721-8842</p>
        </div>
        <Button variant="primary">Book New Appointment</Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Appointments Column */}
        <div className="space-y-6">
          <h2 className="text-xl font-semibold text-text-primary">Upcoming Appointments</h2>
          {appointments.map(apt => (
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
                  <Button variant="primary" className="w-full" onClick={() => window.location.href='/portal/telemedicine'}>Join Video Call</Button>
                  <Button variant="secondary">Reschedule</Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Reports Column */}
        <div className="space-y-6">
          <h2 className="text-xl font-semibold text-text-primary">Recent Test Results</h2>
          <Card>
            <CardContent className="p-0">
              <div className="divide-y divide-border">
                {reports.map(rpt => (
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
