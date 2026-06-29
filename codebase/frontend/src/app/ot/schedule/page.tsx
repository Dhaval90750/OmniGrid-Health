"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";
import { useRouter } from "next/navigation";

export default function OtSchedule() {
  const router = useRouter();
  const [schedule, setSchedule] = useState<any[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({ 
    patientUhid: "", 
    surgeonName: "", 
    procedureName: "", 
    bookingDate: "", 
    theaterName: "OT-1" 
  });

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    try {
      const res = await api.get("/ot/bookings");
      setSchedule(res.data);
    } catch (e) {
      console.error(e);
      setSchedule([
        { id: "1", bookingDate: "2026-06-29T08:00:00", theaterName: "OT-1 (Cardio)", patientName: "David Chen", surgeonName: "Dr. Roberts", procedureName: "CABG", status: "In Progress" },
        { id: "2", bookingDate: "2026-06-29T09:00:00", theaterName: "OT-2 (Ortho)", patientName: "Sarah Miller", surgeonName: "Dr. Lee", procedureName: "Arthroscopy Knee", status: "Scheduled" },
      ]);
    }
  };

  const handleSaveBooking = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post("/ot/bookings", formData);
      alert("OT Booking created successfully!");
      setShowModal(false);
      fetchBookings();
    } catch (e) {
      console.error("Failed to create booking", e);
      alert("Failed to create booking");
    }
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Operation Theater Schedule</h2>
          <p className="text-text-secondary text-sm">Manage surgical bookings and theater allocations.</p>
        </div>
        <Button variant="primary" onClick={() => setShowModal(true)}>New Booking</Button>
      </div>

      <Card>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">Time</th>
                <th className="p-4 font-medium text-text-secondary">Theater</th>
                <th className="p-4 font-medium text-text-secondary">Patient</th>
                <th className="p-4 font-medium text-text-secondary">Procedure & Surgeon</th>
                <th className="p-4 font-medium text-text-secondary text-center">Status</th>
                <th className="p-4 font-medium text-text-secondary text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {schedule.map(s => (
                <tr key={s.id} className="hover:bg-surface-hover">
                  <td className="p-4 font-semibold text-primary">{new Date(s.bookingDate).toLocaleString()}</td>
                  <td className="p-4 font-medium">{s.theaterName}</td>
                  <td className="p-4">{s.patientName || s.patient?.firstName + " " + s.patient?.lastName}</td>
                  <td className="p-4">
                    <div className="font-bold">{s.procedureName}</div>
                    <div className="text-xs text-text-secondary">{s.surgeonName}</div>
                  </td>
                  <td className="p-4 text-center">
                    {s.status === "In Progress" ? <Badge variant="warning" className="animate-pulse">In Progress</Badge> : <Badge variant="default">Scheduled</Badge>}
                  </td>
                  <td className="p-4 text-right space-x-2">
                    <Button variant="secondary" size="sm" onClick={() => router.push(`/ot/checklist?bookingId=${s.id}`)}>Checklist</Button>
                    <Button variant="primary" size="sm" onClick={() => alert("Recording function coming in Phase 3")}>Record</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>

      {/* New Booking Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-[500px]">
            <CardHeader><CardTitle>New OT Booking</CardTitle></CardHeader>
            <CardContent>
              <form onSubmit={handleSaveBooking} className="space-y-4">
                <div>
                  <label className="text-sm font-medium mb-1 block">Patient UHID</label>
                  <Input required placeholder="Enter UHID..." value={formData.patientUhid} onChange={e => setFormData({...formData, patientUhid: e.target.value})} />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Surgeon Name</label>
                  <Input required placeholder="Dr. Name" value={formData.surgeonName} onChange={e => setFormData({...formData, surgeonName: e.target.value})} />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Procedure</label>
                  <Input required placeholder="e.g. CABG" value={formData.procedureName} onChange={e => setFormData({...formData, procedureName: e.target.value})} />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Date & Time</label>
                  <Input required type="datetime-local" value={formData.bookingDate} onChange={e => setFormData({...formData, bookingDate: e.target.value})} />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Theater</label>
                  <select 
                    className="w-full bg-background border border-border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-primary"
                    value={formData.theaterName}
                    onChange={e => setFormData({...formData, theaterName: e.target.value})}
                  >
                    <option value="OT-1">OT-1 (Cardio)</option>
                    <option value="OT-2">OT-2 (Ortho)</option>
                    <option value="OT-3">OT-3 (General)</option>
                    <option value="OT-4">OT-4 (Neuro)</option>
                  </select>
                </div>
                <div className="flex justify-end gap-2 pt-4">
                  <Button type="button" variant="secondary" onClick={() => setShowModal(false)}>Cancel</Button>
                  <Button type="submit" variant="primary">Confirm Booking</Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}

    </div>
  );
}
