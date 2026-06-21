"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

// Dummy data for MVP UI
const DUMMY_VISITS = [
  { id: "1", patientName: "Rahul Sharma", uhid: "MED-2026-000001", time: "09:00 AM", status: "WAITING", type: "New Consult" },
  { id: "2", patientName: "Priya Patel", uhid: "MED-2026-000002", time: "09:30 AM", status: "IN_CONSULTATION", type: "Follow Up" },
  { id: "3", patientName: "Amit Kumar", uhid: "MED-2026-000003", time: "10:00 AM", status: "SCHEDULED", type: "New Consult" },
];

export default function DoctorDashboard() {
  const router = useRouter();

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "WAITING": return <Badge variant="warning">Waiting</Badge>;
      case "IN_CONSULTATION": return <Badge variant="info">In Consultation</Badge>;
      case "SCHEDULED": return <Badge variant="default">Scheduled</Badge>;
      case "COMPLETED": return <Badge variant="success">Completed</Badge>;
      default: return <Badge>{status}</Badge>;
    }
  };

  return (
    <div className="max-w-6xl space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Doctor Dashboard</h2>
          <p className="text-text-secondary text-sm">Today's OPD Appointments</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="md:col-span-3">
          <CardHeader>
            <CardTitle>My Patients (Today)</CardTitle>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-border bg-surface text-text-secondary text-sm">
                  <th className="p-4 font-medium">Time</th>
                  <th className="p-4 font-medium">Patient Details</th>
                  <th className="p-4 font-medium">Type</th>
                  <th className="p-4 font-medium">Status</th>
                  <th className="p-4 font-medium text-right">Action</th>
                </tr>
              </thead>
              <tbody>
                {DUMMY_VISITS.map((visit) => (
                  <tr key={visit.id} className="border-b border-surface-hover hover:bg-surface-hover transition-colors">
                    <td className="p-4 font-medium">{visit.time}</td>
                    <td className="p-4">
                      <div className="font-medium text-primary">{visit.patientName}</div>
                      <div className="text-xs text-text-tertiary">{visit.uhid}</div>
                    </td>
                    <td className="p-4">{visit.type}</td>
                    <td className="p-4">{getStatusBadge(visit.status)}</td>
                    <td className="p-4 text-right">
                      <Button variant="secondary" onClick={() => router.push(`/doctor/visit/${visit.id}`)}>
                        Start Consult
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>

        <div className="space-y-6">
          <Card>
            <CardHeader><CardTitle>Quick Stats</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div className="flex justify-between">
                <span className="text-text-secondary">Total</span>
                <span className="font-bold">12</span>
              </div>
              <div className="flex justify-between">
                <span className="text-text-secondary">Completed</span>
                <span className="font-bold text-success">4</span>
              </div>
              <div className="flex justify-between">
                <span className="text-text-secondary">Waiting</span>
                <span className="font-bold text-warning">3</span>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
