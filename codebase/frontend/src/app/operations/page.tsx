"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

export default function OperationsDashboard() {
  const [activeTab, setActiveTab] = useState("housekeeping");

  // Dummy data
  const housekeeping = [
    { id: "HK-101", zone: "Ward A", desc: "Terminal Cleaning Bed 12", priority: "Urgent", status: "Pending", assigned: "Ramesh" },
    { id: "HK-102", zone: "Lobby", desc: "Spill near Reception", priority: "Emergency", status: "In_Progress", assigned: "Sita" },
    { id: "HK-103", zone: "OT 1", desc: "Post-op sterilization", priority: "Routine", status: "Completed", assigned: "Manoj" }
  ];

  const workOrders = [
    { ticket: "WO-2026-001", cat: "HVAC", loc: "Room 102", desc: "AC not cooling", priority: "High", status: "Assigned", tech: "Electrician Ali" },
    { ticket: "WO-2026-002", cat: "Plumbing", loc: "ICU Restroom", desc: "Leaking faucet", priority: "Medium", status: "Open", tech: "Unassigned" }
  ];

  const transports = [
    { id: "TR-501", patient: "John Doe", from: "Ward B", to: "Radiology", reason: "CT Scan", status: "Requested", porter: "Unassigned" },
    { id: "TR-502", patient: "Jane Smith", from: "Emergency", to: "ICU", reason: "Admission", status: "In_Transit", porter: "Kiran" }
  ];

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Operations & Logistics</h1>
          <p className="text-text-secondary text-sm">Manage housekeeping, maintenance work orders, and patient transport</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-border">
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'housekeeping' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('housekeeping')}
        >
          Housekeeping Tasks
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'maintenance' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('maintenance')}
        >
          Maintenance Help Desk
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'transport' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('transport')}
        >
          Patient Transport
        </button>
      </div>

      {/* Content */}
      {activeTab === "housekeeping" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Housekeeping & Cleaning</CardTitle>
              <Button variant="secondary">Create Cleaning Task</Button>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Task ID</th>
                  <th className="p-3">Zone</th>
                  <th className="p-3">Description</th>
                  <th className="p-3">Priority</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Assigned Staff</th>
                  <th className="p-3">Action</th>
                </tr>
              </thead>
              <tbody>
                {housekeeping.map(task => (
                  <tr key={task.id} className="border-b border-surface-hover">
                    <td className="p-3 font-medium text-primary-dark">{task.id}</td>
                    <td className="p-3 font-bold">{task.zone}</td>
                    <td className="p-3">{task.desc}</td>
                    <td className="p-3">
                      {task.priority === "Emergency" ? <Badge variant="error">Emergency</Badge> : (task.priority === "Urgent" ? <Badge variant="warning">Urgent</Badge> : <Badge variant="info">Routine</Badge>)}
                    </td>
                    <td className="p-3">
                      {task.status === "Pending" ? <Badge variant="warning">Pending</Badge> : (task.status === "In_Progress" ? <Badge variant="info">In Progress</Badge> : <Badge variant="success">Completed</Badge>)}
                    </td>
                    <td className="p-3">{task.assigned}</td>
                    <td className="p-3"><Button variant="secondary" size="sm">Update</Button></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "maintenance" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Maintenance Work Orders</CardTitle>
              <Button variant="secondary">Raise Work Order</Button>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Ticket No</th>
                  <th className="p-3">Category</th>
                  <th className="p-3">Location</th>
                  <th className="p-3">Issue Description</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Technician</th>
                  <th className="p-3">Action</th>
                </tr>
              </thead>
              <tbody>
                {workOrders.map(wo => (
                  <tr key={wo.ticket} className="border-b border-surface-hover">
                    <td className="p-3 font-medium text-primary-dark">{wo.ticket}</td>
                    <td className="p-3"><Badge variant="info">{wo.cat}</Badge></td>
                    <td className="p-3 font-bold">{wo.loc}</td>
                    <td className="p-3">{wo.desc}</td>
                    <td className="p-3">
                      {wo.status === "Open" ? <Badge variant="error">Open</Badge> : <Badge variant="warning">Assigned</Badge>}
                    </td>
                    <td className="p-3">{wo.tech}</td>
                    <td className="p-3"><Button variant="secondary" size="sm">Manage</Button></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "transport" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Internal Patient Transport</CardTitle>
              <Button variant="primary">Request Transport</Button>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Req ID</th>
                  <th className="p-3">Patient</th>
                  <th className="p-3">From</th>
                  <th className="p-3">To</th>
                  <th className="p-3">Reason</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Porter</th>
                  <th className="p-3">Action</th>
                </tr>
              </thead>
              <tbody>
                {transports.map(tr => (
                  <tr key={tr.id} className="border-b border-surface-hover">
                    <td className="p-3 font-medium text-primary-dark">{tr.id}</td>
                    <td className="p-3 font-bold">{tr.patient}</td>
                    <td className="p-3 text-error">{tr.from}</td>
                    <td className="p-3 text-success">{tr.to}</td>
                    <td className="p-3">{tr.reason}</td>
                    <td className="p-3">
                      {tr.status === "Requested" ? <Badge variant="error">Requested</Badge> : <Badge variant="info">In Transit</Badge>}
                    </td>
                    <td className="p-3">{tr.porter}</td>
                    <td className="p-3"><Button variant="secondary" size="sm">Dispatch</Button></td>
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
