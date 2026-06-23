"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";

export default function OtSchedule() {
  const schedule = [
    { id: "OTB-401", time: "08:00 AM - 11:30 AM", theater: "OT-1 (Cardio)", patient: "David Chen", surgeon: "Dr. Roberts", procedure: "CABG", status: "In Progress" },
    { id: "OTB-402", time: "09:00 AM - 10:00 AM", theater: "OT-2 (Ortho)", patient: "Sarah Miller", surgeon: "Dr. Lee", procedure: "Arthroscopy Knee", status: "Scheduled" },
    { id: "OTB-403", time: "12:00 PM - 03:00 PM", theater: "OT-2 (Ortho)", patient: "James Bond", surgeon: "Dr. Lee", procedure: "Total Hip Replacement", status: "Scheduled" }
  ];

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Operation Theater Schedule</h2>
          <p className="text-text-secondary text-sm">Manage surgical bookings and theater allocations.</p>
        </div>
        <Button variant="primary">New Booking</Button>
      </div>

      <Card>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">Time</th>
                <th className="p-4 font-medium text-text-secondary">Theater</th>
                <th className="p-4 font-medium text-text-secondary">Patient</th>
                <th className="p-4 font-medium text-text-secondary">Procedure & Surgeon</th>
                <th className="p-4 font-medium text-text-secondary text-center">Status</th>
                <th className="p-4 font-medium text-text-secondary text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {schedule.map(s => (
                <tr key={s.id} className="hover:bg-surface-hover">
                  <td className="p-4 font-semibold text-primary">{s.time}</td>
                  <td className="p-4 font-medium">{s.theater}</td>
                  <td className="p-4">{s.patient}</td>
                  <td className="p-4">
                    <div className="font-bold">{s.procedure}</div>
                    <div className="text-xs text-text-secondary">{s.surgeon}</div>
                  </td>
                  <td className="p-4 text-center">
                    {s.status === "In Progress" ? <Badge variant="warning" className="animate-pulse">In Progress</Badge> : <Badge variant="default">Scheduled</Badge>}
                  </td>
                  <td className="p-4 text-right space-x-2">
                    <Button variant="secondary" size="sm">Checklist</Button>
                    <Button variant="primary" size="sm">Record</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>
    </div>
  );
}
