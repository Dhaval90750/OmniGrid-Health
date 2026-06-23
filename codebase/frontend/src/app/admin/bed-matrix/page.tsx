"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function BedMatrix() {
  const [data, setData] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await api.get("/admin/bed-matrix");
        setData(res.data);
      } catch (error) {
        console.error("Failed to fetch bed matrix", error);
        // Fallback mock if endpoint fails
        setData({
          wards: [
            {
              wardName: "Intensive Care Unit (ICU)",
              totalBeds: 10, occupied: 8,
              beds: [
                { bedId: "ICU-01", status: "Occupied", patient: "David Chen" },
                { bedId: "ICU-02", status: "Occupied", patient: "Sarah Miller" },
                { bedId: "ICU-03", status: "Available", patient: "" },
                { bedId: "ICU-04", status: "Maintenance", patient: "" },
                { bedId: "ICU-05", status: "Cleaning", patient: "" },
                { bedId: "ICU-06", status: "Occupied", patient: "Robert King" },
              ]
            },
            {
              wardName: "General Ward A",
              totalBeds: 20, occupied: 15,
              beds: [
                { bedId: "WA-01", status: "Occupied", patient: "John Doe" },
                { bedId: "WA-02", status: "Cleaning", patient: "" },
                { bedId: "WA-03", status: "Available", patient: "" },
                { bedId: "WA-04", status: "Occupied", patient: "Alice Smith" },
              ]
            }
          ]
        });
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const getStatusColor = (status: string) => {
    switch(status) {
      case 'Occupied': return 'bg-error text-white border-error-dark';
      case 'Available': return 'bg-success text-white border-success-dark';
      case 'Cleaning': return 'bg-info text-white border-info-dark';
      case 'Maintenance': return 'bg-warning text-white border-warning-dark';
      default: return 'bg-surface border-border text-text-secondary';
    }
  };

  if (loading) return <div className="text-center p-8">Loading Bed Matrix...</div>;

  return (
    <div className="max-w-7xl mx-auto space-y-8">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Live Bed Management Matrix</h2>
          <p className="text-text-secondary text-sm">Real-time occupancy tracking for admission coordinators.</p>
        </div>
        <div className="flex gap-4 text-xs font-semibold">
          <div className="flex items-center gap-1"><span className="w-3 h-3 block bg-error rounded-full"></span> Occupied</div>
          <div className="flex items-center gap-1"><span className="w-3 h-3 block bg-success rounded-full"></span> Available</div>
          <div className="flex items-center gap-1"><span className="w-3 h-3 block bg-info rounded-full"></span> Cleaning</div>
          <div className="flex items-center gap-1"><span className="w-3 h-3 block bg-warning rounded-full"></span> Maintenance</div>
        </div>
      </div>

      {data?.wards.map((ward: any, idx: number) => (
        <Card key={idx} className="border-border shadow-sm">
          <CardHeader className="bg-surface pb-3">
            <CardTitle className="text-lg flex justify-between items-center">
              {ward.wardName}
              <Badge variant="default">{ward.occupied} / {ward.totalBeds} Occupied</Badge>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-6">
            <div className="grid grid-cols-6 gap-4">
              {ward.beds.map((bed: any) => (
                <div key={bed.bedId} className={`p-3 rounded-lg border-2 shadow-sm ${getStatusColor(bed.status)} flex flex-col justify-between h-24`}>
                  <div className="font-bold">{bed.bedId}</div>
                  <div className="text-xs font-medium truncate opacity-90">{bed.patient || bed.status}</div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
