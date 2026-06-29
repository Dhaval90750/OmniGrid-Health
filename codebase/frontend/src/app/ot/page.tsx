"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";

export default function OtDashboard() {
  const [activeTab, setActiveTab] = useState("schedule");
  const [bookings, setBookings] = useState<any[]>([]);
  const [selectedBooking, setSelectedBooking] = useState<any>(null);

  // New Booking State
  const [showBookingModal, setShowBookingModal] = useState(false);
  const [newBooking, setNewBooking] = useState({
    patientId: "",
    surgeonId: "1", // Mock default
    procedureName: "",
    otRoom: "OT-1",
    scheduledDate: "",
    estimatedDurationMinutes: 60
  });

  // Operative Note State
  const [opFindings, setOpFindings] = useState("");
  const [opProcedureDetails, setOpProcedureDetails] = useState("");
  const [bloodLoss, setBloodLoss] = useState("");
  const [implants, setImplants] = useState("");

  // WHO Checklist State
  const [checklist, setChecklist] = useState({
    signInCompleted: false,
    timeOutCompleted: false,
    signOutCompleted: false
  });

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    try {
      const res = await api.get("/ot/bookings");
      setBookings(res.data);
    } catch (e) {
      console.error(e);
      alert("Failed to load OT bookings.");
    }
  };

  const handleCreateBooking = async () => {
    try {
      await api.post("/ot/bookings", {
        patient: { id: newBooking.patientId || "00000000-0000-0000-0000-000000000000" }, // Real backend needs valid UUID
        surgeon: { id: newBooking.surgeonId },
        procedureName: newBooking.procedureName,
        otRoom: newBooking.otRoom,
        scheduledDate: new Date(newBooking.scheduledDate).toISOString(),
        estimatedDurationMinutes: newBooking.estimatedDurationMinutes
      });
      alert("OT Booking Created Successfully!");
      setShowBookingModal(false);
      fetchBookings();
    } catch (e) {
      console.error(e);
      alert("Error creating OT Booking. Ensure valid patient UUID in a real scenario.");
      setShowBookingModal(false);
    }
  };

  const handleSelectBooking = (booking: any, tab: string) => {
    setSelectedBooking(booking);
    setActiveTab(tab);
    setChecklist({ signInCompleted: false, timeOutCompleted: false, signOutCompleted: false });
    setOpFindings("");
    setOpProcedureDetails("");
    setBloodLoss("");
    setImplants("");
  };

  const handleSubmitOperativeNote = async () => {
    if (!selectedBooking) return;
    try {
      await api.post("/ot/records", {
        booking: { id: selectedBooking.id },
        primarySurgeon: { id: "1" },
        findings: opFindings,
        procedureDetails: opProcedureDetails,
        estimatedBloodLoss: bloodLoss ? parseFloat(bloodLoss) : 0,
        implantsUsed: implants,
        complications: "None",
        postOpInstructions: "Standard recovery protocols"
      });
      alert("Operative Note Signed and Published!");
      setSelectedBooking(null);
      setActiveTab("schedule");
    } catch (e) {
      console.error(e);
      alert("Error submitting Operative Note.");
      setSelectedBooking(null);
      setActiveTab("schedule");
    }
  };

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Operating Theater (OT) Management</h2>
          <p className="text-text-secondary text-sm">Schedule surgeries and manage operative documentation.</p>
        </div>
        <Button variant="primary" onClick={() => setShowBookingModal(true)}>+ New Surgery Booking</Button>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-border">
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'schedule' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => { setActiveTab('schedule'); setSelectedBooking(null); }}
        >
          OT Schedule Board
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'checklist' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => { if(selectedBooking) handleSelectBooking(selectedBooking, 'checklist'); else setActiveTab('checklist'); }}
        >
          WHO Safety Checklist
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'opnote' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => { if(selectedBooking) handleSelectBooking(selectedBooking, 'opnote'); else setActiveTab('opnote'); }}
        >
          Operative Notes
        </button>
      </div>

      {/* Content */}
      {activeTab === "schedule" && (
        <Card>
          <CardContent className="p-0">
            {bookings.length === 0 ? (
              <div className="text-center p-8 text-text-secondary text-sm italic">No surgeries scheduled.</div>
            ) : (
              <table className="w-full text-left text-sm border-collapse">
                <thead className="bg-surface border-b border-border">
                  <tr>
                    <th className="p-4">Time</th>
                    <th className="p-4">OT Room</th>
                    <th className="p-4">Patient</th>
                    <th className="p-4">Procedure</th>
                    <th className="p-4">Surgeon</th>
                    <th className="p-4">Status</th>
                    <th className="p-4 text-right">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {bookings.map(booking => (
                    <tr key={booking.id} className="hover:bg-surface-hover">
                      <td className="p-4 font-medium">{new Date(booking.scheduledDate).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</td>
                      <td className="p-4"><Badge variant="info">{booking.otRoom}</Badge></td>
                      <td className="p-4">{booking.patient?.firstName} {booking.patient?.lastName}</td>
                      <td className="p-4 font-bold text-primary">{booking.procedureName}</td>
                      <td className="p-4">Dr. {booking.surgeon?.firstName} {booking.surgeon?.lastName}</td>
                      <td className="p-4"><Badge>{booking.status}</Badge></td>
                      <td className="p-4 text-right flex justify-end gap-2">
                        <Button variant="secondary" size="sm" onClick={() => handleSelectBooking(booking, 'checklist')}>WHO Checklist</Button>
                        <Button variant="primary" size="sm" onClick={() => handleSelectBooking(booking, 'opnote')}>Write Op Note</Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </CardContent>
        </Card>
      )}

      {activeTab === "checklist" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>WHO Surgical Safety Checklist</CardTitle>
              {selectedBooking && <Badge variant="info">Patient: {selectedBooking.patient?.firstName} {selectedBooking.patient?.lastName} | {selectedBooking.procedureName}</Badge>}
            </div>
          </CardHeader>
          <CardContent>
            {selectedBooking ? (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {/* SIGN IN */}
                <div className={`border rounded-md p-4 ${checklist.signInCompleted ? 'border-success bg-success/5' : 'border-border bg-white'}`}>
                  <h3 className="font-bold text-lg mb-4 flex items-center gap-2">
                    <span className="bg-primary text-white w-6 h-6 rounded-full inline-flex items-center justify-center text-xs">1</span>
                    Sign In <span className="text-sm font-normal text-text-secondary">(Before Induction)</span>
                  </h3>
                  <div className="space-y-3 text-sm">
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Patient has confirmed identity, site, procedure, and consent</label>
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Site is marked</label>
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Anaesthesia safety check completed</label>
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Pulse oximeter on patient and functioning</label>
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Known allergy evaluated</label>
                  </div>
                  <Button 
                    variant={checklist.signInCompleted ? "secondary" : "primary"} 
                    className="w-full mt-6"
                    onClick={() => setChecklist({...checklist, signInCompleted: true})}
                  >
                    {checklist.signInCompleted ? "Completed" : "Complete Sign In"}
                  </Button>
                </div>

                {/* TIME OUT */}
                <div className={`border rounded-md p-4 ${checklist.timeOutCompleted ? 'border-success bg-success/5' : 'border-border bg-white'} ${!checklist.signInCompleted ? 'opacity-50 pointer-events-none' : ''}`}>
                  <h3 className="font-bold text-lg mb-4 flex items-center gap-2">
                    <span className="bg-primary text-white w-6 h-6 rounded-full inline-flex items-center justify-center text-xs">2</span>
                    Time Out <span className="text-sm font-normal text-text-secondary">(Before Skin Incision)</span>
                  </h3>
                  <div className="space-y-3 text-sm">
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> All team members introduced by name and role</label>
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Surgeon, Anaesthetist, and Nurse verbally confirm: Patient, Site, Procedure</label>
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Anticipated critical events discussed</label>
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Antibiotic prophylaxis given within last 60 mins</label>
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Essential imaging displayed</label>
                  </div>
                  <Button 
                    variant={checklist.timeOutCompleted ? "secondary" : "primary"} 
                    className="w-full mt-6"
                    onClick={() => setChecklist({...checklist, timeOutCompleted: true})}
                  >
                    {checklist.timeOutCompleted ? "Completed" : "Complete Time Out"}
                  </Button>
                </div>

                {/* SIGN OUT */}
                <div className={`border rounded-md p-4 ${checklist.signOutCompleted ? 'border-success bg-success/5' : 'border-border bg-white'} ${!checklist.timeOutCompleted ? 'opacity-50 pointer-events-none' : ''}`}>
                  <h3 className="font-bold text-lg mb-4 flex items-center gap-2">
                    <span className="bg-primary text-white w-6 h-6 rounded-full inline-flex items-center justify-center text-xs">3</span>
                    Sign Out <span className="text-sm font-normal text-text-secondary">(Before Patient Leaves OT)</span>
                  </h3>
                  <div className="space-y-3 text-sm">
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Nurse verbally confirms name of procedure</label>
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Instrument, sponge, and needle counts are correct</label>
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Specimen is labeled (including patient name)</label>
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Any equipment problems addressed</label>
                    <label className="flex items-start gap-2"><input type="checkbox" className="mt-1" /> Key concerns for recovery and management discussed</label>
                  </div>
                  <Button 
                    variant={checklist.signOutCompleted ? "secondary" : "primary"} 
                    className="w-full mt-6"
                    onClick={() => setChecklist({...checklist, signOutCompleted: true})}
                  >
                    {checklist.signOutCompleted ? "Completed" : "Complete Sign Out"}
                  </Button>
                </div>
              </div>
            ) : (
              <div className="py-12 text-center text-text-secondary">Please select a surgery from the Schedule Board.</div>
            )}
          </CardContent>
        </Card>
      )}

      {activeTab === "opnote" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Operative Note</CardTitle>
              {selectedBooking && <Badge variant="info">Patient: {selectedBooking.patient?.firstName} {selectedBooking.patient?.lastName} | {selectedBooking.procedureName}</Badge>}
            </div>
          </CardHeader>
          <CardContent>
            {selectedBooking ? (
              <div className="space-y-6">
                <div>
                  <label className="font-bold text-sm block mb-1">Pre-Operative Findings</label>
                  <textarea 
                    className="w-full h-24 p-3 border border-border rounded-md text-sm focus:border-primary outline-none resize-none" 
                    placeholder="Describe findings..."
                    value={opFindings}
                    onChange={e => setOpFindings(e.target.value)}
                  ></textarea>
                </div>

                <div>
                  <label className="font-bold text-sm block mb-1">Procedure Details</label>
                  <textarea 
                    className="w-full h-40 p-3 border border-border rounded-md text-sm focus:border-primary outline-none resize-none" 
                    placeholder="Step-by-step procedure notes..."
                    value={opProcedureDetails}
                    onChange={e => setOpProcedureDetails(e.target.value)}
                  ></textarea>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="font-bold text-sm block mb-1">Estimated Blood Loss (ml)</label>
                    <Input type="number" placeholder="e.g. 50" value={bloodLoss} onChange={e => setBloodLoss(e.target.value)} />
                  </div>
                  <div>
                    <label className="font-bold text-sm block mb-1">Implants Used (if any)</label>
                    <Input placeholder="Describe implants or 'None'" value={implants} onChange={e => setImplants(e.target.value)} />
                  </div>
                </div>

                <div className="flex justify-end gap-4 pt-4 border-t border-border">
                   <Button variant="secondary" onClick={() => setActiveTab('schedule')}>Cancel</Button>
                   <Button variant="primary" onClick={handleSubmitOperativeNote} disabled={!opProcedureDetails}>Sign & Publish Operative Note</Button>
                </div>
              </div>
            ) : (
              <div className="py-12 text-center text-text-secondary">Please select a surgery from the Schedule Board.</div>
            )}
          </CardContent>
        </Card>
      )}

      {/* New Booking Modal */}
      {showBookingModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader>
              <CardTitle>Book OT Slot</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <label className="text-sm font-medium mb-1 block">Patient ID (UUID or &apos;mock&apos;)</label>
                <Input value={newBooking.patientId} onChange={e => setNewBooking({...newBooking, patientId: e.target.value})} placeholder="e.g. 00000000-0000-0000-0000-000000000000" />
              </div>
              <div>
                <label className="text-sm font-medium mb-1 block">Procedure Name</label>
                <Input value={newBooking.procedureName} onChange={e => setNewBooking({...newBooking, procedureName: e.target.value})} placeholder="e.g. Laparoscopic Appendectomy" />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium mb-1 block">OT Room</label>
                  <select className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm bg-white" value={newBooking.otRoom} onChange={e => setNewBooking({...newBooking, otRoom: e.target.value})}>
                    <option>OT-1</option>
                    <option>OT-2</option>
                    <option>OT-3 (Minor)</option>
                  </select>
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Duration (min)</label>
                  <Input type="number" value={newBooking.estimatedDurationMinutes} onChange={e => setNewBooking({...newBooking, estimatedDurationMinutes: parseInt(e.target.value)})} />
                </div>
              </div>
              <div>
                <label className="text-sm font-medium mb-1 block">Scheduled Time</label>
                <Input type="datetime-local" value={newBooking.scheduledDate} onChange={e => setNewBooking({...newBooking, scheduledDate: e.target.value})} />
              </div>

              <div className="flex justify-end gap-2 pt-4 border-t border-border mt-4">
                <Button variant="secondary" onClick={() => setShowBookingModal(false)}>Cancel</Button>
                <Button variant="primary" onClick={handleCreateBooking} disabled={!newBooking.procedureName || !newBooking.scheduledDate}>Book Slot</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

    </div>
  );
}
