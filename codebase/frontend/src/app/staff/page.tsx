"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

import { api } from "@/lib/api";

export default function StaffDashboard() {
  const [activeTab, setActiveTab] = useState("directory");

  const [staff, setStaff] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/staff/profiles')
      .then(res => {
        setStaff(res.data);
        setLoading(false);
      })
      .catch(err => {
        console.error(err);
        setLoading(false);
      });
  }, []);

  const roster = [
    { date: "2026-06-21", shift: "Morning (08:00 - 16:00)", location: "ICU", staff: "Dr. Rahul Sharma (Cardiology)" },
    { date: "2026-06-21", shift: "On-Call (24 Hrs)", location: "Hospital Wide", staff: "Dr. Anjali Desai (Pulmonology)" },
    { date: "2026-06-21", shift: "Evening (16:00 - 00:00)", location: "Emergency", staff: "Dr. Amit Kumar (ER Resident)" }
  ];

  const leaves = [
    { reqId: "LR-801", staff: "Nurse Priya Patel", from: "2026-07-01", to: "2026-07-05", reason: "Family Vacation", status: "Pending" },
    { reqId: "LR-802", staff: "Dr. Anjali Desai", from: "2026-08-10", to: "2026-08-12", reason: "Medical Conference", status: "Approved" }
  ];

  const consults = [
    { reqId: "CC-301", patient: "John Doe", from: "Dr. Amit Kumar (ER)", toDept: "Cardiology", reason: "Abnormal ECG findings in ER.", status: "Requested" },
    { reqId: "CC-302", patient: "Jane Smith", from: "Dr. Rahul Sharma", toDept: "Nephrology", reason: "Elevated Creatinine levels.", status: "Completed" }
  ];

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Staff & Clinical Operations</h1>
          <p className="text-text-secondary text-sm">Manage staff directory, duty rosters, leaves, and cross-consultations</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-border">
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'directory' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('directory')}
        >
          Staff Directory
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'roster' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('roster')}
        >
          Duty Roster
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'leaves' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('leaves')}
        >
          Leave Management
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'consults' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('consults')}
        >
          Cross-Consultations
        </button>
      </div>

      {/* Content */}
      {activeTab === "directory" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Hospital Staff</CardTitle>
              <Button variant="secondary">Add Staff Member</Button>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Emp ID</th>
                  <th className="p-3">Name</th>
                  <th className="p-3">Role</th>
                  <th className="p-3">Department</th>
                  <th className="p-3">Contact</th>
                  <th className="p-3">Status</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr><td colSpan={6} className="p-4 text-center">Loading...</td></tr>
                ) : (
                  staff.map(s => (
                    <tr key={s.id} className="border-b border-surface-hover">
                      <td className="p-3 font-medium text-primary-dark">{s.employeeCode}</td>
                      <td className="p-3 font-bold">{s.fullName}</td>
                      <td className="p-3"><Badge variant="info">{s.role}</Badge></td>
                      <td className="p-3">{s.department}</td>
                      <td className="p-3">{s.contactNumber}</td>
                      <td className="p-3">
                        {s.isActive ? <Badge variant="success">Active</Badge> : <Badge variant="error">Inactive</Badge>}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "roster" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Daily Duty Roster & On-Call Schedule</CardTitle>
              <Button variant="secondary">Generate Roster</Button>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Date</th>
                  <th className="p-3">Location</th>
                  <th className="p-3">Shift Type</th>
                  <th className="p-3">Assigned Staff</th>
                </tr>
              </thead>
              <tbody>
                {roster.map((r, i) => (
                  <tr key={i} className="border-b border-surface-hover">
                    <td className="p-3 font-medium">{r.date}</td>
                    <td className="p-3 font-bold">{r.location}</td>
                    <td className="p-3">
                      {r.shift.includes("On-Call") ? <Badge variant="error">{r.shift}</Badge> : <Badge variant="info">{r.shift}</Badge>}
                    </td>
                    <td className="p-3">{r.staff}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "leaves" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Leave Requests</CardTitle>
              <Button variant="secondary">Apply for Leave</Button>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Req ID</th>
                  <th className="p-3">Staff Member</th>
                  <th className="p-3">Duration</th>
                  <th className="p-3">Reason</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Action</th>
                </tr>
              </thead>
              <tbody>
                {leaves.map(l => (
                  <tr key={l.reqId} className="border-b border-surface-hover">
                    <td className="p-3 font-medium">{l.reqId}</td>
                    <td className="p-3 font-bold">{l.staff}</td>
                    <td className="p-3">{l.from} to {l.to}</td>
                    <td className="p-3">{l.reason}</td>
                    <td className="p-3">
                      {l.status === "Pending" ? <Badge variant="warning">Pending Approval</Badge> : <Badge variant="success">Approved</Badge>}
                    </td>
                    <td className="p-3">
                      {l.status === "Pending" && <Button variant="secondary" size="sm">Review</Button>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "consults" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Cross-Departmental Consultations</CardTitle>
              <Button variant="primary">Request Consult</Button>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Req ID</th>
                  <th className="p-3">Patient</th>
                  <th className="p-3">Requesting Dr.</th>
                  <th className="p-3">Target Dept</th>
                  <th className="p-3">Clinical Reason</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Action</th>
                </tr>
              </thead>
              <tbody>
                {consults.map(c => (
                  <tr key={c.reqId} className="border-b border-surface-hover">
                    <td className="p-3 font-medium text-primary-dark">{c.reqId}</td>
                    <td className="p-3 font-bold">{c.patient}</td>
                    <td className="p-3">{c.from}</td>
                    <td className="p-3"><Badge variant="info">{c.toDept}</Badge></td>
                    <td className="p-3">{c.reason}</td>
                    <td className="p-3">
                      {c.status === "Requested" ? <Badge variant="warning">Requested</Badge> : <Badge variant="success">Completed</Badge>}
                    </td>
                    <td className="p-3">
                      {c.status === "Requested" ? <Button variant="secondary" size="sm">Accept Consult</Button> : <Button variant="secondary" size="sm">View Notes</Button>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

    </div>
  );
}
