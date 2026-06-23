"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";

export default function OPDBilling() {
  const [searchQuery, setSearchQuery] = useState("");
  const [activeInvoice, setActiveInvoice] = useState<any>(null);
  const [showPaymentDialog, setShowPaymentDialog] = useState(false);
  const [paymentData, setPaymentData] = useState({ amount: "", paymentMethod: "CASH", referenceNumber: "" });
  const [receipt, setReceipt] = useState<any>(null);

  const searchVisit = () => {
    // Mock fetching an active visit and creating a pending invoice
    setActiveInvoice({
      id: "inv-1",
      invoiceNumber: "INV-" + Date.now(),
      patientName: "John Doe",
      uhid: "UHID-1001",
      status: "PENDING",
      items: [
        { description: "General Physician Consultation", quantity: 1, unitCost: 50.00, totalCost: 50.00 },
        { description: "Complete Blood Count", quantity: 1, unitCost: 25.00, totalCost: 25.00 }
      ],
      totalAmount: 75.00,
      discountAmount: 0.00,
      netAmount: 75.00,
      amountPaid: 0.00
    });
  };

  const handlePayment = () => {
    // Mock processing payment
    const newPaid = activeInvoice.amountPaid + parseFloat(paymentData.amount);
    const updatedInvoice = {
      ...activeInvoice,
      amountPaid: newPaid,
      status: newPaid >= activeInvoice.netAmount ? "PAID" : "PARTIAL"
    };
    setActiveInvoice(updatedInvoice);
    setReceipt({
      receiptNumber: "REC-" + Date.now(),
      date: new Date().toLocaleString(),
      amount: paymentData.amount,
      method: paymentData.paymentMethod
    });
    setShowPaymentDialog(false);
  };

  const printReceipt = () => {
    const printWindow = window.open('', '', 'width=600,height=600');
    printWindow?.document.write(`
      <html>
        <head><title>Receipt</title><style>body { font-family: sans-serif; padding: 20px; }</style></head>
        <body>
          <h2>MedCore Hospital</h2>
          <p><strong>Receipt #:</strong> ${receipt.receiptNumber}</p>
          <p><strong>Date:</strong> ${receipt.date}</p>
          <p><strong>Patient:</strong> ${activeInvoice.patientName} (${activeInvoice.uhid})</p>
          <hr/>
          <p><strong>Amount Paid:</strong> $${parseFloat(receipt.amount).toFixed(2)}</p>
          <p><strong>Payment Method:</strong> ${receipt.method}</p>
          <hr/>
          <p>Thank you.</p>
        </body>
      </html>
    `);
    printWindow?.document.close();
    printWindow?.print();
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">OPD Billing Desk</h2>
          <p className="text-text-secondary text-sm">Generate invoices and collect payments for Outpatient visits.</p>
        </div>
      </div>

      {!activeInvoice && (
        <Card className="max-w-md mx-auto mt-12">
          <CardHeader><CardTitle className="text-center">Search Active Visit</CardTitle></CardHeader>
          <CardContent className="space-y-4">
            <Input placeholder="Enter UHID or Visit ID (e.g. UHID-1001)" value={searchQuery} onChange={e => setSearchQuery(e.target.value)} />
            <Button className="w-full" onClick={searchVisit}>Search & Generate Bill</Button>
          </CardContent>
        </Card>
      )}

      {activeInvoice && (
        <div className="grid grid-cols-3 gap-6">
          <div className="col-span-2 space-y-6">
            <Card>
              <CardHeader className="py-4 border-b border-border bg-surface flex flex-row justify-between items-center">
                <CardTitle className="text-lg">Invoice: {activeInvoice.invoiceNumber}</CardTitle>
                <Badge variant={activeInvoice.status === 'PAID' ? 'success' : 'default'}>{activeInvoice.status}</Badge>
              </CardHeader>
              <CardContent className="p-0">
                <div className="p-4 bg-primary/5 text-primary-dark font-medium border-b border-border">
                  Patient: {activeInvoice.patientName} ({activeInvoice.uhid})
                </div>
                <table className="w-full text-left text-sm">
                  <thead className="bg-surface border-b border-border">
                    <tr>
                      <th className="p-4 font-medium text-text-secondary">Description</th>
                      <th className="p-4 font-medium text-text-secondary">Qty</th>
                      <th className="p-4 font-medium text-text-secondary">Unit Price</th>
                      <th className="p-4 font-medium text-text-secondary text-right">Total</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-border">
                    {activeInvoice.items.map((item:any, i:number) => (
                      <tr key={i} className="hover:bg-surface-hover">
                        <td className="p-4">{item.description}</td>
                        <td className="p-4">{item.quantity}</td>
                        <td className="p-4">${item.unitCost.toFixed(2)}</td>
                        <td className="p-4 text-right font-medium">${item.totalCost.toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </CardContent>
            </Card>
          </div>

          <div className="space-y-6">
            <Card>
              <CardHeader className="py-4 border-b border-border bg-surface"><CardTitle className="text-lg">Payment Summary</CardTitle></CardHeader>
              <CardContent className="p-6 space-y-4">
                <div className="flex justify-between text-sm">
                  <span className="text-text-secondary">Subtotal</span>
                  <span>${activeInvoice.totalAmount.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-text-secondary">Discount</span>
                  <span>${activeInvoice.discountAmount.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-lg font-bold border-t border-border pt-4">
                  <span>Net Amount</span>
                  <span>${activeInvoice.netAmount.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-sm text-success-dark font-medium">
                  <span>Amount Paid</span>
                  <span>${activeInvoice.amountPaid.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-lg font-bold text-error mt-4">
                  <span>Balance Due</span>
                  <span>${(activeInvoice.netAmount - activeInvoice.amountPaid).toFixed(2)}</span>
                </div>

                <div className="pt-6 space-y-3">
                  {activeInvoice.status !== 'PAID' && (
                    <Button className="w-full bg-success hover:bg-success-dark" onClick={() => setShowPaymentDialog(true)}>Collect Payment</Button>
                  )}
                  {receipt && (
                    <Button className="w-full" variant="secondary" onClick={printReceipt}>Print HTML Receipt</Button>
                  )}
                  <Button className="w-full" variant="secondary" onClick={() => {setActiveInvoice(null); setReceipt(null)}}>Close & Search Next</Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      )}

      {showPaymentDialog && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-[400px]">
            <CardHeader><CardTitle>Process Payment</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div>
                <label className="text-sm font-medium mb-1 block">Amount to Collect ($)</label>
                <Input 
                  type="number" 
                  value={paymentData.amount || (activeInvoice.netAmount - activeInvoice.amountPaid)} 
                  onChange={e => setPaymentData({...paymentData, amount: e.target.value})} 
                />
              </div>
              <div>
                <label className="text-sm font-medium mb-1 block">Payment Method</label>
                <select 
                  className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm outline-none focus:border-primary bg-white"
                  value={paymentData.paymentMethod}
                  onChange={e => setPaymentData({...paymentData, paymentMethod: e.target.value})}
                >
                  <option>CASH</option>
                  <option>CREDIT CARD</option>
                  <option>UPI</option>
                </select>
              </div>
              {paymentData.paymentMethod !== 'CASH' && (
                <div>
                  <label className="text-sm font-medium mb-1 block">Reference / Transaction ID</label>
                  <Input value={paymentData.referenceNumber} onChange={e => setPaymentData({...paymentData, referenceNumber: e.target.value})} />
                </div>
              )}
              <div className="flex justify-end gap-3 mt-6 border-t border-border pt-4">
                <Button type="button" variant="secondary" onClick={() => setShowPaymentDialog(false)}>Cancel</Button>
                <Button onClick={handlePayment} className="bg-success hover:bg-success-dark">Confirm Payment</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
