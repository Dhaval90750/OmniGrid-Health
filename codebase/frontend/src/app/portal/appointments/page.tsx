"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";

export default function BookAppointment() {
  const router = useRouter();
  const [formData, setFormData] = useState({
    department: "",
    doctorId: "",
    date: "",
    time: "",
    visitType: "In-Person"
  });

  const handleChange = (e: React.ChangeEvent<HTMLSelectElement | HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    alert("Appointment successfully booked!");
    router.push("/portal");
  };

  return (
    <div className="max-w-3xl mx-auto py-8">
      <h1 className="text-3xl font-bold text-text-primary border-b border-border pb-4 mb-8">Book an Appointment</h1>
      
      <Card>
        <CardHeader>
          <CardTitle>Select Details</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <label className="block text-sm font-medium">Department</label>
                <select name="department" value={formData.department} onChange={handleChange} className="w-full p-2 border rounded-md" required>
                  <option value="">Select Department</option>
                  <option value="Cardiology">Cardiology</option>
                  <option value="Orthopedics">Orthopedics</option>
                  <option value="General Medicine">General Medicine</option>
                </select>
              </div>

              <div className="space-y-2">
                <label className="block text-sm font-medium">Doctor</label>
                <select name="doctorId" value={formData.doctorId} onChange={handleChange} className="w-full p-2 border rounded-md" required>
                  <option value="">Select Doctor</option>
                  <option value="doc-1">Dr. Sarah Miller</option>
                  <option value="doc-2">Dr. R. Iyer</option>
                </select>
              </div>

              <div className="space-y-2">
                <label className="block text-sm font-medium">Date</label>
                <input type="date" name="date" value={formData.date} onChange={handleChange} className="w-full p-2 border rounded-md" required />
              </div>

              <div className="space-y-2">
                <label className="block text-sm font-medium">Time Slot</label>
                <select name="time" value={formData.time} onChange={handleChange} className="w-full p-2 border rounded-md" required>
                  <option value="">Select Time</option>
                  <option value="10:00 AM">10:00 AM</option>
                  <option value="11:30 AM">11:30 AM</option>
                  <option value="02:00 PM">02:00 PM</option>
                </select>
              </div>
              
              <div className="space-y-2 col-span-2">
                <label className="block text-sm font-medium">Visit Type</label>
                <div className="flex gap-4">
                  <label className="flex items-center gap-2">
                    <input type="radio" name="visitType" value="In-Person" checked={formData.visitType === "In-Person"} onChange={handleChange} />
                    In-Person
                  </label>
                  <label className="flex items-center gap-2">
                    <input type="radio" name="visitType" value="Teleconsultation" checked={formData.visitType === "Teleconsultation"} onChange={handleChange} />
                    Teleconsultation (Video Call)
                  </label>
                </div>
              </div>
            </div>

            <div className="flex justify-end gap-4 pt-4 border-t border-border">
              <Button type="button" variant="secondary" onClick={() => router.back()}>Cancel</Button>
              <Button type="submit" variant="primary">Confirm Booking</Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
