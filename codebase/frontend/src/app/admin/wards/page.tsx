"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function WardManagement() {
  const [wards, setWards] = useState<any[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({ name: "", code: "", category: "General" });

  useEffect(() => {
    fetchWards();
  }, []);

  const fetchWards = async () => {
    try {
      const res = await api.get("/wards");
      setWards(res.data);
    } catch (e) {
      setWards([
        { id: "1", code: "W-ICU", name: "Intensive Care Unit", category: "ICU", isActive: true },
        { id: "2", code: "W-GEN1", name: "General Medicine Male", category: "General", isActive: true },
        { id: "3", code: "W-MAT", name: "Maternity Ward", category: "Maternity", isActive: true }
      ]);
    }
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post("/wards", formData);
      alert("Ward created!");
      setShowModal(false);
      fetchWards();
    } catch (e) {
      console.error(e);
      alert("Failed to create ward");
    }
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Ward Configuration</h2>
          <p className="text-text-secondary text-sm">Manage hospital wards, categories, and bed mapping.</p>
        </div>
        <Button onClick={() => setShowModal(true)}>Create Ward</Button>
      </div>

      <Card>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">Code</th>
                <th className="p-4 font-medium text-text-secondary">Ward Name</th>
                <th className="p-4 font-medium text-text-secondary">Category</th>
                <th className="p-4 font-medium text-text-secondary">Status</th>
                <th className="p-4 font-medium text-text-secondary text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {wards.map((ward) => (
                <tr key={ward.id} className="hover:bg-surface-hover">
                  <td className="p-4 font-mono font-medium">{ward.code}</td>
                  <td className="p-4 font-semibold">{ward.name}</td>
                  <td className="p-4 text-text-secondary">{ward.category}</td>
                  <td className="p-4">
                    {ward.isActive ? <Badge variant="success">Active</Badge> : <Badge variant="secondary">Inactive</Badge>}
                  </td>
                  <td className="p-4 text-right">
                    <Button variant="secondary" size="sm" className="mr-2">Manage Beds</Button>
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
            <CardHeader><CardTitle>Create New Ward</CardTitle></CardHeader>
            <CardContent>
              <form onSubmit={handleSave} className="space-y-4">
                <div>
                  <label className="text-sm font-medium mb-1 block">Ward Code</label>
                  <Input required placeholder="e.g. W-ICU" value={formData.code} onChange={e => setFormData({...formData, code: e.target.value})} />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Ward Name</label>
                  <Input required placeholder="e.g. Intensive Care Unit" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Category</label>
                  <select 
                    className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm outline-none focus:border-primary bg-white"
                    value={formData.category} 
                    onChange={e => setFormData({...formData, category: e.target.value})}
                  >
                    <option>General</option>
                    <option>ICU</option>
                    <option>Maternity</option>
                    <option>Pediatric</option>
                    <option>Isolation</option>
                  </select>
                </div>
                <div className="flex justify-end gap-3 mt-6">
                  <Button type="button" variant="secondary" onClick={() => setShowModal(false)}>Cancel</Button>
                  <Button type="submit">Save Ward</Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
