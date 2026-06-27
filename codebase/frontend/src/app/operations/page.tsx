"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";

export default function OperationsDashboard() {
  const [activeTab, setActiveTab] = useState("housekeeping");

  // States
  const [housekeepingTasks, setHousekeepingTasks] = useState<any[]>([]);
  const [workOrders, setWorkOrders] = useState<any[]>([]);
  const [transportRequests, setTransportRequests] = useState<any[]>([]);
  const [incidents, setIncidents] = useState<any[]>([]);

  // Modals
  const [showHKModal, setShowHKModal] = useState(false);
  const [showWOModal, setShowWOModal] = useState(false);
  const [showTransportModal, setShowTransportModal] = useState(false);
  const [showIncidentModal, setShowIncidentModal] = useState(false);

  // Forms
  const [newHK, setNewHK] = useState({ location: "", taskType: "ROUTINE_CLEANING", priority: "NORMAL", notes: "" });
  const [newWO, setNewWO] = useState({ equipmentId: "", equipmentName: "", location: "", issueDescription: "", priority: "NORMAL", reportedBy: { id: "1" } });
  const [newTransport, setNewTransport] = useState({ patient: { id: "00000000-0000-0000-0000-000000000000" }, fromLocation: "", toLocation: "", priority: "NORMAL", transportType: "WHEELCHAIR", notes: "" });
  const [newIncident, setNewIncident] = useState({ incidentType: "PATIENT_FALL", severity: "LOW", location: "", description: "", reportedBy: { id: "1" }, patientId: "" });

  useEffect(() => {
    fetchData();
  }, [activeTab]);

  const fetchData = async () => {
    try {
      if (activeTab === "housekeeping") {
        const res = await api.get("/operations/housekeeping");
        setHousekeepingTasks(res.data);
      } else if (activeTab === "work-orders") {
        const res = await api.get("/operations/work-orders");
        setWorkOrders(res.data);
      } else if (activeTab === "transport") {
        const res = await api.get("/operations/transport");
        setTransportRequests(res.data);
      } else if (activeTab === "incidents") {
        const res = await api.get("/operations/incidents");
        setIncidents(res.data);
      }
    } catch (e) {
      console.error(e);
    }
  };

  const handleCreateHK = async () => {
    try {
      await api.post("/operations/housekeeping", newHK);
      setShowHKModal(false);
      fetchData();
    } catch (e) {
      alert("Failed to create Housekeeping Task.");
    }
  };

  const handleCreateWO = async () => {
    try {
      await api.post("/operations/work-orders", newWO);
      setShowWOModal(false);
      fetchData();
    } catch (e) {
      alert("Failed to create Work Order.");
    }
  };

  const handleCreateTransport = async () => {
    try {
      await api.post("/operations/transport", newTransport);
      setShowTransportModal(false);
      fetchData();
    } catch (e) {
      alert("Failed to create Transport Request. Check UUID.");
    }
  };

  const handleCreateIncident = async () => {
    try {
      await api.post("/operations/incidents", {
        ...newIncident,
        patient: newIncident.patientId ? { id: newIncident.patientId } : null
      });
      setShowIncidentModal(false);
      fetchData();
    } catch (e) {
      alert("Failed to report Incident.");
    }
  };

  const getPriorityBadge = (prio: string) => {
    switch (prio) {
      case "URGENT":
      case "CRITICAL":
      case "STAT":
        return <Badge variant="error">{prio}</Badge>;
      case "HIGH": return <Badge variant="warning">{prio}</Badge>;
      default: return <Badge variant="info">{prio}</Badge>;
    }
  };

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Facilities & Operations Management</h2>
          <p className="text-text-secondary text-sm">Manage housekeeping, maintenance, transport, and incident reports.</p>
        </div>
        <div className="flex gap-2">
          {activeTab === 'housekeeping' && <Button onClick={() => setShowHKModal(true)}>+ New Task</Button>}
          {activeTab === 'work-orders' && <Button onClick={() => setShowWOModal(true)}>+ Report Issue</Button>}
          {activeTab === 'transport' && <Button onClick={() => setShowTransportModal(true)}>+ Req. Transport</Button>}
          {activeTab === 'incidents' && <Button variant="error" onClick={() => setShowIncidentModal(true)}>+ Report Incident</Button>}
        </div>
      </div>

      <div className="flex gap-4 border-b border-border">
        <button className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'housekeeping' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`} onClick={() => setActiveTab('housekeeping')}>Housekeeping</button>
        <button className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'work-orders' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`} onClick={() => setActiveTab('work-orders')}>Biomedical & Maintenance</button>
        <button className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'transport' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`} onClick={() => setActiveTab('transport')}>Patient Transport</button>
        <button className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'incidents' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`} onClick={() => setActiveTab('incidents')}>Incident Reporting</button>
      </div>

      {activeTab === 'housekeeping' && (
        <Card>
          <CardContent className="p-0">
            {housekeepingTasks.length === 0 ? <div className="p-8 text-center text-text-secondary">No housekeeping tasks pending.</div> : (
              <table className="w-full text-left text-sm">
                <thead className="bg-surface border-b border-border">
                  <tr><th className="p-4">Location</th><th className="p-4">Type</th><th className="p-4">Priority</th><th className="p-4">Status</th><th className="p-4">Created</th></tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {housekeepingTasks.map(t => (
                    <tr key={t.id} className="hover:bg-surface-hover">
                      <td className="p-4 font-bold">{t.location}</td>
                      <td className="p-4">{t.taskType}</td>
                      <td className="p-4">{getPriorityBadge(t.priority)}</td>
                      <td className="p-4"><Badge>{t.status}</Badge></td>
                      <td className="p-4">{new Date(t.createdAt).toLocaleString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </CardContent>
        </Card>
      )}

      {activeTab === 'work-orders' && (
        <Card>
          <CardContent className="p-0">
            {workOrders.length === 0 ? <div className="p-8 text-center text-text-secondary">No active work orders.</div> : (
              <table className="w-full text-left text-sm">
                <thead className="bg-surface border-b border-border">
                  <tr><th className="p-4">ID</th><th className="p-4">Equipment</th><th className="p-4">Location</th><th className="p-4">Issue</th><th className="p-4">Priority</th><th className="p-4">Status</th></tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {workOrders.map(t => (
                    <tr key={t.id} className="hover:bg-surface-hover">
                      <td className="p-4 text-xs font-mono">{t.id.substring(0,8)}</td>
                      <td className="p-4 font-bold">{t.equipmentName}</td>
                      <td className="p-4">{t.location}</td>
                      <td className="p-4 text-xs text-text-secondary">{t.issueDescription}</td>
                      <td className="p-4">{getPriorityBadge(t.priority)}</td>
                      <td className="p-4"><Badge>{t.status}</Badge></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </CardContent>
        </Card>
      )}

      {activeTab === 'transport' && (
        <Card>
          <CardContent className="p-0">
            {transportRequests.length === 0 ? <div className="p-8 text-center text-text-secondary">No transport requests.</div> : (
              <table className="w-full text-left text-sm">
                <thead className="bg-surface border-b border-border">
                  <tr><th className="p-4">Patient</th><th className="p-4">From → To</th><th className="p-4">Type</th><th className="p-4">Priority</th><th className="p-4">Status</th></tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {transportRequests.map(t => (
                    <tr key={t.id} className="hover:bg-surface-hover">
                      <td className="p-4 font-bold">{t.patient?.firstName} {t.patient?.lastName}</td>
                      <td className="p-4">{t.fromLocation} → {t.toLocation}</td>
                      <td className="p-4">{t.transportType}</td>
                      <td className="p-4">{getPriorityBadge(t.priority)}</td>
                      <td className="p-4"><Badge>{t.status}</Badge></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </CardContent>
        </Card>
      )}

      {activeTab === 'incidents' && (
        <Card>
          <CardContent className="p-0">
            {incidents.length === 0 ? <div className="p-8 text-center text-text-secondary">No incidents reported.</div> : (
              <table className="w-full text-left text-sm">
                <thead className="bg-surface border-b border-border">
                  <tr><th className="p-4">Date</th><th className="p-4">Type</th><th className="p-4">Severity</th><th className="p-4">Location</th><th className="p-4">Description</th></tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {incidents.map(t => (
                    <tr key={t.id} className="hover:bg-surface-hover">
                      <td className="p-4 text-xs">{new Date(t.createdAt).toLocaleString()}</td>
                      <td className="p-4 font-bold">{t.incidentType}</td>
                      <td className="p-4">{t.severity === 'HIGH' || t.severity === 'CRITICAL' ? <Badge variant="error">{t.severity}</Badge> : <Badge>{t.severity}</Badge>}</td>
                      <td className="p-4">{t.location}</td>
                      <td className="p-4 text-xs text-text-secondary truncate max-w-[200px]">{t.description}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </CardContent>
        </Card>
      )}

      {/* Housekeeping Modal */}
      {showHKModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader><CardTitle>New Housekeeping Task</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <Input placeholder="Location (e.g. GM-05, OT-1)" value={newHK.location} onChange={e => setNewHK({...newHK, location: e.target.value})} />
              <select className="w-full h-10 px-3 border border-border rounded-md text-sm bg-white" value={newHK.taskType} onChange={e => setNewHK({...newHK, taskType: e.target.value})}>
                <option value="ROUTINE_CLEANING">Routine Cleaning</option>
                <option value="DISCHARGE_CLEANING">Discharge Cleaning (Terminal)</option>
                <option value="SPILL_CLEANUP">Biohazard Spill</option>
              </select>
              <select className="w-full h-10 px-3 border border-border rounded-md text-sm bg-white" value={newHK.priority} onChange={e => setNewHK({...newHK, priority: e.target.value})}>
                <option value="NORMAL">Normal</option>
                <option value="HIGH">High</option>
                <option value="URGENT">Urgent / STAT</option>
              </select>
              <Input placeholder="Additional Notes" value={newHK.notes} onChange={e => setNewHK({...newHK, notes: e.target.value})} />
              <div className="flex justify-end gap-2 pt-4 border-t border-border mt-4">
                <Button variant="secondary" onClick={() => setShowHKModal(false)}>Cancel</Button>
                <Button variant="primary" onClick={handleCreateHK}>Save Task</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Work Order Modal */}
      {showWOModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader><CardTitle>Report Equipment Issue</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <Input placeholder="Equipment Name (e.g. ECG Machine, MRI)" value={newWO.equipmentName} onChange={e => setNewWO({...newWO, equipmentName: e.target.value})} />
              <Input placeholder="Location" value={newWO.location} onChange={e => setNewWO({...newWO, location: e.target.value})} />
              <textarea className="w-full h-24 p-3 border border-border rounded-md text-sm focus:border-primary outline-none resize-none" placeholder="Describe the issue..." value={newWO.issueDescription} onChange={e => setNewWO({...newWO, issueDescription: e.target.value})} />
              <select className="w-full h-10 px-3 border border-border rounded-md text-sm bg-white" value={newWO.priority} onChange={e => setNewWO({...newWO, priority: e.target.value})}>
                <option value="NORMAL">Normal</option>
                <option value="HIGH">High (Impacting Care)</option>
                <option value="CRITICAL">Critical (Life Support / Emergency)</option>
              </select>
              <div className="flex justify-end gap-2 pt-4 border-t border-border mt-4">
                <Button variant="secondary" onClick={() => setShowWOModal(false)}>Cancel</Button>
                <Button variant="primary" onClick={handleCreateWO}>Submit Work Order</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Transport Modal */}
      {showTransportModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader><CardTitle>Request Patient Transport</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <Input placeholder="Patient UUID" value={newTransport.patient.id} onChange={e => setNewTransport({...newTransport, patient: { id: e.target.value }})} />
              <div className="grid grid-cols-2 gap-4">
                <Input placeholder="From (e.g. GM-05)" value={newTransport.fromLocation} onChange={e => setNewTransport({...newTransport, fromLocation: e.target.value})} />
                <Input placeholder="To (e.g. Radiology)" value={newTransport.toLocation} onChange={e => setNewTransport({...newTransport, toLocation: e.target.value})} />
              </div>
              <select className="w-full h-10 px-3 border border-border rounded-md text-sm bg-white" value={newTransport.transportType} onChange={e => setNewTransport({...newTransport, transportType: e.target.value})}>
                <option value="WHEELCHAIR">Wheelchair</option>
                <option value="STRETCHER">Stretcher</option>
                <option value="BED">Bed (Full)</option>
              </select>
              <select className="w-full h-10 px-3 border border-border rounded-md text-sm bg-white" value={newTransport.priority} onChange={e => setNewTransport({...newTransport, priority: e.target.value})}>
                <option value="NORMAL">Normal</option>
                <option value="STAT">STAT / Emergency</option>
              </select>
              <div className="flex justify-end gap-2 pt-4 border-t border-border mt-4">
                <Button variant="secondary" onClick={() => setShowTransportModal(false)}>Cancel</Button>
                <Button variant="primary" onClick={handleCreateTransport}>Request Transport</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Incident Modal */}
      {showIncidentModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader><CardTitle className="text-error">Report Safety Incident</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <select className="w-full h-10 px-3 border border-border rounded-md text-sm bg-white" value={newIncident.incidentType} onChange={e => setNewIncident({...newIncident, incidentType: e.target.value})}>
                <option value="PATIENT_FALL">Patient Fall</option>
                <option value="MEDICATION_ERROR">Medication Error</option>
                <option value="NEEDLESTICK">Needlestick Injury</option>
                <option value="EQUIPMENT_FAILURE">Equipment Failure Harm</option>
                <option value="SECURITY">Security / Violence</option>
              </select>
              <select className="w-full h-10 px-3 border border-border rounded-md text-sm bg-white" value={newIncident.severity} onChange={e => setNewIncident({...newIncident, severity: e.target.value})}>
                <option value="LOW">Low (Near Miss / No Harm)</option>
                <option value="MEDIUM">Medium (Minor Harm)</option>
                <option value="HIGH">High (Severe Harm)</option>
                <option value="CRITICAL">Critical (Death / Sentinel Event)</option>
              </select>
              <Input placeholder="Location of Incident" value={newIncident.location} onChange={e => setNewIncident({...newIncident, location: e.target.value})} />
              <Input placeholder="Patient UUID (Optional)" value={newIncident.patientId} onChange={e => setNewIncident({...newIncident, patientId: e.target.value})} />
              <textarea className="w-full h-32 p-3 border border-border rounded-md text-sm focus:border-error outline-none resize-none" placeholder="Detailed factual description..." value={newIncident.description} onChange={e => setNewIncident({...newIncident, description: e.target.value})} />
              
              <div className="flex justify-end gap-2 pt-4 border-t border-border mt-4">
                <Button variant="secondary" onClick={() => setShowIncidentModal(false)}>Cancel</Button>
                <Button variant="primary" className="bg-error hover:bg-error/90 text-white" onClick={handleCreateIncident}>Submit Incident Report</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

    </div>
  );
}
