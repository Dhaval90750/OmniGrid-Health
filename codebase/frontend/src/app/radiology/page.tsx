"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function RadiologyWorkspace() {
  const [activeOrder, setActiveOrder] = useState<any>(null);
  const [templates, setTemplates] = useState<any[]>([]);
  
  const [findings, setFindings] = useState("");
  const [impression, setImpression] = useState("");
  const [isCritical, setIsCritical] = useState(false);

  const mockPendingOrders = [
    { id: "rad-101", patientName: "Jane Smith", uhid: "UHID-2042", modality: "X-Ray", study: "Chest PA", urgency: "Routine" },
    { id: "rad-102", patientName: "Robert Johnson", uhid: "UHID-5501", modality: "MRI", study: "Brain w/ Contrast", urgency: "Stat" }
  ];

  useEffect(() => {
    if (activeOrder) {
      fetchTemplates(activeOrder.modality);
    }
  }, [activeOrder]);

  const fetchTemplates = async (modality: string) => {
    try {
      const res = await api.get(`/radiology/templates?modality=${modality}`);
      setTemplates(res.data);
    } catch (e) {
      console.error(e);
      // fallback mock templates
      setTemplates([
        { id: "t1", templateName: `${modality} Normal`, contentTemplate: "Normal study without any significant abnormal findings." },
        { id: "t2", templateName: `${modality} Abnormal`, contentTemplate: "Abnormal findings observed." }
      ]);
    }
  };

  const applyTemplate = (templateId: string) => {
    const t = templates.find(x => x.id === templateId);
    if (t) {
      setFindings(t.contentTemplate);
      setImpression("Normal");
    }
  };

  const handleSaveReport = async (status: "Draft" | "Final") => {
    try {
      // await api.post(`/radiology/reports`, { ...payload, status });
      alert(`Report saved as ${status}.`);
      if (status === "Final") {
        setActiveOrder(null);
        setFindings("");
        setImpression("");
        setIsCritical(false);
      }
    } catch (e) {
      console.error(e);
      alert("Failed to save report.");
    }
  };

  return (
    <div className="flex h-[calc(100vh-80px)] -m-8">
      {/* Left Sidebar: Pending Orders */}
      <div className="w-80 border-r border-border bg-surface flex flex-col h-full overflow-y-auto">
        <div className="p-4 border-b border-border font-semibold text-text-primary sticky top-0 bg-surface z-10 shadow-sm">
          Pending Studies
        </div>
        <div className="p-2 space-y-2">
          {mockPendingOrders.map((order) => (
            <div 
              key={order.id} 
              className={`p-3 border rounded-md cursor-pointer transition-colors ${activeOrder?.id === order.id ? 'border-primary bg-primary/5' : 'border-border hover:border-primary/50'}`}
              onClick={() => setActiveOrder(order)}
            >
              <div className="flex justify-between items-start mb-1">
                <span className="font-semibold text-sm">{order.patientName}</span>
                {order.urgency === 'Stat' ? <Badge variant="error">STAT</Badge> : <Badge variant="secondary">Routine</Badge>}
              </div>
              <div className="text-xs text-text-secondary">{order.modality} • {order.study}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 flex flex-col bg-background h-full">
        {activeOrder ? (
          <>
            {/* Top Bar */}
            <div className="p-4 bg-primary-light text-primary-dark flex justify-between items-center shadow-sm">
              <div className="flex gap-6">
                <div><span className="text-xs opacity-80 block uppercase">Patient</span><span className="font-bold">{activeOrder.patientName}</span></div>
                <div><span className="text-xs opacity-80 block uppercase">Study</span><span className="font-bold">{activeOrder.modality} - {activeOrder.study}</span></div>
              </div>
              <Button variant="secondary" onClick={() => setActiveOrder(null)}>Close Study</Button>
            </div>

            {/* Split Workspace */}
            <div className="flex-1 flex overflow-hidden">
              {/* Viewer Mock */}
              <div className="flex-1 bg-black flex flex-col justify-center items-center text-white/50 border-r border-border relative">
                {/* Mocking a DICOM overlay text */}
                <div className="absolute top-4 left-4 text-xs font-mono">
                  {activeOrder.patientName}<br/>{activeOrder.uhid}<br/>DOB: 1980-01-01
                </div>
                <div className="absolute top-4 right-4 text-xs font-mono text-right">
                  {activeOrder.modality}<br/>{activeOrder.study}<br/>Today
                </div>
                
                <div className="w-64 h-64 border-2 border-white/20 rounded-full flex items-center justify-center animate-pulse">
                  DICOM Viewer Integration<br/>(Cornerstone.js Frame)
                </div>

                <div className="absolute bottom-4 left-4 text-xs font-mono">
                  W: 1500 L: -500
                </div>
              </div>

              {/* Reporting Panel */}
              <div className="w-[450px] bg-surface flex flex-col h-full overflow-y-auto">
                <div className="p-4 border-b border-border font-semibold text-text-primary">
                  Findings Report
                </div>
                
                <div className="p-4 space-y-4 flex-1">
                  <div>
                    <label className="text-xs font-medium text-text-secondary mb-1 block">Insert Template</label>
                    <select 
                      className="w-full p-2 border border-border rounded-md text-sm outline-none focus:border-primary"
                      onChange={(e) => applyTemplate(e.target.value)}
                    >
                      <option value="">Select Template...</option>
                      {templates.map(t => (
                        <option key={t.id} value={t.id}>{t.templateName}</option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label className="text-xs font-medium text-text-secondary mb-1 block">Findings</label>
                    <textarea 
                      className="w-full h-40 p-3 border border-border rounded-md focus:border-primary outline-none resize-none text-sm"
                      placeholder="Detailed findings..."
                      value={findings}
                      onChange={(e) => setFindings(e.target.value)}
                    />
                  </div>

                  <div>
                    <label className="text-xs font-medium text-text-secondary mb-1 block">Impression</label>
                    <textarea 
                      className="w-full h-24 p-3 border border-border rounded-md focus:border-primary outline-none resize-none text-sm font-semibold"
                      placeholder="Final impression/conclusion..."
                      value={impression}
                      onChange={(e) => setImpression(e.target.value)}
                    />
                  </div>

                  <div className="flex items-center gap-2">
                    <input 
                      type="checkbox" 
                      id="criticalFlag" 
                      checked={isCritical}
                      onChange={(e) => setIsCritical(e.target.checked)}
                      className="rounded border-border text-error focus:ring-error"
                    />
                    <label htmlFor="criticalFlag" className="text-sm font-medium text-error cursor-pointer">
                      Flag as Critical Finding
                    </label>
                  </div>
                </div>

                <div className="p-4 border-t border-border bg-surface shadow-[0_-4px_6px_-1px_rgba(0,0,0,0.05)] flex gap-2">
                  <Button variant="secondary" className="flex-1" onClick={() => handleSaveReport("Draft")}>Save Draft</Button>
                  <Button variant="primary" className="flex-1" onClick={() => handleSaveReport("Final")}>Sign & Finalize</Button>
                </div>
              </div>
            </div>
          </>
        ) : (
          <div className="flex-1 flex items-center justify-center text-text-secondary">
            <div className="text-center">
              <h3 className="text-xl font-medium mb-2">Radiology Workspace</h3>
              <p>Select a pending study from the left sidebar to begin reporting.</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
