"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";

// Dummy data for OPD Billing
const DUMMY_INVOICES = [
  { id: "1", invoiceNo: "INV-2606-001", patientName: "Rahul Sharma", amount: "$150.00", status: "PENDING", date: "2026-06-21" },
  { id: "2", invoiceNo: "INV-2606-002", patientName: "Priya Patel", amount: "$2,400.00", status: "PARTIAL", date: "2026-06-20" },
  { id: "3", invoiceNo: "INV-2606-003", patientName: "Amit Kumar", amount: "$80.00", status: "PAID", date: "2026-06-21" },
];

// Dummy claims data
const CLAIMS = [
  { id: "CLM-001", patient: "Rahul Verma", policy: "HDFC Ergo Optima", preAuth: 150000, status: "PreAuth_Approved", date: "2026-06-12" },
  { id: "CLM-002", patient: "Neha Joshi", policy: "Star Health Comprehensive", preAuth: 80000, status: "PreAuth_Pending", date: "2026-06-18" },
  { id: "CLM-003", patient: "Suresh Kumar", policy: "ICICI Lombard", claimed: 210000, approved: 195000, status: "Settled", date: "2026-06-05" }
];

export default function BillingDashboard() {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState("opd");
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedPatient, setSelectedPatient] = useState<any>(null);

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "PENDING": return <Badge variant="warning">Pending</Badge>;
      case "PARTIAL": return <Badge variant="info">Partial</Badge>;
      case "PAID": return <Badge variant="success">Paid</Badge>;
      default: return <Badge>{status}</Badge>;
    }
  };

  const handleGenerateBill = (patient: any) => {
    setSelectedPatient(patient);
    setActiveTab("masterBill");
  };

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Financial Command Center</h2>
          <p className="text-text-secondary text-sm">Manage OPD Invoices, IPD Billing, and Insurance Claims</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-border">
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'opd' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('opd')}
        >
          OPD Invoices
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'discharges' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => { setActiveTab('discharges'); setSelectedPatient(null); }}
        >
          Pending IPD Bills
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'claims' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('claims')}
        >
          Insurance Claims Tracker
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'masterBill' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('masterBill')}
        >
          IPD Master Bill Engine
        </button>
      </div>

      {/* Content */}
      {activeTab === "opd" && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card className="bg-primary text-white border-none shadow-lg">
              <CardContent className="p-6">
                <div className="text-sm opacity-80 mb-1">Revenue Today</div>
                <div className="text-4xl font-bold">$4,520.00</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-6">
                <div className="text-sm text-text-secondary mb-1">Pending Receivables</div>
                <div className="text-4xl font-bold text-warning">$1,250.00</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-6">
                <div className="text-sm text-text-secondary mb-1">Invoices Generated</div>
                <div className="text-4xl font-bold text-text-primary">24</div>
              </CardContent>
            </Card>
          </div>

          <Card>
            <CardHeader>
              <div className="flex justify-between items-center w-full">
                <CardTitle>Recent OPD Invoices</CardTitle>
                <div className="w-64">
                  <Input 
                    placeholder="Search Invoice No or Patient..." 
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                  />
                </div>
              </div>
            </CardHeader>
            <CardContent>
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-border bg-surface text-text-secondary text-sm">
                    <th className="p-4 font-medium">Invoice No.</th>
                    <th className="p-4 font-medium">Patient</th>
                    <th className="p-4 font-medium">Date</th>
                    <th className="p-4 font-medium">Net Amount</th>
                    <th className="p-4 font-medium">Status</th>
                    <th className="p-4 font-medium text-right">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {DUMMY_INVOICES.map((inv) => (
                    <tr key={inv.id} className="border-b border-surface-hover hover:bg-surface-hover transition-colors">
                      <td className="p-4 font-medium text-primary">{inv.invoiceNo}</td>
                      <td className="p-4">{inv.patientName}</td>
                      <td className="p-4">{inv.date}</td>
                      <td className="p-4 font-bold">{inv.amount}</td>
                      <td className="p-4">{getStatusBadge(inv.status)}</td>
                      <td className="p-4 text-right">
                        <Button variant="secondary" size="sm" onClick={() => router.push(`/billing/invoice/${inv.id}`)}>
                          {inv.status === "PAID" ? "View Receipt" : "Collect Payment"}
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </CardContent>
          </Card>
        </div>
      )}

      {activeTab === "claims" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Insurance & Pre-Authorizations</CardTitle>
              <Button variant="secondary">New Claim / Pre-Auth</Button>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Claim ID</th>
                  <th className="p-3">Patient</th>
                  <th className="p-3">Policy Provider</th>
                  <th className="p-3">Pre-Auth / Claimed</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Action</th>
                </tr>
              </thead>
              <tbody>
                {CLAIMS.map(claim => (
                  <tr key={claim.id} className="border-b border-surface-hover">
                    <td className="p-3 font-medium">{claim.id}</td>
                    <td className="p-3">{claim.patient}</td>
                    <td className="p-3">{claim.policy}</td>
                    <td className="p-3 font-medium">₹{claim.preAuth || claim.claimed}</td>
                    <td className="p-3">
                      {claim.status === "PreAuth_Approved" && <Badge variant="success">PreAuth Approved</Badge>}
                      {claim.status === "PreAuth_Pending" && <Badge variant="warning">PreAuth Pending</Badge>}
                      {claim.status === "Settled" && <Badge variant="info">Settled</Badge>}
                    </td>
                    <td className="p-3">
                      <Button variant="secondary" size="sm">Update Status</Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "discharges" && (
        <Card>
          <CardHeader>
            <CardTitle>Patients Pending Final Bill (IPD)</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div className="p-4 border border-border rounded-md hover:bg-surface transition-colors cursor-pointer" onClick={() => handleGenerateBill({ name: 'Rahul Verma', id: 'MED-2026-00089', dischargeType: 'Normal', duration: 12 })}>
                <div className="flex justify-between items-start mb-2">
                  <div className="font-bold">Rahul Verma</div>
                  <Badge variant="warning">Billing Pending</Badge>
                </div>
                <div className="text-sm text-text-secondary mb-4">Discharged 2 hours ago. Total IPD duration: 12 days.</div>
                <Button variant="primary" className="w-full">Generate Master Bill</Button>
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      {activeTab === "masterBill" && (
        <Card>
          <CardHeader className="flex justify-between items-center border-b border-border pb-4">
            <CardTitle>IPD Master Bill</CardTitle>
            {selectedPatient && <Badge variant="info">{selectedPatient.name} | {selectedPatient.id}</Badge>}
          </CardHeader>
          <CardContent className="pt-4">
            {selectedPatient ? (
              <div className="space-y-6">
                
                <div className="border border-border rounded-md overflow-hidden">
                  <table className="w-full text-left text-sm">
                    <thead className="bg-surface border-b border-border">
                      <tr>
                        <th className="p-3">Category</th>
                        <th className="p-3">Description</th>
                        <th className="p-3 text-right">Qty / Days</th>
                        <th className="p-3 text-right">Unit Price</th>
                        <th className="p-3 text-right">Total Price</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-border">
                      <tr>
                         <td className="p-3 font-medium">Room Rent</td>
                         <td className="p-3">General Ward Bed (GM-05)</td>
                         <td className="p-3 text-right">12</td>
                         <td className="p-3 text-right">₹2,500.00</td>
                         <td className="p-3 text-right font-medium">₹30,000.00</td>
                      </tr>
                      <tr>
                         <td className="p-3 font-medium">Consultation</td>
                         <td className="p-3">Daily Rounds - Pulmonology</td>
                         <td className="p-3 text-right">12</td>
                         <td className="p-3 text-right">₹1,000.00</td>
                         <td className="p-3 text-right font-medium">₹12,000.00</td>
                      </tr>
                      <tr>
                         <td className="p-3 font-medium">Pharmacy</td>
                         <td className="p-3">Inpatient Medications (Aggregated)</td>
                         <td className="p-3 text-right">1</td>
                         <td className="p-3 text-right">₹45,300.00</td>
                         <td className="p-3 text-right font-medium">₹45,300.00</td>
                      </tr>
                      <tr>
                         <td className="p-3 font-medium">Lab & Radiology</td>
                         <td className="p-3">CBC, CXR, CT Thorax</td>
                         <td className="p-3 text-right">1</td>
                         <td className="p-3 text-right">₹18,500.00</td>
                         <td className="p-3 text-right font-medium">₹18,500.00</td>
                      </tr>
                      <tr>
                         <td className="p-3 font-medium">OT / Procedures</td>
                         <td className="p-3">ICU Ventillator Support (3 Days)</td>
                         <td className="p-3 text-right">3</td>
                         <td className="p-3 text-right">₹15,000.00</td>
                         <td className="p-3 text-right font-medium">₹45,000.00</td>
                      </tr>
                    </tbody>
                    <tfoot className="bg-surface-hover">
                      <tr>
                        <td colSpan={4} className="p-3 text-right font-bold">Gross Total:</td>
                        <td className="p-3 text-right font-bold text-lg">₹1,50,800.00</td>
                      </tr>
                      <tr>
                        <td colSpan={4} className="p-3 text-right font-medium text-success">Insurance Coverage (HDFC Ergo):</td>
                        <td className="p-3 text-right font-medium text-success">- ₹1,40,000.00</td>
                      </tr>
                      <tr>
                        <td colSpan={4} className="p-3 text-right font-medium">Advance Deposit Paid:</td>
                        <td className="p-3 text-right font-medium">- ₹5,000.00</td>
                      </tr>
                      <tr>
                        <td colSpan={4} className="p-3 text-right font-bold text-error">Net Patient Payable:</td>
                        <td className="p-3 text-right font-bold text-error text-xl">₹5,800.00</td>
                      </tr>
                    </tfoot>
                  </table>
                </div>

                <div className="flex justify-end gap-3 border-t border-border pt-6">
                  <Button variant="secondary">Save as Draft</Button>
                  <Button variant="primary" onClick={() => alert("Bill Finalized & Sent to Patient!")}>Finalize Bill & Generate Receipt</Button>
                </div>

              </div>
            ) : (
              <div className="py-24 text-center text-text-secondary">
                Select a discharged patient from the Pending Bills tab.
              </div>
            )}
          </CardContent>
        </Card>
      )}

    </div>
  );
}
