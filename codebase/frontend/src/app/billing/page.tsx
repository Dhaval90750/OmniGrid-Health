"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";
import { useAuthStore } from "@/store/useAuthStore";

// Removed dummy claims data

export default function BillingDashboard() {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState("opd");
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedPatient, setSelectedPatient] = useState<any>(null);
  
  const user = useAuthStore((state) => state.user);
  
  // Pending Discharges for IPD Billing
  const [pendingDischarges, setPendingDischarges] = useState<any[]>([]);
  
  const [invoices, setInvoices] = useState<any[]>([]);
  const [revenueToday, setRevenueToday] = useState(0);
  const [pendingReceivables, setPendingReceivables] = useState(0);
  
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [selectedInvoice, setSelectedInvoice] = useState<any>(null);
  const [paymentAmount, setPaymentAmount] = useState("");
  const [paymentMode, setPaymentMode] = useState("CASH");
  const [paymentRef, setPaymentRef] = useState("");

  const [loading, setLoading] = useState(false);

  const [claims, setClaims] = useState<any[]>([]);

  useEffect(() => {
    if (user && (user.permissions['BILLING_MANAGE'] === 'true' || user.roles.includes('ADMIN') || user.roles.includes('SUPER_ADMIN'))) {
      fetchInvoices();
      fetchPendingDischarges();
      fetchClaims();
    }
  }, [user]);

  const fetchPendingDischarges = async () => {
    try {
      const res = await api.get(`/billing/ipd/pending`);
      const mapped = res.data.map((bill: any) => ({
        id: bill.id,
        patientId: bill.patient?.id,
        name: bill.patient ? `${bill.patient.firstName} ${bill.patient.lastName}` : "Unknown",
        duration: 0, // Calculate from admission/discharge dates if available
        dischargedAt: bill.generatedAt || bill.createdAt
      }));
      setPendingDischarges(mapped);
    } catch (e) {
      console.error(e);
      alert("Failed to load pending IPD bills");
    }
  };

  const fetchClaims = async () => {
    try {
      const res = await api.get(`/billing/claims`);
      const mapped = res.data.map((claim: any) => ({
        id: claim.id,
        patient: claim.visit?.patient ? `${claim.visit.patient.firstName} ${claim.visit.patient.lastName}` : "Unknown",
        policy: claim.policy?.policyNumber || "Unknown Policy",
        preAuth: claim.preauthAmount || 0,
        claimed: claim.claimedAmount || 0,
        approved: claim.approvedAmount || 0,
        status: claim.status,
        date: claim.createdAt
      }));
      setClaims(mapped);
    } catch (e) {
      console.error(e);
      alert("Failed to load insurance claims");
    }
  };

  const fetchInvoices = async () => {
    try {
      // In a real system, we'd query by date or status. Here we get pending from our backend.
      const res = await api.get(`/billing/invoices/pending`);
      const fetchedInvoices = res.data;
      
      // Calculate KPIs
      let pending = 0;
      let revenue = 0;
      
      fetchedInvoices.forEach((inv: any) => {
        if (inv.status !== "PAID") {
          pending += (inv.netAmount - inv.amountPaid);
        }
        // Assume anything collected today goes to revenue. For MVP, we just sum amountPaid.
        revenue += inv.amountPaid; 
      });
      
      setInvoices(fetchedInvoices);
      setPendingReceivables(pending);
      setRevenueToday(revenue);
      
    } catch (e) {
      console.error(e);
      // Fallback for UI if API not seeded
      setInvoices([]);
    }
  };

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

  const openPaymentModal = (invoice: any) => {
    setSelectedInvoice(invoice);
    setPaymentAmount((invoice.netAmount - invoice.amountPaid).toString());
    setShowPaymentModal(true);
  };

  const handleCollectPayment = async () => {
    if (!selectedInvoice) return;
    setLoading(true);
    try {
      await api.post(`/billing/invoices/${selectedInvoice.id}/pay`, {
        amount: parseFloat(paymentAmount),
        paymentMode: paymentMode,
        referenceNumber: paymentRef
      });
      alert("Payment Collected Successfully!");
      setShowPaymentModal(false);
      fetchInvoices();
    } catch (e) {
      alert("Error collecting payment");
    } finally {
      setLoading(false);
    }
  };

  const handleFinalizeMasterBill = async () => {
    try {
      if (!selectedPatient || !selectedPatient.patientId) {
        alert("Invalid patient selection");
        return;
      }
      
      const payload = {
        patient: { id: selectedPatient.patientId },
        invoiceNumber: `MB-${Date.now()}`,
        status: "PENDING",
        totalAmount: 150800.00,
        discountAmount: 140000.00, // Insurance
        netAmount: 10800.00, // Advance deducted in real calc, but here just net
        amountPaid: 5000.00
      };
      await api.post(`/billing/invoices`, payload);
      alert("Bill Finalized & Sent to Patient!");
      setActiveTab("opd");
      fetchInvoices();
      setPendingDischarges(pendingDischarges.filter(p => p.id !== selectedPatient.id));
    } catch (e) {
      console.error(e);
      alert("Failed to finalize bill");
    }
  };

  if (!user) {
    return <div className="p-8 text-center">Loading...</div>;
  }

  const hasAccess = user.permissions['BILLING_MANAGE'] === 'true' || user.roles.includes('ADMIN') || user.roles.includes('SUPER_ADMIN');
  if (!hasAccess) {
    return (
      <div className="flex items-center justify-center h-full min-h-[600px]">
        <Card className="w-full max-w-md border-error">
          <CardHeader className="bg-error/10">
            <CardTitle className="text-error flex items-center gap-2">
              <span>Access Denied</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="pt-6 text-center text-text-secondary">
            You do not have the required permissions (BILLING_MANAGE) to access the Financial Command Center.
          </CardContent>
        </Card>
      </div>
    );
  }

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
          Invoices & Receivables
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
      </div>

      {/* Content */}
      {activeTab === "opd" && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card className="bg-primary text-white border-none shadow-lg">
              <CardContent className="p-6">
                <div className="text-sm opacity-80 mb-1">Collections Today</div>
                <div className="text-4xl font-bold">₹{revenueToday.toLocaleString()}</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-6">
                <div className="text-sm text-text-secondary mb-1">Pending Receivables</div>
                <div className="text-4xl font-bold text-warning">₹{pendingReceivables.toLocaleString()}</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-6">
                <div className="text-sm text-text-secondary mb-1">Active Invoices</div>
                <div className="text-4xl font-bold text-text-primary">{invoices.length}</div>
              </CardContent>
            </Card>
          </div>

          <Card>
            <CardHeader>
              <div className="flex justify-between items-center w-full">
                <CardTitle>Invoices</CardTitle>
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
              {invoices.length === 0 ? (
                <div className="text-center p-8 text-text-secondary italic">No invoices found. Generate one by completing an OPD visit.</div>
              ) : (
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="border-b border-border bg-surface text-text-secondary text-sm">
                      <th className="p-4 font-medium">Invoice No.</th>
                      <th className="p-4 font-medium">Patient</th>
                      <th className="p-4 font-medium">Date</th>
                      <th className="p-4 font-medium">Net Amount</th>
                      <th className="p-4 font-medium">Balance Due</th>
                      <th className="p-4 font-medium">Status</th>
                      <th className="p-4 font-medium text-right">Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {invoices.filter(i => (i.invoiceNumber + (i.patient?.firstName||"")).toLowerCase().includes(searchQuery.toLowerCase())).map((inv) => (
                      <tr key={inv.id} className="border-b border-surface-hover hover:bg-surface-hover transition-colors">
                        <td className="p-4 font-medium text-primary">{inv.invoiceNumber}</td>
                        <td className="p-4">{inv.patient?.firstName} {inv.patient?.lastName}</td>
                        <td className="p-4">{new Date(inv.createdAt).toLocaleDateString()}</td>
                        <td className="p-4 font-bold">₹{inv.netAmount.toLocaleString()}</td>
                        <td className="p-4 font-bold text-error">₹{(inv.netAmount - inv.amountPaid).toLocaleString()}</td>
                        <td className="p-4">{getStatusBadge(inv.status)}</td>
                        <td className="p-4 text-right">
                          <Button variant="secondary" size="sm" onClick={() => {
                            if(inv.status === "PAID") alert("Receipt preview would open here.");
                            else openPaymentModal(inv);
                          }}>
                            {inv.status === "PAID" ? "View Receipt" : "Collect Payment"}
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </CardContent>
          </Card>
        </div>
      )}

      {/* Payment Modal */}
      {showPaymentModal && selectedInvoice && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader>
              <CardTitle>Collect Payment</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <div className="text-sm text-text-secondary">Invoice: <span className="font-bold text-text-primary">{selectedInvoice.invoiceNumber}</span></div>
                <div className="text-sm text-text-secondary">Patient: <span className="font-bold text-text-primary">{selectedInvoice.patient?.firstName} {selectedInvoice.patient?.lastName}</span></div>
                <div className="text-sm text-text-secondary">Balance Due: <span className="font-bold text-error">₹{(selectedInvoice.netAmount - selectedInvoice.amountPaid).toLocaleString()}</span></div>
              </div>
              
              <div>
                <label className="text-sm font-medium mb-1 block">Payment Amount (₹)</label>
                <Input type="number" value={paymentAmount} onChange={e => setPaymentAmount(e.target.value)} />
              </div>
              <div>
                <label className="text-sm font-medium mb-1 block">Payment Mode</label>
                <select className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm bg-white" value={paymentMode} onChange={e => setPaymentMode(e.target.value)}>
                  <option>CASH</option>
                  <option>CARD</option>
                  <option>UPI</option>
                  <option>INSURANCE</option>
                </select>
              </div>
              <div>
                <label className="text-sm font-medium mb-1 block">Reference Number (if applicable)</label>
                <Input value={paymentRef} onChange={e => setPaymentRef(e.target.value)} />
              </div>

              <div className="flex justify-end gap-2 pt-4 border-t border-border mt-4">
                <Button variant="secondary" onClick={() => setShowPaymentModal(false)}>Cancel</Button>
                <Button variant="primary" onClick={handleCollectPayment} disabled={loading || !paymentAmount}>Confirm Payment</Button>
              </div>
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
                {claims.map(claim => (
                  <tr key={claim.id} className="border-b border-surface-hover">
                    <td className="p-3 font-medium">{claim.id}</td>
                    <td className="p-3">{claim.patient}</td>
                    <td className="p-3">{claim.policy}</td>
                    <td className="p-3 font-medium">₹{claim.preAuth || claim.claimed}</td>
                    <td className="p-3">
                      {claim.status === "PreAuth_Approved" && <Badge variant="success">PreAuth Approved</Badge>}
                      {claim.status === "PreAuth_Pending" && <Badge variant="warning">PreAuth Pending</Badge>}
                      {claim.status === "Settled" && <Badge variant="info">Settled</Badge>}
                      {claim.status !== "PreAuth_Approved" && claim.status !== "PreAuth_Pending" && claim.status !== "Settled" && <Badge>{claim.status}</Badge>}
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
              {pendingDischarges.length === 0 ? (
                <div className="col-span-3 py-12 text-center text-text-secondary">No pending IPD bills.</div>
              ) : (
                pendingDischarges.map(pd => (
                  <div key={pd.id} className="p-4 border border-border rounded-md hover:bg-surface transition-colors cursor-pointer" onClick={() => handleGenerateBill(pd)}>
                    <div className="flex justify-between items-start mb-2">
                      <div className="font-bold">{pd.name}</div>
                      <Badge variant="warning">Billing Pending</Badge>
                    </div>
                    <div className="text-sm text-text-secondary mb-4">Discharged. Total IPD duration: {pd.duration} days.</div>
                    <Button variant="primary" className="w-full">Generate Master Bill</Button>
                  </div>
                ))
              )}
            </div>
          </CardContent>
        </Card>
      )}

      {activeTab === "masterBill" && (
        <Card>
          <CardHeader className="flex justify-between items-center border-b border-border pb-4">
            <CardTitle>IPD Master Bill</CardTitle>
            {selectedPatient && <Badge variant="info">{selectedPatient.name}</Badge>}
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
                  <Button variant="primary" onClick={handleFinalizeMasterBill}>Finalize Bill & Generate Receipt</Button>
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
