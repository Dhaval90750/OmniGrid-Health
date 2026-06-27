"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";

export default function StaffDashboard() {
  const [activeTab, setActiveTab] = useState("directory");

  // States
  const [staff, setStaff] = useState<any[]>([]);
  const [rosters, setRosters] = useState<any[]>([]);
  const [leaves, setLeaves] = useState<any[]>([]);
  const [consults, setConsults] = useState<any[]>([]);

  // Modals
  const [showStaffModal, setShowStaffModal] = useState(false);
  const [showRosterModal, setShowRosterModal] = useState(false);
  const [showLeaveModal, setShowLeaveModal] = useState(false);
  const [showConsultModal, setShowConsultModal] = useState(false);

  // Forms
  const [newStaff, setNewStaff] = useState({ firstName: "", lastName: "", role: "Resident", department: "General Medicine", phone: "", email: "" });
  const [newRoster, setNewRoster] = useState({ staffProfile: { id: "" }, date: "", shiftType: "MORNING", department: "General Medicine", role: "Resident" });
  const [newLeave, setNewLeave] = useState({ staffProfile: { id: "" }, leaveType: "SICK_LEAVE", startDate: "", endDate: "", reason: "" });
  const [newConsult, setNewConsult] = useState({ requestingDoctor: { id: "" }, consultingDoctor: { id: "" }, patient: { id: "" }, reason: "", priority: "NORMAL" });

  useEffect(() => {
    fetchData();
  }, [activeTab]);

  const fetchData = async () => {
    try {
      if (activeTab === "directory") {
        const res = await api.get("/staff/profiles");
        setStaff(res.data);
      } else if (activeTab === "rosters") {
        const res = await api.get("/staff/rosters");
        setRosters(res.data);
      } else if (activeTab === "leaves") {
        const res = await api.get("/staff/leaves");
        setLeaves(res.data);
      } else if (activeTab === "consults") {
        const res = await api.get("/staff/consults");
        setConsults(res.data);
      }
    } catch (e) {
      console.error(e);
    }
  };

  const handleCreateStaff = async () => {
    try {
      await api.post("/staff/profiles", newStaff);
      setShowStaffModal(false);
      fetchData();
    } catch (e) {
      alert("Failed to create Staff Profile.");
    }
  };

  const handleCreateRoster = async () => {
    try {
      await api.post("/staff/rosters", newRoster);
      setShowRosterModal(false);
      fetchData();
    } catch (e) {
      alert("Failed to create Roster.");
    }
  };

  const handleCreateLeave = async () => {
    try {
      await api.post("/staff/leaves", newLeave);
      setShowLeaveModal(false);
      fetchData();
    } catch (e) {
      alert("Failed to request leave.");
    }
  };

  const handleCreateConsult = async () => {
    try {
      await api.post("/staff/consults", newConsult);
      setShowConsultModal(false);
      fetchData();
    } catch (e) {
      alert("Failed to request consult.");
    }
  };

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">HR & Workforce Command Center</h2>
          <p className="text-text-secondary text-sm">Manage staff directory, duty rosters, leaves, and cross consultations.</p>
        </div>
        <div className="flex gap-2">
          {activeTab === 'directory' && <Button onClick={() => setShowStaffModal(true)}>+ Onboard Staff</Button>}
          {activeTab === 'rosters' && <Button onClick={() => setShowRosterModal(true)}>+ Assign Shift</Button>}
          {activeTab === 'leaves' && <Button onClick={() => setShowLeaveModal(true)}>+ Request Leave</Button>}
          {activeTab === 'consults' && <Button onClick={() => setShowConsultModal(true)}>+ Req. Consult</Button>}
        </div>
      </div>

      <div className="flex gap-4 border-b border-border overflow-x-auto">
        <button className={`pb-2 px-1 text-sm font-medium border-b-2 whitespace-nowrap ${activeTab === 'directory' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`} onClick={() => setActiveTab('directory')}>Staff Directory</button>
        <button className={`pb-2 px-1 text-sm font-medium border-b-2 whitespace-nowrap ${activeTab === 'rosters' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`} onClick={() => setActiveTab('rosters')}>Duty Rosters</button>
        <button className={`pb-2 px-1 text-sm font-medium border-b-2 whitespace-nowrap ${activeTab === 'leaves' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`} onClick={() => setActiveTab('leaves')}>Leave Management</button>
        <button className={`pb-2 px-1 text-sm font-medium border-b-2 whitespace-nowrap ${activeTab === 'consults' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`} onClick={() => setActiveTab('consults')}>Cross Consultations</button>
      </div>

      {activeTab === 'directory' && (
        <Card>
          <CardContent className="p-0">
            {staff.length === 0 ? <div className="p-8 text-center text-text-secondary">No staff members found.</div> : (
              <table className="w-full text-left text-sm border-collapse">
                <thead className="bg-surface border-b border-border">
                  <tr><th className="p-4">ID</th><th className="p-4">Name</th><th className="p-4">Role</th><th className="p-4">Department</th><th className="p-4">Contact</th></tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {staff.map(s => (
                    <tr key={s.id} className="hover:bg-surface-hover">
                      <td className="p-4 text-xs font-mono">{s.id.substring(0,8)}</td>
                      <td className="p-4 font-bold">{s.firstName} {s.lastName}</td>
                      <td className="p-4"><Badge variant="info">{s.role}</Badge></td>
                      <td className="p-4">{s.department}</td>
                      <td className="p-4 text-xs">{s.phone} | {s.email}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </CardContent>
        </Card>
      )}

      {activeTab === 'rosters' && (
        <Card>
          <CardContent className="p-0">
            {rosters.length === 0 ? <div className="p-8 text-center text-text-secondary">No roster assignments.</div> : (
              <table className="w-full text-left text-sm border-collapse">
                <thead className="bg-surface border-b border-border">
                  <tr><th className="p-4">Date</th><th className="p-4">Staff</th><th className="p-4">Department</th><th className="p-4">Shift Type</th></tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {rosters.map(r => (
                    <tr key={r.id} className="hover:bg-surface-hover">
                      <td className="p-4 font-bold">{r.date}</td>
                      <td className="p-4">{r.staffProfile?.firstName} {r.staffProfile?.lastName}</td>
                      <td className="p-4">{r.department}</td>
                      <td className="p-4"><Badge>{r.shiftType}</Badge></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </CardContent>
        </Card>
      )}

      {activeTab === 'leaves' && (
        <Card>
          <CardContent className="p-0">
            {leaves.length === 0 ? <div className="p-8 text-center text-text-secondary">No leave requests.</div> : (
              <table className="w-full text-left text-sm border-collapse">
                <thead className="bg-surface border-b border-border">
                  <tr><th className="p-4">Staff</th><th className="p-4">Type</th><th className="p-4">Dates</th><th className="p-4">Status</th></tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {leaves.map(l => (
                    <tr key={l.id} className="hover:bg-surface-hover">
                      <td className="p-4 font-bold">{l.staffProfile?.firstName} {l.staffProfile?.lastName}</td>
                      <td className="p-4">{l.leaveType}</td>
                      <td className="p-4 text-xs">{l.startDate} to {l.endDate}</td>
                      <td className="p-4">
                        <Badge variant={l.status === 'APPROVED' ? 'success' : (l.status === 'PENDING' ? 'warning' : 'error')}>{l.status}</Badge>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </CardContent>
        </Card>
      )}

      {activeTab === 'consults' && (
        <Card>
          <CardContent className="p-0">
            {consults.length === 0 ? <div className="p-8 text-center text-text-secondary">No cross consultations.</div> : (
              <table className="w-full text-left text-sm border-collapse">
                <thead className="bg-surface border-b border-border">
                  <tr><th className="p-4">Patient</th><th className="p-4">Requesting Dr.</th><th className="p-4">Consulting Dr.</th><th className="p-4">Priority</th><th className="p-4">Status</th></tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {consults.map(c => (
                    <tr key={c.id} className="hover:bg-surface-hover">
                      <td className="p-4 font-bold">{c.patient?.firstName} {c.patient?.lastName}</td>
                      <td className="p-4 text-xs">{c.requestingDoctor?.firstName} {c.requestingDoctor?.lastName}</td>
                      <td className="p-4 text-xs font-bold text-primary">{c.consultingDoctor?.firstName} {c.consultingDoctor?.lastName}</td>
                      <td className="p-4">{c.priority === 'URGENT' || c.priority === 'STAT' ? <Badge variant="error">{c.priority}</Badge> : <Badge>{c.priority}</Badge>}</td>
                      <td className="p-4"><Badge>{c.status}</Badge></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </CardContent>
        </Card>
      )}

      {/* Staff Modal */}
      {showStaffModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader><CardTitle>Onboard New Staff</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <Input placeholder="First Name" value={newStaff.firstName} onChange={e => setNewStaff({...newStaff, firstName: e.target.value})} />
                <Input placeholder="Last Name" value={newStaff.lastName} onChange={e => setNewStaff({...newStaff, lastName: e.target.value})} />
              </div>
              <select className="w-full h-10 px-3 border border-border rounded-md text-sm bg-white" value={newStaff.role} onChange={e => setNewStaff({...newStaff, role: e.target.value})}>
                <option value="Consultant">Consultant</option>
                <option value="Resident">Resident</option>
                <option value="Nurse">Nurse</option>
                <option value="Technician">Technician</option>
                <option value="Admin">Admin</option>
              </select>
              <Input placeholder="Department (e.g. Cardiology)" value={newStaff.department} onChange={e => setNewStaff({...newStaff, department: e.target.value})} />
              <Input placeholder="Phone" value={newStaff.phone} onChange={e => setNewStaff({...newStaff, phone: e.target.value})} />
              <Input type="email" placeholder="Email" value={newStaff.email} onChange={e => setNewStaff({...newStaff, email: e.target.value})} />
              <div className="flex justify-end gap-2 pt-4 border-t border-border mt-4">
                <Button variant="secondary" onClick={() => setShowStaffModal(false)}>Cancel</Button>
                <Button variant="primary" onClick={handleCreateStaff}>Save Profile</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Roster Modal */}
      {showRosterModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader><CardTitle>Assign Shift</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <Input placeholder="Staff Profile UUID" value={newRoster.staffProfile.id} onChange={e => setNewRoster({...newRoster, staffProfile: { id: e.target.value }})} />
              <Input type="date" value={newRoster.date} onChange={e => setNewRoster({...newRoster, date: e.target.value})} />
              <select className="w-full h-10 px-3 border border-border rounded-md text-sm bg-white" value={newRoster.shiftType} onChange={e => setNewRoster({...newRoster, shiftType: e.target.value})}>
                <option value="MORNING">Morning (08:00 - 16:00)</option>
                <option value="EVENING">Evening (16:00 - 00:00)</option>
                <option value="NIGHT">Night (00:00 - 08:00)</option>
              </select>
              <Input placeholder="Department" value={newRoster.department} onChange={e => setNewRoster({...newRoster, department: e.target.value})} />
              <div className="flex justify-end gap-2 pt-4 border-t border-border mt-4">
                <Button variant="secondary" onClick={() => setShowRosterModal(false)}>Cancel</Button>
                <Button variant="primary" onClick={handleCreateRoster}>Assign Shift</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Leave Modal */}
      {showLeaveModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader><CardTitle>Request Leave</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <Input placeholder="Staff Profile UUID" value={newLeave.staffProfile.id} onChange={e => setNewLeave({...newLeave, staffProfile: { id: e.target.value }})} />
              <select className="w-full h-10 px-3 border border-border rounded-md text-sm bg-white" value={newLeave.leaveType} onChange={e => setNewLeave({...newLeave, leaveType: e.target.value})}>
                <option value="SICK_LEAVE">Sick Leave</option>
                <option value="CASUAL_LEAVE">Casual Leave</option>
                <option value="MATERNITY_LEAVE">Maternity Leave</option>
                <option value="UNPAID_LEAVE">Unpaid Leave</option>
              </select>
              <div className="grid grid-cols-2 gap-4">
                <Input type="date" value={newLeave.startDate} onChange={e => setNewLeave({...newLeave, startDate: e.target.value})} />
                <Input type="date" value={newLeave.endDate} onChange={e => setNewLeave({...newLeave, endDate: e.target.value})} />
              </div>
              <textarea className="w-full h-24 p-3 border border-border rounded-md text-sm focus:border-primary outline-none resize-none" placeholder="Reason for leave..." value={newLeave.reason} onChange={e => setNewLeave({...newLeave, reason: e.target.value})} />
              <div className="flex justify-end gap-2 pt-4 border-t border-border mt-4">
                <Button variant="secondary" onClick={() => setShowLeaveModal(false)}>Cancel</Button>
                <Button variant="primary" onClick={handleCreateLeave}>Submit Request</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Consult Modal */}
      {showConsultModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader><CardTitle>Request Cross Consultation</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <Input placeholder="Patient UUID" value={newConsult.patient.id} onChange={e => setNewConsult({...newConsult, patient: { id: e.target.value }})} />
              <div className="grid grid-cols-2 gap-4">
                <Input placeholder="Req. Dr. UUID" value={newConsult.requestingDoctor.id} onChange={e => setNewConsult({...newConsult, requestingDoctor: { id: e.target.value }})} />
                <Input placeholder="Consult Dr. UUID" value={newConsult.consultingDoctor.id} onChange={e => setNewConsult({...newConsult, consultingDoctor: { id: e.target.value }})} />
              </div>
              <select className="w-full h-10 px-3 border border-border rounded-md text-sm bg-white" value={newConsult.priority} onChange={e => setNewConsult({...newConsult, priority: e.target.value})}>
                <option value="NORMAL">Normal</option>
                <option value="URGENT">Urgent</option>
                <option value="STAT">STAT</option>
              </select>
              <textarea className="w-full h-24 p-3 border border-border rounded-md text-sm focus:border-primary outline-none resize-none" placeholder="Clinical reason for consult..." value={newConsult.reason} onChange={e => setNewConsult({...newConsult, reason: e.target.value})} />
              <div className="flex justify-end gap-2 pt-4 border-t border-border mt-4">
                <Button variant="secondary" onClick={() => setShowConsultModal(false)}>Cancel</Button>
                <Button variant="primary" onClick={handleCreateConsult}>Request Consult</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

    </div>
  );
}
