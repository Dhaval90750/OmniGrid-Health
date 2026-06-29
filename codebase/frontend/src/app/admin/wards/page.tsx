"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

import { useRouter } from "next/navigation";

export default function WardManagement() {
  const router = useRouter();
  const [wards, setWards] = useState<any[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [editingWard, setEditingWard] = useState<any>(null);
  const [formData, setFormData] = useState({ code: "", name: "", category: "General", isActive: true });

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
      if (editingWard) {
        await api.put(`/wards/${editingWard.id}`, formData);
        alert("Ward updated!");
      } else {
        await api.post("/wards", formData);
        alert("Ward created!");
      }
      setShowModal(false);
      fetchWards();
    } catch (e) {
      console.error(e);
      alert("Failed to save ward");
    }
  };

  const openAddModal = () => {
    setEditingWard(null);
    setFormData({ code: "", name: "", category: "General", isActive: true });
    setShowModal(true);
  };

  const openEditModal = (ward: any) => {
    setEditingWard(ward);
    setFormData({ code: ward.code, name: ward.name, category: ward.category, isActive: ward.isActive !== false });
    setShowModal(true);
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Ward Configuration</h2>
          <p className="text-text-secondary text-sm">Manage hospital wards, categories, and bed mapping.</p>
        </div>
        <Button onClick={openAddModal}>Create Ward</Button>
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
                    {ward.isActive ? <Badge variant="success">Active</Badge> : <Badge variant="default">Inactive</Badge>}
                  </td>
                  <td className="p-4 text-right">
                    <Button variant="secondary" size="sm" className="mr-2" onClick={() => router.push('/operations/beds')}>Manage Beds</Button>
                    <Button variant="secondary" size="sm" onClick={() => openEditModal(ward)}>Edit</Button>
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
            <CardHeader><CardTitle>{editingWard ? "Edit Ward" : "Create New Ward"}</CardTitle></CardHeader>
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
                    className="w-full bg-background border border-border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-primary"
                    value={formData.category}
                    onChange={e => setFormData({...formData, category: e.target.value})}
                  >
                    <option value="General">General</option>
                    <option value="ICU">ICU</option>
                    <option value="Maternity">Maternity</option>
                    <option value="Pediatrics">Pediatrics</option>
                    <option value="Emergency">Emergency</option>
                  </select>
                </div>
                {editingWard && (
                  <div className="flex items-center gap-2 mt-2">
                    <input 
                      type="checkbox" 
                      checked={formData.isActive}
                      onChange={(e) => setFormData({...formData, isActive: e.target.checked})}
                    />
                    <label className="text-sm">Active</label>
                  </div>
                )}
                <div className="flex justify-end gap-2 pt-4">
                  <Button type="button" variant="secondary" onClick={() => setShowModal(false)}>Cancel</Button>
                  <Button type="submit" variant="primary">Save Ward</Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
