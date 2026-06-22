"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function LabDashboard() {
  const [activeTab, setActiveTab] = useState<"collection" | "results">("collection");
  const [barcodeQuery, setBarcodeQuery] = useState("");
  const [activeSample, setActiveSample] = useState<any>(null);
  
  // For results entry
  const [results, setResults] = useState([{ testName: "Hemoglobin", value: "", unit: "g/dL", refRange: "13-17" }]);

  const handleBarcodeScan = async (e: React.FormEvent) => {
    e.preventDefault();
    // Simulate finding a sample
    setActiveSample({
      id: "sample-123",
      barcode: barcodeQuery,
      status: "Pending_Collection",
      test: { testName: "CBC", category: "Hematology" },
      patient: { name: "John Doe", uhid: "UHID-1001" }
    });
  };

  const markCollected = async () => {
    try {
      // await api.post(`/lab/samples/${activeSample.id}/collect`);
      alert(`Sample ${activeSample.barcode} marked as Collected.`);
      setActiveSample(null);
      setBarcodeQuery("");
    } catch (e) {
      console.error(e);
    }
  };

  const authorizeResults = async () => {
    try {
      // await api.post(`/lab/samples/${activeSample.id}/authorize`, formattedResults);
      alert("Results Authorized and Signed off.");
      setActiveSample(null);
      setBarcodeQuery("");
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Laboratory Information System</h2>
          <p className="text-text-secondary text-sm">Manage sample collection and result authorizations.</p>
        </div>
      </div>

      <div className="flex gap-4 border-b border-border pb-2">
        <button 
          className={`px-4 py-2 font-medium text-sm rounded-md transition-colors ${activeTab === 'collection' ? 'bg-primary text-white' : 'text-text-secondary hover:bg-surface-hover'}`}
          onClick={() => setActiveTab('collection')}
        >
          Sample Collection
        </button>
        <button 
          className={`px-4 py-2 font-medium text-sm rounded-md transition-colors ${activeTab === 'results' ? 'bg-primary text-white' : 'text-text-secondary hover:bg-surface-hover'}`}
          onClick={() => setActiveTab('results')}
        >
          Pathologist Review
        </button>
      </div>

      {activeTab === "collection" && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <Card>
            <CardHeader><CardTitle>Scan Barcode</CardTitle></CardHeader>
            <CardContent>
              <form onSubmit={handleBarcodeScan} className="flex gap-4">
                <Input placeholder="Scan sample barcode..." value={barcodeQuery} onChange={(e) => setBarcodeQuery(e.target.value)} autoFocus className="flex-1" />
                <Button type="submit" variant="secondary">Lookup</Button>
              </form>
            </CardContent>
          </Card>

          {activeSample && (
            <Card className="border-primary/20 bg-primary-light">
              <CardHeader><CardTitle>Sample Details</CardTitle></CardHeader>
              <CardContent className="space-y-4">
                <div className="flex justify-between border-b border-primary/20 pb-2">
                  <span className="text-sm">Patient</span>
                  <span className="font-semibold">{activeSample.patient.name} ({activeSample.patient.uhid})</span>
                </div>
                <div className="flex justify-between border-b border-primary/20 pb-2">
                  <span className="text-sm">Test</span>
                  <span className="font-semibold">{activeSample.test.testName}</span>
                </div>
                <div className="flex justify-between pb-2">
                  <span className="text-sm">Barcode</span>
                  <span className="font-mono bg-white px-2 py-1 rounded">{activeSample.barcode}</span>
                </div>
                <Button variant="primary" className="w-full" onClick={markCollected}>
                  Confirm Sample Collected
                </Button>
              </CardContent>
            </Card>
          )}
        </div>
      )}

      {activeTab === "results" && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card className="md:col-span-1">
            <CardHeader><CardTitle>Pending Reviews</CardTitle></CardHeader>
            <CardContent>
              <div className="space-y-2">
                <div 
                  className="p-3 border border-border rounded-md hover:border-primary cursor-pointer transition-colors"
                  onClick={() => handleBarcodeScan({ preventDefault: () => {} } as any)}
                >
                  <div className="flex justify-between items-center mb-1">
                    <span className="font-semibold text-sm">John Doe</span>
                    <Badge variant="warning">Processing</Badge>
                  </div>
                  <div className="text-xs text-text-secondary">CBC • LAB-2026-A1B2C3D4</div>
                </div>
              </div>
            </CardContent>
          </Card>

          {activeSample && (
            <Card className="md:col-span-2">
              <CardHeader><CardTitle>Enter Results for {activeSample.test.testName}</CardTitle></CardHeader>
              <CardContent className="space-y-6">
                <div className="bg-surface p-4 rounded-md flex justify-between items-center text-sm">
                  <div><span className="text-text-secondary">Patient:</span> <span className="font-medium">{activeSample.patient.name}</span></div>
                  <div><span className="text-text-secondary">Barcode:</span> <span className="font-mono">{activeSample.barcode}</span></div>
                </div>

                <div className="border border-border rounded-md overflow-hidden">
                  <table className="w-full text-left text-sm">
                    <thead className="bg-surface border-b border-border">
                      <tr>
                        <th className="p-3 font-medium">Parameter</th>
                        <th className="p-3 font-medium">Value</th>
                        <th className="p-3 font-medium">Unit</th>
                        <th className="p-3 font-medium">Reference</th>
                      </tr>
                    </thead>
                    <tbody>
                      {results.map((res, idx) => (
                        <tr key={idx} className="border-b border-surface-hover">
                          <td className="p-3">{res.testName}</td>
                          <td className="p-3">
                            <input 
                              type="number" 
                              className="w-24 p-1 border border-border rounded-md focus:border-primary outline-none"
                              value={res.value}
                              onChange={(e) => {
                                const newRes = [...results];
                                newRes[idx].value = e.target.value;
                                setResults(newRes);
                              }}
                            />
                          </td>
                          <td className="p-3 text-text-secondary">{res.unit}</td>
                          <td className="p-3 text-text-secondary">{res.refRange}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                <div className="flex justify-end gap-4">
                  <Button variant="secondary" onClick={() => setActiveSample(null)}>Cancel</Button>
                  <Button variant="primary" onClick={authorizeResults}>Sign & Authorize Report</Button>
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      )}
    </div>
  );
}
