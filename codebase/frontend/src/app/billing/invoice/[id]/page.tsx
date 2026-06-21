"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";

export default function InvoicePayment() {
  const router = useRouter();
  const [paymentAmount, setPaymentAmount] = useState("150.00");
  const [paymentMethod, setPaymentMethod] = useState("CASH");

  const handlePay = (e: React.FormEvent) => {
    e.preventDefault();
    alert(`Payment of $${paymentAmount} via ${paymentMethod} collected successfully!`);
    router.push("/billing");
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="secondary" onClick={() => router.push("/billing")}>← Back to Billing</Button>
        <h2 className="text-2xl font-semibold text-text-primary">Invoice #INV-2606-001</h2>
        <Badge variant="warning">Pending</Badge>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Invoice Details */}
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <CardHeader className="flex justify-between items-center border-b pb-4">
              <div>
                <CardTitle>OmniGrid Health System</CardTitle>
                <div className="text-sm text-text-secondary mt-1">123 Health Ave, Medical District</div>
              </div>
              <div className="text-right">
                <div className="font-bold">Rahul Sharma (34M)</div>
                <div className="text-sm text-text-secondary">UHID: MED-2026-000001</div>
              </div>
            </CardHeader>
            <CardContent className="pt-6">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-border text-sm text-text-secondary">
                    <th className="py-2">Description</th>
                    <th className="py-2 text-center">Qty</th>
                    <th className="py-2 text-right">Unit Price</th>
                    <th className="py-2 text-right">Total</th>
                  </tr>
                </thead>
                <tbody className="text-sm">
                  <tr className="border-b border-surface-hover">
                    <td className="py-4">Cardiology Consultation (Dr. Smith)</td>
                    <td className="py-4 text-center">1</td>
                    <td className="py-4 text-right">$100.00</td>
                    <td className="py-4 text-right font-medium">$100.00</td>
                  </tr>
                  <tr className="border-b border-surface-hover">
                    <td className="py-4">ECG Test</td>
                    <td className="py-4 text-center">1</td>
                    <td className="py-4 text-right">$50.00</td>
                    <td className="py-4 text-right font-medium">$50.00</td>
                  </tr>
                </tbody>
              </table>

              <div className="mt-8 flex justify-end">
                <div className="w-64 space-y-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-text-secondary">Subtotal</span>
                    <span>$150.00</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-text-secondary">Discount</span>
                    <span>$0.00</span>
                  </div>
                  <div className="flex justify-between pt-2 border-t font-bold text-lg">
                    <span>Net Amount</span>
                    <span>$150.00</span>
                  </div>
                  <div className="flex justify-between pt-2 text-success font-medium">
                    <span>Amount Paid</span>
                    <span>$0.00</span>
                  </div>
                  <div className="flex justify-between pt-2 text-error font-bold">
                    <span>Balance Due</span>
                    <span>$150.00</span>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Payment Collection Widget */}
        <div className="lg:col-span-1">
          <Card className="sticky top-6">
            <CardHeader className="bg-surface"><CardTitle>Collect Payment</CardTitle></CardHeader>
            <CardContent className="pt-6">
              <form onSubmit={handlePay} className="space-y-4">
                <Input 
                  label="Amount to Collect ($)" 
                  type="number" 
                  value={paymentAmount}
                  onChange={(e) => setPaymentAmount(e.target.value)}
                  required 
                />
                
                <div className="flex flex-col gap-1 w-full">
                  <label className="text-xs font-medium text-text-secondary">Payment Method *</label>
                  <select 
                    className="bg-background border border-border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none focus:border-primary" 
                    value={paymentMethod}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                    required
                  >
                    <option value="CASH">Cash</option>
                    <option value="CARD">Credit/Debit Card</option>
                    <option value="UPI">UPI / Digital Wallet</option>
                    <option value="INSURANCE">Insurance TPA</option>
                  </select>
                </div>

                {paymentMethod !== "CASH" && (
                  <Input label="Transaction Reference No." placeholder="Enter Ref ID" />
                )}

                <Button type="submit" variant="primary" className="w-full mt-4">
                  Confirm Payment
                </Button>
              </form>
            </CardContent>
          </Card>
        </div>

      </div>
    </div>
  );
}
