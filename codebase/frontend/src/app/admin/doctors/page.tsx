"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function DoctorManagement() {
  const [doctors, setDoctors] = useState<any[]>([]);
  const [showDoctorModal, setShowDoctorModal] = useState(false);
  const [editingDoctor, setEditingDoctor] = useState<any>(null);
  
  const [formData, setFormData] = useState({
    employeeCode: "",
    fullName: "",
    department: "",
    role: "Consultant",
    contactNumber: "",
    isActive: true
  });

  useEffect(() => {
    fetchDoctors();
  }, []);

  const fetchDoctors = async () => {
    try {
      const res = await api.get("/staff/doctors");
      setDoctors(res.data);
    } catch (e) {
      console.error(e);
      setDoctors([]);
    }
  };

  const handleSaveDoctor = async () => {
    try {
      if (editingDoctor) {
        await api.put(`/staff/profiles/${editingDoctor.id}`, formData);
      } else {
        await api.post("/staff/profiles", formData);
      }
      setShowDoctorModal(false);
      fetchDoctors();
    } catch (e) {
      console.error("Failed to save doctor", e);
      alert("Failed to save doctor");
    }
  };

  const openAddModal = () => {
    setEditingDoctor(null);
    setFormData({ employeeCode: "", fullName: "", department: "", role: "Consultant", contactNumber: "", isActive: true });
    setShowDoctorModal(true);
  };

  const openEditModal = (doc: any) => {
    setEditingDoctor(doc);
    setFormData({ ...doc });
    setShowDoctorModal(true);
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Doctor Profiles</h2>
          <p className="text-text-secondary text-sm">Manage doctor schedules, privileges, and profiles.</p>
        </div>
        <Button onClick={openAddModal}>Add Doctor</Button>
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
                    <Button variant="secondary" size="sm" className="mr-2" onClick={() => alert("Scheduling module is part of Phase 2 OT/Staff Roster")}>Schedule</Button>
                    <Button variant="secondary" size="sm" onClick={() => openEditModal(doc)}>Edit</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>

      {/* Add / Edit Doctor Modal */}
      {showDoctorModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-surface p-6 rounded-lg max-w-md w-full shadow-xl space-y-4">
            <h3 className="text-xl font-bold">{editingDoctor ? "Edit Doctor" : "Add Doctor"}</h3>
            
            <div className="space-y-4">
              <Input 
                label="Employee Code" 
                value={formData.employeeCode} 
                onChange={(e) => setFormData({...formData, employeeCode: e.target.value})} 
              />
              <Input 
                label="Full Name" 
                value={formData.fullName} 
                onChange={(e) => setFormData({...formData, fullName: e.target.value})} 
              />
              <Input 
                label="Department" 
                value={formData.department} 
                onChange={(e) => setFormData({...formData, department: e.target.value})} 
              />
              <div className="flex flex-col gap-1 w-full">
                <label className="text-xs font-medium text-text-secondary">Role</label>
                <select 
                  value={formData.role} 
                  onChange={(e) => setFormData({...formData, role: e.target.value})}
                  className="bg-background border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none transition-colors border-border hover:border-text-tertiary focus:border-primary focus:ring-1 focus:ring-primary"
                >
                  <option value="Consultant">Consultant</option>
                  <option value="Resident">Resident</option>
                  <option value="Junior Doctor">Junior Doctor</option>
                </select>
              </div>
              <Input 
                label="Contact Number" 
                value={formData.contactNumber} 
                onChange={(e) => setFormData({...formData, contactNumber: e.target.value})} 
              />
              <div className="flex items-center gap-2 mt-2">
                <input 
                  type="checkbox" 
                  checked={formData.isActive}
                  onChange={(e) => setFormData({...formData, isActive: e.target.checked})}
                />
                <label className="text-sm">Active</label>
              </div>
            </div>

            <div className="flex justify-end gap-2 mt-6">
              <Button variant="secondary" onClick={() => setShowDoctorModal(false)}>Cancel</Button>
              <Button variant="primary" onClick={handleSaveDoctor}>Save</Button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
