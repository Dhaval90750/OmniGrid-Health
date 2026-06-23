"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function DoctorManagement() {
  const [doctors, setDoctors] = useState<any[]>([]);

  useEffect(() => {
    fetchDoctors();
  }, []);

  const fetchDoctors = async () => {
    try {
      const res = await api.get("/staff/doctors");
      setDoctors(res.data);
    } catch (e) {
      console.error(e);
      setDoctors([
        { id: "1", fullName: "Dr. Adams", employeeCode: "EMP001", role: "Consultant", department: "Cardiology", isActive: true },
        { id: "2", fullName: "Dr. Lee", employeeCode: "EMP002", role: "Resident", department: "Orthopedics", isActive: true }
      ]);
    }
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Doctor Profiles</h2>
          <p className="text-text-secondary text-sm">Manage doctor schedules, privileges, and profiles.</p>
        </div>
        <Button>Add Doctor</Button>
      </div>

      <Card>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">EMP Code</th>
                <th className="p-4 font-medium text-text-secondary">Name</th>
                <th className="p-4 font-medium text-text-secondary">Department</th>
                <th className="p-4 font-medium text-text-secondary">Role</th>
                <th className="p-4 font-medium text-text-secondary">Status</th>
                <th className="p-4 font-medium text-text-secondary text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {doctors.map((doc) => (
                <tr key={doc.id} className="hover:bg-surface-hover">
                  <td className="p-4 font-mono font-medium">{doc.employeeCode}</td>
                  <td className="p-4 font-semibold">{doc.fullName}</td>
                  <td className="p-4">{doc.department}</td>
                  <td className="p-4 text-text-secondary">{doc.role}</td>
                  <td className="p-4">
                    {doc.isActive ? <Badge variant="success">Active</Badge> : <Badge variant="default">Inactive</Badge>}
                  </td>
                  <td className="p-4 text-right">
                    <Button variant="secondary" size="sm" className="mr-2">Schedule</Button>
                    <Button variant="secondary" size="sm">Edit</Button>
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
