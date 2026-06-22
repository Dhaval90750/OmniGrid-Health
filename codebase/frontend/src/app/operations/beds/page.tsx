"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function BedManagement() {
  const [beds, setBeds] = useState<any[]>([]);

  const mockBeds = [
    { id: "b1", ward: "General Ward A", bedNumber: "A-01", status: "AVAILABLE" },
    { id: "b2", ward: "General Ward A", bedNumber: "A-02", status: "OCCUPIED", patient: "Jane Smith" },
    { id: "b3", ward: "General Ward A", bedNumber: "A-03", status: "CLEANING" },
    { id: "b4", ward: "General Ward A", bedNumber: "A-04", status: "AVAILABLE" },
    { id: "b5", ward: "ICU", bedNumber: "ICU-01", status: "OCCUPIED", patient: "Robert Johnson" },
    { id: "b6", ward: "ICU", bedNumber: "ICU-02", status: "CLEANING" }
  ];

  useEffect(() => {
    // In a real implementation, fetch from backend
    // fetchBeds();
    setBeds(mockBeds);
  }, []);

  const markClean = async (bedId: string) => {
    try {
      // await api.put(`/admissions/beds/${bedId}/clean`);
      setBeds(beds.map(b => b.id === bedId ? { ...b, status: "AVAILABLE" } : b));
      alert("Bed marked as Clean & Available.");
    } catch (e) {
      console.error(e);
      alert("Failed to update bed status.");
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case "AVAILABLE": return "bg-success/20 border-success/50 text-success-dark";
      case "OCCUPIED": return "bg-error/20 border-error/50 text-error-dark";
      case "CLEANING": return "bg-warning/20 border-warning/50 text-warning-dark";
      default: return "bg-surface border-border";
    }
  };

  // Group beds by ward
  const bedsByWard = beds.reduce((acc, bed) => {
    if (!acc[bed.ward]) acc[bed.ward] = [];
    acc[bed.ward].push(bed);
    return acc;
  }, {});

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Bed Management (ADT)</h2>
          <p className="text-text-secondary text-sm">Real-time bed tracking and housekeeping status.</p>
        </div>
        <div className="flex gap-4 items-center bg-surface px-4 py-2 rounded-md border border-border">
          <div className="flex items-center gap-2"><div className="w-3 h-3 rounded-full bg-success"></div> <span className="text-sm">Available</span></div>
          <div className="flex items-center gap-2"><div className="w-3 h-3 rounded-full bg-error"></div> <span className="text-sm">Occupied</span></div>
          <div className="flex items-center gap-2"><div className="w-3 h-3 rounded-full bg-warning"></div> <span className="text-sm">Cleaning Required</span></div>
        </div>
      </div>

      <div className="space-y-8">
        {Object.entries(bedsByWard).map(([ward, wardBeds]: [string, any]) => (
          <div key={ward} className="space-y-4">
            <h3 className="text-lg font-medium border-b border-border pb-2">{ward}</h3>
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
              {wardBeds.map((bed: any) => (
                <div 
                  key={bed.id} 
                  className={`p-4 rounded-lg border-2 flex flex-col justify-between h-32 transition-transform hover:scale-105 ${getStatusColor(bed.status)}`}
                >
                  <div className="flex justify-between items-start">
                    <span className="font-bold text-lg">{bed.bedNumber}</span>
                  </div>
                  
                  <div className="text-sm">
                    {bed.status === "OCCUPIED" && <div className="font-medium truncate" title={bed.patient}>{bed.patient}</div>}
                    {bed.status === "AVAILABLE" && <div className="opacity-70">Ready for Admission</div>}
                  </div>

                  {bed.status === "CLEANING" && (
                    <Button 
                      size="sm" 
                      className="w-full bg-warning hover:bg-warning-dark text-white text-xs mt-2"
                      onClick={() => markClean(bed.id)}
                    >
                      Mark Cleaned
                    </Button>
                  )}
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
