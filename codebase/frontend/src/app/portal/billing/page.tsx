"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";

export default function PatientBilling() {
  
  const bills = [
    { id: "INV-2026-9921", date: "Oct 12, 2026", desc: "Outpatient Consultation - Dr. Miller", amount: 150.00, status: "Paid" },
    { id: "INV-2026-9930", date: "Oct 15, 2026", desc: "Complete Blood Count (CBC)", amount: 45.00, status: "Unpaid" }
  ];

  return (
    <div className="max-w-4xl mx-auto py-8">
      <h1 className="text-3xl font-bold text-text-primary border-b border-border pb-4 mb-8">My Bills & Payments</h1>
      
      <Card>
        <CardHeader>
          <CardTitle>Invoices</CardTitle>
        </CardHeader>
        <CardContent className="p-0">
          <div className="divide-y divide-border">
            {bills.map(b => (
              <div key={b.id} className="p-4 flex justify-between items-center hover:bg-surface-hover">
                <div>
                  <div className="font-bold">{b.desc}</div>
                  <div className="text-sm text-text-secondary">{b.id} • {b.date}</div>
                </div>
                <div className="flex items-center gap-4">
                  <div className="font-bold text-lg">${b.amount.toFixed(2)}</div>
                  {b.status === "Paid" ? (
                    <span className="text-success font-bold text-sm bg-success/10 px-2 py-1 rounded">Paid</span>
                  ) : (
                    <Button variant="primary" size="sm">Pay Now</Button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
