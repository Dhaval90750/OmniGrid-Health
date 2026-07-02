"use client";

import { useState, useEffect } from "react";
import { api } from "@/lib/api";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { useAuthStore } from "@/store/useAuthStore";

export default function LaboratoryDashboard() {
  const { user } = useAuthStore();
  const [orders, setOrders] = useState<any[]>([]);
  const [barcode, setBarcode] = useState("");
  const [loading, setLoading] = useState(true);

  const fetchOrders = async () => {
    try {
      // Mocking fetch since we use LabController which might not have full standard endpoints mapped
      // In real implementation: await api.get('/lab/orders?status=PENDING');
      const res = await api.get('/lab/orders'); 
      setOrders(res.data || []);
    } catch (e) {
      console.error(e);
      // fallback mock data
      setOrders([
        { id: "123", patientName: "John Doe", testName: "Complete Blood Count", status: "PENDING", priority: "STAT", barcode: "LAB-9991" },
        { id: "124", patientName: "Jane Smith", testName: "Lipid Panel", status: "PROCESSING", priority: "ROUTINE", barcode: "LAB-9992" }
      ]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const handleScan = (e: React.FormEvent) => {
    e.preventDefault();
    if (!barcode) return;
    const order = orders.find(o => o.barcode === barcode);
    if (order) {
      alert(`Scanned Order: ${order.testName} for ${order.patientName}`);
      // Open result entry modal
    } else {
      alert("Barcode not found in pending orders.");
    }
    setBarcode("");
  };

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-text-primary">Laboratory Information System (LIS)</h1>
          <p className="text-text-secondary mt-1">Manage sample tracking, resulting, and critical alerts.</p>
        </div>
        <div className="flex items-center gap-4">
          <form onSubmit={handleScan} className="flex gap-2">
            <Input 
              placeholder="Scan Sample Barcode..." 
              value={barcode} 
              onChange={e => setBarcode(e.target.value)} 
              autoFocus 
            />
            <Button type="submit">Scan</Button>
          </form>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="border-l-4 border-l-warning">
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary">Pending Samples</div>
            <div className="text-3xl font-bold text-text-primary mt-2">24</div>
          </CardContent>
        </Card>
        <Card className="border-l-4 border-l-primary">
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary">Processing</div>
            <div className="text-3xl font-bold text-text-primary mt-2">12</div>
          </CardContent>
        </Card>
        <Card className="border-l-4 border-l-success">
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary">Completed Today</div>
            <div className="text-3xl font-bold text-text-primary mt-2">89</div>
          </CardContent>
        </Card>
        <Card className="border-l-4 border-l-error">
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary">Critical Alerts</div>
            <div className="text-3xl font-bold text-text-primary mt-2">3</div>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Active Test Orders</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b text-sm text-text-secondary">
                  <th className="p-3">Order ID / Barcode</th>
                  <th className="p-3">Patient</th>
                  <th className="p-3">Test Name</th>
                  <th className="p-3">Priority</th>
                  <th className="p-3">Status</th>
                  <th className="p-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order, i) => (
                  <tr key={i} className="border-b hover:bg-background-secondary transition-colors">
                    <td className="p-3 font-mono text-sm">{order.barcode}</td>
                    <td className="p-3">{order.patientName}</td>
                    <td className="p-3">{order.testName}</td>
                    <td className="p-3">
                      <Badge variant={order.priority === 'STAT' ? 'error' : 'secondary'}>
                        {order.priority}
                      </Badge>
                    </td>
                    <td className="p-3">
                      <Badge variant={order.status === 'COMPLETED' ? 'success' : 'secondary'}>
                        {order.status}
                      </Badge>
                    </td>
                    <td className="p-3 text-right">
                      <Button variant="secondary" size="sm" onClick={() => alert("Enter results for " + order.id)}>
                        Enter Results
                      </Button>
                    </td>
                  </tr>
                ))}
                {orders.length === 0 && !loading && (
                  <tr>
                    <td colSpan={6} className="p-6 text-center text-text-secondary">No active orders found.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
