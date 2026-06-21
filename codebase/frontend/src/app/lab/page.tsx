"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

export default function LabDashboard() {
  const [activeTab, setActiveTab] = useState("pending");

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Laboratory Information System</h1>
          <p className="text-text-secondary text-sm">Manage test orders, samples, and results</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-border">
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'pending' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('pending')}
        >
          Pending Orders
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'processing' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('processing')}
        >
          Active Processing
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'results' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('results')}
        >
          Result Entry
        </button>
      </div>

      {/* Content */}
      {activeTab === "pending" && (
        <Card>
          <CardHeader><CardTitle>Pending Sample Collections</CardTitle></CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Patient</th>
                  <th className="p-3">Test Ordered</th>
                  <th className="p-3">Priority</th>
                  <th className="p-3">Ordered By</th>
                  <th className="p-3">Action</th>
                </tr>
              </thead>
              <tbody>
                <tr className="border-b border-surface-hover">
                  <td className="p-3">
                    <div className="font-medium">Rahul Sharma</div>
                    <div className="text-xs text-text-secondary">UHID: MED-2026-000001</div>
                  </td>
                  <td className="p-3 font-medium">Complete Blood Count (CBC)</td>
                  <td className="p-3"><Badge variant="warning">Routine</Badge></td>
                  <td className="p-3">Dr. Anjali Desai</td>
                  <td className="p-3">
                    <Button variant="primary" size="sm" onClick={() => alert("Sample Barcode Printed & Collected")}>Collect Sample</Button>
                  </td>
                </tr>
                <tr className="border-b border-surface-hover">
                  <td className="p-3">
                    <div className="font-medium">Sneha Patel</div>
                    <div className="text-xs text-text-secondary">UHID: MED-2026-000045</div>
                  </td>
                  <td className="p-3 font-medium">Fasting Blood Sugar (FBS)</td>
                  <td className="p-3"><Badge variant="error">Stat</Badge></td>
                  <td className="p-3">Dr. Vikram Singh</td>
                  <td className="p-3">
                    <Button variant="primary" size="sm">Collect Sample</Button>
                  </td>
                </tr>
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "processing" && (
        <Card>
          <CardHeader><CardTitle>Samples Received (In Processing)</CardTitle></CardHeader>
          <CardContent>
             <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Barcode ID</th>
                  <th className="p-3">Patient</th>
                  <th className="p-3">Test</th>
                  <th className="p-3">Time Received</th>
                  <th className="p-3">Status</th>
                </tr>
              </thead>
              <tbody>
                <tr className="border-b border-surface-hover">
                  <td className="p-3 font-mono text-xs">LAB-0001-A</td>
                  <td className="p-3 font-medium">Vikram Singh</td>
                  <td className="p-3">Lipid Profile</td>
                  <td className="p-3">09:15 AM</td>
                  <td className="p-3"><Badge variant="info">Processing - Auto Analyzer</Badge></td>
                </tr>
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "results" && (
        <Card>
          <CardHeader><CardTitle>Result Entry & Authorization</CardTitle></CardHeader>
          <CardContent>
             <div className="border border-border rounded-md p-4 mb-4">
               <div className="flex justify-between border-b pb-4 mb-4">
                 <div>
                   <h3 className="font-bold text-lg">Complete Blood Count (CBC)</h3>
                   <div className="text-sm text-text-secondary">Sample ID: LAB-0002-B | Patient: Amit Kumar (MED-2026-000089)</div>
                 </div>
                 <Badge variant="warning">Awaiting Results</Badge>
               </div>

               <div className="space-y-4">
                 <div className="grid grid-cols-4 gap-4 items-center border-b pb-2">
                   <div className="col-span-1 font-medium text-sm">Hemoglobin (Hb)</div>
                   <div className="col-span-1">
                     <input type="number" className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" defaultValue={11.5} />
                   </div>
                   <div className="col-span-1 text-sm text-text-secondary">13.0 - 17.0 g/dL</div>
                   <div className="col-span-1"><Badge variant="error">Low (Abnormal)</Badge></div>
                 </div>

                 <div className="grid grid-cols-4 gap-4 items-center border-b pb-2">
                   <div className="col-span-1 font-medium text-sm">Total WBC Count</div>
                   <div className="col-span-1">
                     <input type="number" className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" defaultValue={6500} />
                   </div>
                   <div className="col-span-1 text-sm text-text-secondary">4000 - 11000 /cumm</div>
                   <div className="col-span-1"><Badge variant="success">Normal</Badge></div>
                 </div>

                 <div className="grid grid-cols-4 gap-4 items-center border-b pb-2">
                   <div className="col-span-1 font-medium text-sm">Platelet Count</div>
                   <div className="col-span-1">
                     <input type="number" className="w-full p-2 border border-border rounded-md focus:border-primary outline-none" defaultValue={150000} />
                   </div>
                   <div className="col-span-1 text-sm text-text-secondary">150000 - 450000 /cumm</div>
                   <div className="col-span-1"><Badge variant="success">Normal</Badge></div>
                 </div>
               </div>

               <div className="mt-6 flex justify-end gap-4">
                 <Button variant="secondary">Save Draft</Button>
                 <Button variant="primary" onClick={() => alert("Report Authorized & Sent to Doctor!")}>Authorize Report</Button>
               </div>
             </div>
          </CardContent>
        </Card>
      )}

    </div>
  );
}
