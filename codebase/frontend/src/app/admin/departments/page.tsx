"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function DepartmentManagement() {
  const [departments, setDepartments] = useState<any[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({ name: "", code: "", description: "" });

  useEffect(() => {
    fetchDepartments();
  }, []);

  const fetchDepartments = async () => {
    try {
      const res = await api.get("/departments");
      setDepartments(res.data);
    } catch (e) {
      console.error(e);
      setDepartments([
        { id: "1", name: "Cardiology", code: "CARD", description: "Heart Center", isActive: true },
        { id: "2", name: "Orthopedics", code: "ORTHO", description: "Bone & Joint", isActive: true }
      ]);
    }
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post("/departments", { ...formData, isActive: true });
      alert("Department created!");
      setShowModal(false);
      fetchDepartments();
    } catch (e) {
      console.error(e);
      alert("Failed to create department");
    }
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Department Management</h2>
          <p className="text-text-secondary text-sm">Create and organize hospital clinical departments.</p>
        </div>
        <Button onClick={() => setShowModal(true)}>Add Department</Button>
      </div>

      <Card>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">Code</th>
                <th className="p-4 font-medium text-text-secondary">Name</th>
                <th className="p-4 font-medium text-text-secondary">Description</th>
                <th className="p-4 font-medium text-text-secondary">Status</th>
                <th className="p-4 font-medium text-text-secondary text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {departments.map((dept) => (
                <tr key={dept.id} className="hover:bg-surface-hover">
                  <td className="p-4 font-mono font-medium">{dept.code}</td>
                  <td className="p-4 font-semibold">{dept.name}</td>
                  <td className="p-4 text-text-secondary">{dept.description}</td>
                  <td className="p-4">
                    {dept.isActive ? <Badge variant="success">Active</Badge> : <Badge variant="secondary">Inactive</Badge>}
                  </td>
                  <td className="p-4 text-right">
                    <Button variant="secondary" size="sm">Edit</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-[500px]">
            <CardHeader><CardTitle>Create Department</CardTitle></CardHeader>
            <CardContent>
              <form onSubmit={handleSave} className="space-y-4">
                <div>
                  <label className="text-sm font-medium mb-1 block">Department Code</label>
                  <Input 
                    required 
                    placeholder="e.g. CARD" 
                    value={formData.code} 
                    onChange={e => setFormData({...formData, code: e.target.value})} 
                  />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Department Name</label>
                  <Input 
                    required 
                    placeholder="e.g. Cardiology" 
                    value={formData.name} 
                    onChange={e => setFormData({...formData, name: e.target.value})} 
                  />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Description</label>
                  <textarea 
                    className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary"
                    rows={3}
                    value={formData.description}
                    onChange={e => setFormData({...formData, description: e.target.value})} 
                  />
                </div>
                <div className="flex justify-end gap-3 mt-6">
                  <Button type="button" variant="secondary" onClick={() => setShowModal(false)}>Cancel</Button>
                  <Button type="submit">Save Department</Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
