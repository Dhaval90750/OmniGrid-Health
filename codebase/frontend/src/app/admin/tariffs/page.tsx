"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function TariffManagement() {
  const [tariffs, setTariffs] = useState<any[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({ serviceCode: "", serviceName: "", category: "Consultation", price: "" });

  useEffect(() => {
    fetchTariffs();
  }, []);

  const fetchTariffs = async () => {
    try {
      const res = await api.get("/tariffs");
      setTariffs(res.data);
    } catch (e) {
      setTariffs([
        { id: "1", serviceCode: "CON-001", serviceName: "General Physician Consultation", category: "Consultation", price: 50.00, isActive: true },
        { id: "2", serviceCode: "LAB-CBC", serviceName: "Complete Blood Count", category: "Lab Test", price: 25.00, isActive: true },
        { id: "3", serviceCode: "RAD-XRAY", serviceName: "Chest X-Ray", category: "Radiology", price: 75.00, isActive: true }
      ]);
    }
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post("/tariffs", { ...formData, price: parseFloat(formData.price) });
      alert("Tariff created!");
      setShowModal(false);
      fetchTariffs();
    } catch (e) {
      console.error(e);
      alert("Failed to create tariff");
    }
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Tariff Management</h2>
          <p className="text-text-secondary text-sm">Configure hospital services and pricing.</p>
        </div>
        <Button onClick={() => setShowModal(true)}>Add New Tariff</Button>
      </div>

      <Card>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">Code</th>
                <th className="p-4 font-medium text-text-secondary">Service Name</th>
                <th className="p-4 font-medium text-text-secondary">Category</th>
                <th className="p-4 font-medium text-text-secondary">Price ($)</th>
                <th className="p-4 font-medium text-text-secondary">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {tariffs.map((tariff) => (
                <tr key={tariff.id} className="hover:bg-surface-hover">
                  <td className="p-4 font-mono font-medium">{tariff.serviceCode}</td>
                  <td className="p-4 font-semibold">{tariff.serviceName}</td>
                  <td className="p-4 text-text-secondary">{tariff.category}</td>
                  <td className="p-4 font-bold text-success-dark">${Number(tariff.price).toFixed(2)}</td>
                  <td className="p-4">
                    {tariff.isActive ? <Badge variant="success">Active</Badge> : <Badge variant="default">Inactive</Badge>}
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
            <CardHeader><CardTitle>Add New Service Tariff</CardTitle></CardHeader>
            <CardContent>
              <form onSubmit={handleSave} className="space-y-4">
                <div>
                  <label className="text-sm font-medium mb-1 block">Service Code</label>
                  <Input required placeholder="e.g. CON-002" value={formData.serviceCode} onChange={e => setFormData({...formData, serviceCode: e.target.value})} />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Service Name</label>
                  <Input required placeholder="e.g. Specialist Consultation" value={formData.serviceName} onChange={e => setFormData({...formData, serviceName: e.target.value})} />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Category</label>
                  <select 
                    className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm outline-none focus:border-primary bg-white"
                    value={formData.category} 
                    onChange={e => setFormData({...formData, category: e.target.value})}
                  >
                    <option>Consultation</option>
                    <option>Procedure</option>
                    <option>Lab Test</option>
                    <option>Radiology</option>
                    <option>Room Charge</option>
                  </select>
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Price ($)</label>
                  <Input type="number" step="0.01" required placeholder="0.00" value={formData.price} onChange={e => setFormData({...formData, price: e.target.value})} />
                </div>
                <div className="flex justify-end gap-3 mt-6">
                  <Button type="button" variant="secondary" onClick={() => setShowModal(false)}>Cancel</Button>
                  <Button type="submit">Save Tariff</Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
