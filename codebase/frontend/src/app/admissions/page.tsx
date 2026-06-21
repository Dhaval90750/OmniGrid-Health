"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";

// Dummy data for IPD Census
const DUMMY_CENSUS = [
  { id: "1", patientName: "Rahul Sharma", uhid: "MED-2026-000001", ward: "General Male", bed: "GM-101", admittingDr: "Dr. Smith", admissionDate: "2026-06-19", los: "3 days" },
  { id: "2", patientName: "Priya Patel", uhid: "MED-2026-000002", ward: "Maternity", bed: "MAT-205", admittingDr: "Dr. Jones", admissionDate: "2026-06-20", los: "2 days" },
  { id: "3", patientName: "Amit Kumar", uhid: "MED-2026-000003", ward: "ICU", bed: "ICU-03", admittingDr: "Dr. Smith", admissionDate: "2026-06-21", los: "1 day" },
];

export default function IpdDashboard() {
  const router = useRouter();
  const [searchQuery, setSearchQuery] = useState("");

  return (
    <div className="max-w-6xl space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">IPD Census</h2>
          <p className="text-text-secondary text-sm">Currently admitted patients</p>
        </div>
        <Button onClick={() => router.push("/admissions/new")}>+ Admit Patient</Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        
        {/* Quick Stats */}
        <div className="md:col-span-4 grid grid-cols-1 md:grid-cols-4 gap-4">
          <Card>
            <CardContent className="p-4 flex flex-col items-center justify-center">
              <div className="text-sm text-text-secondary">Total Admissions</div>
              <div className="text-3xl font-bold text-primary">45</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4 flex flex-col items-center justify-center">
              <div className="text-sm text-text-secondary">Available Beds</div>
              <div className="text-3xl font-bold text-success">12</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4 flex flex-col items-center justify-center">
              <div className="text-sm text-text-secondary">ICU Occupancy</div>
              <div className="text-3xl font-bold text-error">95%</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4 flex flex-col items-center justify-center">
              <div className="text-sm text-text-secondary">Pending Discharges</div>
              <div className="text-3xl font-bold text-warning">8</div>
            </CardContent>
          </Card>
        </div>

        {/* Data Table */}
        <Card className="md:col-span-4">
          <CardHeader>
            <div className="flex justify-between items-center w-full">
              <CardTitle>Active Admissions</CardTitle>
              <div className="w-64">
                <Input 
                  placeholder="Search by UHID, Ward, Bed..." 
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-border bg-surface text-text-secondary text-sm">
                  <th className="p-4 font-medium">Ward & Bed</th>
                  <th className="p-4 font-medium">Patient Details</th>
                  <th className="p-4 font-medium">Admitting Dr</th>
                  <th className="p-4 font-medium">LOS</th>
                  <th className="p-4 font-medium text-right">Action</th>
                </tr>
              </thead>
              <tbody>
                {DUMMY_CENSUS.map((adm) => (
                  <tr key={adm.id} className="border-b border-surface-hover hover:bg-surface-hover transition-colors">
                    <td className="p-4">
                      <div className="font-bold text-primary">{adm.ward}</div>
                      <div className="text-sm text-text-secondary">Bed: {adm.bed}</div>
                    </td>
                    <td className="p-4">
                      <div className="font-medium">{adm.patientName}</div>
                      <div className="text-xs text-text-tertiary">{adm.uhid}</div>
                    </td>
                    <td className="p-4">{adm.admittingDr}</td>
                    <td className="p-4">
                      <Badge variant="info">{adm.los}</Badge>
                    </td>
                    <td className="p-4 text-right">
                      <Button variant="secondary" size="sm">Manage</Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>

      </div>
    </div>
  );
}
