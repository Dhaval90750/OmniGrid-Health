"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function LabDashboard() {
  const [activeTab, setActiveTab] = useState("COLLECTION"); // COLLECTION, RECEPTION, RESULTS, AUTHORIZATION
  
  // Mocks for UI until backend fully populates these workflows via specific endpoints
  const [samples, setSamples] = useState<any[]>([]);
  const [selectedSample, setSelectedSample] = useState<any>(null);
  
  // Result Entry State
  const [results, setResults] = useState<any>({});

  useEffect(() => {
    fetchSamples();
  }, [activeTab]);

  const fetchSamples = async () => {
    // In a real app, this would hit specific endpoints like /lab/samples/pending-collection, /lab/samples/pending-results, etc.
    // For MVP, we use mock data that mimics the flow based on activeTab
    try {
      let mockData: any[] = [];
      if (activeTab === "COLLECTION") {
        mockData = [
          { id: "s1", barcode: null, testName: "Complete Blood Count (CBC)", patientName: "Rahul Sharma", uhid: "UHID-1002", status: "ORDERED" }
        ];
      } else if (activeTab === "RECEPTION") {
        mockData = [
          { id: "s2", barcode: "LAB-2026-0001", testName: "Lipid Profile", patientName: "Amit Kumar", uhid: "UHID-1005", status: "COLLECTED" }
        ];
      } else if (activeTab === "RESULTS") {
        mockData = [
          { 
            id: "s3", 
            barcode: "LAB-2026-0002", 
            testName: "Complete Blood Count (CBC)", 
            patientName: "Priya Patel", 
            uhid: "UHID-1008", 
            status: "RECEIVED",
            parameters: [
              { id: "p1", name: "Hemoglobin", unit: "g/dL", refRange: "12.0 - 15.5" },
              { id: "p2", name: "WBC Count", unit: "cells/mcL", refRange: "4,500 - 11,000" },
              { id: "p3", name: "Platelets", unit: "cells/mcL", refRange: "150,000 - 450,000" }
            ]
          }
        ];
      } else if (activeTab === "AUTHORIZATION") {
        mockData = [
          { id: "s4", barcode: "LAB-2026-0003", testName: "Liver Function Test", patientName: "Sneha Reddy", uhid: "UHID-1012", status: "RESULT_ENTERED" }
        ];
      }
      setSamples(mockData);
      setSelectedSample(null);
      setResults({});
    } catch (e) {
      console.error(e);
    }
  };

  const handleGenerateBarcode = async (sample: any) => {
    try {
      // await api.post(`/lab/orders/${sample.orderId}/generate-barcodes`);
      alert("Barcode generated successfully: LAB-2026-XXXX");
      fetchSamples();
    } catch (e) {
      alert("Failed to generate barcode");
    }
  };

  const handleCollect = async (sampleId: string) => {
    try {
      await api.post(`/lab/samples/${sampleId}/collect`);
      alert("Sample marked as collected.");
      fetchSamples();
    } catch (e) {
      alert("Failed to collect sample");
    }
  };

  const handleReceive = async (sampleId: string) => {
    try {
      await api.post(`/lab/samples/${sampleId}/receive`, { accept: true });
      alert("Sample received in lab.");
      fetchSamples();
    } catch (e) {
      alert("Failed to receive sample");
    }
  };

  const handleSaveResults = async () => {
    if (!selectedSample) return;
    try {
      const payload = Object.keys(results).map(paramId => ({
        parameterId: paramId,
        value: results[paramId]
      }));
      await api.post(`/lab/samples/${selectedSample.id}/results`, payload);
      alert("Results saved successfully.");
      fetchSamples();
    } catch (e) {
      alert("Failed to save results");
    }
  };

  const handleAuthorize = async (sampleId: string) => {
    try {
      await api.post(`/lab/samples/${sampleId}/authorize`, []);
      alert("Report authorized and published.");
      fetchSamples();
    } catch (e) {
      alert("Failed to authorize report");
    }
  };

  const tabs = [
    { id: "COLLECTION", label: "Phlebotomy / Collection" },
    { id: "RECEPTION", label: "Sample Reception" },
    { id: "RESULTS", label: "Result Entry" },
    { id: "AUTHORIZATION", label: "Pathologist Auth" }
  ];

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Laboratory Information System (LIS)</h2>
          <p className="text-text-secondary text-sm">Manage end-to-clincal lab workflows.</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-2 border-b border-border">
        {tabs.map(tab => (
          <button
            key={tab.id}
            className={`px-4 py-2 font-semibold text-sm transition-colors border-b-2 ${activeTab === tab.id ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
            onClick={() => setActiveTab(tab.id)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Worklist */}
        <Card className="md:col-span-1 shadow-sm">
          <CardHeader className="bg-surface border-b border-border py-3">
            <CardTitle className="text-base">Worklist</CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            {samples.length === 0 ? (
              <div className="text-center p-8 text-text-secondary text-sm italic">No pending tasks in this queue.</div>
            ) : (
              <div className="divide-y divide-border">
                {samples.map(s => (
                  <div 
                    key={s.id} 
                    className={`p-4 cursor-pointer hover:bg-surface-hover ${selectedSample?.id === s.id ? 'bg-primary/5 border-l-4 border-primary' : 'border-l-4 border-transparent'}`}
                    onClick={() => setSelectedSample(s)}
                  >
                    <div className="font-bold text-sm text-primary-dark">{s.testName}</div>
                    <div className="text-xs text-text-secondary mt-1">{s.patientName} • {s.uhid}</div>
                    {s.barcode && <div className="text-xs font-mono mt-2 bg-gray-200 inline-block px-1 rounded">{s.barcode}</div>}
                    <div className="mt-2"><Badge variant="info">{s.status}</Badge></div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* Detail View */}
        <Card className="md:col-span-2 shadow-sm">
          <CardHeader className="bg-surface border-b border-border py-3">
            <CardTitle className="text-base">Action Panel</CardTitle>
          </CardHeader>
          <CardContent className="p-6">
            {!selectedSample ? (
              <div className="text-center p-12 text-text-secondary border-2 border-dashed border-border rounded-lg bg-surface">
                <div className="text-4xl mb-4">🔬</div>
                <p>Select a task from the worklist to take action.</p>
              </div>
            ) : (
              <div className="space-y-6">
                <div className="bg-primary/5 p-4 rounded-md border border-primary/20">
                  <h3 className="font-bold text-lg text-primary-dark">{selectedSample.testName}</h3>
                  <p className="text-sm text-text-secondary">Patient: {selectedSample.patientName} ({selectedSample.uhid})</p>
                  {selectedSample.barcode && <p className="text-sm font-mono mt-2">Barcode: {selectedSample.barcode}</p>}
                </div>

                {activeTab === "COLLECTION" && (
                  <div className="space-y-4">
                    <p className="text-sm">Please generate a barcode and affix it to the sample tube before collection.</p>
                    <div className="flex gap-4">
                      <Button variant="secondary" onClick={() => handleGenerateBarcode(selectedSample)}>Generate Barcode</Button>
                      <Button variant="primary" onClick={() => handleCollect(selectedSample.id)}>Mark as Collected</Button>
                    </div>
                  </div>
                )}

                {activeTab === "RECEPTION" && (
                  <div className="space-y-4">
                    <p className="text-sm">Scan barcode to receive sample in the main laboratory.</p>
                    <Input placeholder="Scan Barcode (e.g. LAB-2026-...)" className="max-w-sm" />
                    <Button variant="primary" onClick={() => handleReceive(selectedSample.id)}>Receive & Accept Sample</Button>
                  </div>
                )}

                {activeTab === "RESULTS" && selectedSample.parameters && (
                  <div className="space-y-6">
                    <h4 className="font-semibold border-b border-border pb-2">Enter Test Results</h4>
                    <table className="w-full text-left text-sm">
                      <thead>
                        <tr className="text-text-secondary">
                          <th className="pb-2">Parameter</th>
                          <th className="pb-2">Value</th>
                          <th className="pb-2">Unit</th>
                          <th className="pb-2">Reference Range</th>
                        </tr>
                      </thead>
                      <tbody>
                        {selectedSample.parameters.map((p: any) => (
                          <tr key={p.id} className="border-t border-border">
                            <td className="py-3 font-medium">{p.name}</td>
                            <td className="py-3">
                              <Input 
                                className="w-24 h-8 text-sm" 
                                value={results[p.id] || ""} 
                                onChange={e => setResults({...results, [p.id]: e.target.value})}
                              />
                            </td>
                            <td className="py-3 text-text-secondary">{p.unit}</td>
                            <td className="py-3 text-text-secondary">{p.refRange}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                    <div className="flex justify-end">
                      <Button variant="primary" onClick={handleSaveResults}>Save Results</Button>
                    </div>
                  </div>
                )}

                {activeTab === "AUTHORIZATION" && (
                  <div className="space-y-4">
                    <p className="text-sm text-warning-dark bg-warning/10 p-3 rounded-md border border-warning/20">
                      Results have been entered and are pending pathologist review. Please review the values carefully before finalizing.
                    </p>
                    <Button variant="primary" onClick={() => handleAuthorize(selectedSample.id)}>Authorize & Publish Report</Button>
                  </div>
                )}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
