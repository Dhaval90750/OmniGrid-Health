"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";

export default function RadiologyDashboard() {
  const [activeTab, setActiveTab] = useState("X-Ray"); // X-Ray, CT Scan, MRI, Ultrasound
  const [stats, setStats] = useState<any>({ total: 0, pending: 0, completed: 0, critical: 0 });
  const [criticalReports, setCriticalReports] = useState<any[]>([]);
  const [worklist, setWorklist] = useState<any[]>([]);
  
  const [selectedOrder, setSelectedOrder] = useState<any>(null);
  const [templates, setTemplates] = useState<any[]>([]);
  const [reportFindings, setReportFindings] = useState("");
  const [reportImpression, setReportImpression] = useState("");
  const [isCritical, setIsCritical] = useState(false);

  useEffect(() => {
    fetchStats();
    fetchWorklist();
  }, [activeTab]);

  const fetchStats = async () => {
    try {
      const statRes = await api.get("/radiology/dashboard/stats");
      setStats(statRes.data);
      const critRes = await api.get("/radiology/dashboard/critical");
      setCriticalReports(critRes.data);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchWorklist = async () => {
    try {
      const res = await api.get(`/radiology/orders/pending?modality=${activeTab}`);
      // Transform response to match expected UI structure
      const formattedList = res.data.map((o: any) => ({
        id: o.id,
        studyName: o.studyDescription,
        patientName: o.patient ? `${o.patient.firstName} ${o.patient.lastName}` : "Unknown Patient",
        uhid: o.patient ? o.patient.uhid : "N/A",
        priority: o.urgency ? o.urgency.toUpperCase() : "ROUTINE",
        status: o.status,
        date: o.createdAt || new Date().toISOString()
      }));
      setWorklist(formattedList);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchTemplates = async (modality: string) => {
    try {
      const res = await api.get(`/radiology/templates?modality=${modality}`);
      setTemplates(res.data);
    } catch (e) {
      console.error(e);
      // Fallback
      setTemplates([
        { id: "t1", name: "Standard Chest X-Ray", content: "Lungs are clear. Heart size is normal. No pleural effusion or pneumothorax." }
      ]);
    }
  };

  const handleSelectOrder = (order: any) => {
    setSelectedOrder(order);
    fetchTemplates(activeTab);
    setReportFindings("");
    setReportImpression("");
    setIsCritical(order.priority === "STAT");
  };

  const applyTemplate = (templateContent: string) => {
    setReportFindings(templateContent);
  };

  const handleSubmitReport = async () => {
    if (!selectedOrder) return;
    try {
      const payload = {
        order: { id: selectedOrder.id }, // Need proper UUID in real backend
        reportingDoctor: { id: "1" },
        findings: reportFindings,
        impression: reportImpression,
        isCritical: isCritical
      };
      await api.post("/radiology/reports", payload);
      alert("Radiology Report Published Successfully!");
      setSelectedOrder(null);
      fetchStats();
      fetchWorklist();
    } catch (e) {
      console.error(e);
      alert("Failed to submit report.");
    }
  };

  const modalities = ["X-Ray", "CT Scan", "MRI", "Ultrasound"];

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Radiology Information System (RIS)</h2>
          <p className="text-text-secondary text-sm">Manage imaging orders and generate diagnostic reports.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="bg-surface shadow-sm">
          <CardContent className="p-4 text-center">
            <div className="text-sm text-text-secondary">Total Orders</div>
            <div className="text-3xl font-bold">{stats.total || 0}</div>
          </CardContent>
        </Card>
        <Card className="bg-surface shadow-sm">
          <CardContent className="p-4 text-center">
            <div className="text-sm text-text-secondary">Pending Worklist</div>
            <div className="text-3xl font-bold text-warning">{stats.pending || 0}</div>
          </CardContent>
        </Card>
        <Card className="bg-surface shadow-sm">
          <CardContent className="p-4 text-center">
            <div className="text-sm text-text-secondary">Completed</div>
            <div className="text-3xl font-bold text-success">{stats.completed || 0}</div>
          </CardContent>
        </Card>
        <Card className="bg-error text-white shadow-sm border-none">
          <CardContent className="p-4 text-center">
            <div className="text-sm opacity-90">STAT / Critical</div>
            <div className="text-3xl font-bold">{stats.critical || 0}</div>
          </CardContent>
        </Card>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-border">
        {modalities.map(mod => (
          <button
            key={mod}
            className={`pb-2 px-4 text-sm font-medium border-b-2 ${activeTab === mod ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
            onClick={() => { setActiveTab(mod); setSelectedOrder(null); }}
          >
            {mod}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Worklist Column */}
        <Card className="md:col-span-1 shadow-sm">
          <CardHeader className="bg-surface border-b border-border py-3">
            <CardTitle className="text-base flex justify-between items-center">
              Pending Queue
              <Badge>{worklist.length}</Badge>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            {worklist.length === 0 ? (
              <div className="text-center p-8 text-text-secondary text-sm italic">No pending {activeTab} orders.</div>
            ) : (
              <div className="divide-y divide-border">
                {worklist.map(order => (
                  <div 
                    key={order.id} 
                    className={`p-4 cursor-pointer hover:bg-surface-hover ${selectedOrder?.id === order.id ? 'bg-primary/5 border-l-4 border-primary' : 'border-l-4 border-transparent'}`}
                    onClick={() => handleSelectOrder(order)}
                  >
                    <div className="flex justify-between items-start mb-1">
                      <div className="font-bold text-sm text-primary-dark">{order.studyName}</div>
                      {order.priority === "STAT" && <Badge variant="warning">STAT</Badge>}
                    </div>
                    <div className="text-xs text-text-secondary">{order.patientName} • {order.uhid}</div>
                    <div className="text-xs text-text-secondary mt-1">Ordered: {new Date(order.date).toLocaleTimeString()}</div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* Reporting Column */}
        <Card className="md:col-span-2 shadow-sm">
          <CardHeader className="bg-surface border-b border-border py-3 flex justify-between items-center flex-row">
            <CardTitle className="text-base">Radiologist Workspace</CardTitle>
          </CardHeader>
          <CardContent className="p-0 flex h-[600px]">
            {!selectedOrder ? (
              <div className="w-full flex flex-col items-center justify-center text-text-secondary">
                <div className="text-5xl mb-4">🩻</div>
                <p>Select a study from the queue to dictate a report.</p>
              </div>
            ) : (
              <div className="w-full flex flex-col">
                <div className="p-4 border-b border-border bg-gray-50 flex justify-between items-center">
                  <div>
                    <h3 className="font-bold text-lg">{selectedOrder.studyName}</h3>
                    <div className="text-sm text-text-secondary">Patient: {selectedOrder.patientName} ({selectedOrder.uhid})</div>
                  </div>
                  <Button variant="secondary" size="sm" onClick={() => window.open('https://www.osirix-viewer.com/', '_blank')}>
                    Open DICOM Viewer ↗
                  </Button>
                </div>
                
                <div className="p-4 flex-1 overflow-y-auto space-y-4">
                  {/* Template selector */}
                  {templates.length > 0 && (
                    <div className="flex gap-2 items-center mb-4">
                      <span className="text-sm font-semibold">Quick Template:</span>
                      {templates.map(t => (
                        <Badge key={t.id} variant="info" className="cursor-pointer" onClick={() => applyTemplate(t.content)}>
                          {t.name}
                        </Badge>
                      ))}
                    </div>
                  )}

                  <div>
                    <label className="font-bold text-sm block mb-1">Clinical Findings</label>
                    <textarea 
                      className="w-full h-40 p-3 border border-border rounded-md text-sm focus:border-primary outline-none resize-none" 
                      placeholder="Dictate or type clinical findings here..."
                      value={reportFindings}
                      onChange={e => setReportFindings(e.target.value)}
                    ></textarea>
                  </div>

                  <div>
                    <label className="font-bold text-sm block mb-1">Impression / Conclusion</label>
                    <textarea 
                      className="w-full h-24 p-3 border border-border rounded-md text-sm focus:border-primary outline-none resize-none" 
                      placeholder="Summary impression..."
                      value={reportImpression}
                      onChange={e => setReportImpression(e.target.value)}
                    ></textarea>
                  </div>

                  <div className="flex items-center gap-2">
                    <input 
                      type="checkbox" 
                      id="critical" 
                      checked={isCritical} 
                      onChange={e => setIsCritical(e.target.checked)} 
                    />
                    <label htmlFor="critical" className="text-sm font-bold text-error">Flag as Critical / Urgent Finding</label>
                  </div>
                </div>

                <div className="p-4 border-t border-border flex justify-end gap-3 bg-surface">
                  <Button variant="secondary" onClick={() => setSelectedOrder(null)}>Cancel</Button>
                  <Button variant="primary" onClick={handleSubmitReport} disabled={!reportFindings || !reportImpression}>
                    Sign & Publish Report
                  </Button>
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
