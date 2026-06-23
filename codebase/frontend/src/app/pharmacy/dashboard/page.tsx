"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";

export default function PharmacyDashboard() {
  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Pharmacy Command Center</h2>
          <p className="text-text-secondary text-sm">Stock tracking, FEFO alerts, and prescription volume.</p>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card className="bg-surface border-border">
          <CardContent className="p-6">
            <div className="text-text-secondary text-sm font-medium">Pending e-Prescriptions</div>
            <div className="text-3xl font-bold text-text-primary mt-2">12</div>
          </CardContent>
        </Card>
        <Card className="bg-surface border-border">
          <CardContent className="p-6">
            <div className="text-text-secondary text-sm font-medium">Total Inventory Value</div>
            <div className="text-3xl font-bold text-success-dark mt-2">₹14.2M</div>
          </CardContent>
        </Card>
        <Card className="bg-warning/10 border-warning">
          <CardContent className="p-6">
            <div className="text-warning-dark text-sm font-medium">Low Stock Alerts</div>
            <div className="text-3xl font-bold text-warning-dark mt-2">45</div>
          </CardContent>
        </Card>
        <Card className="bg-error/10 border-error">
          <CardContent className="p-6">
            <div className="text-error-dark text-sm font-medium">Expiring (&lt;30 Days)</div>
            <div className="text-3xl font-bold text-error-dark mt-2">8</div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-2 gap-6">
        <Card className="border-warning">
          <CardHeader className="bg-warning/5 border-b border-warning pb-3">
            <CardTitle className="text-warning-dark flex items-center">
              Reorder Requirements (Below Minimum Level)
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-4 font-medium text-text-secondary">Drug</th>
                  <th className="p-4 font-medium text-text-secondary text-right">Current Stock</th>
                  <th className="p-4 font-medium text-text-secondary text-right">Reorder Level</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                <tr className="hover:bg-warning/5">
                  <td className="p-4 font-semibold">Paracetamol 500mg Tablet</td>
                  <td className="p-4 text-right font-bold text-error">24</td>
                  <td className="p-4 text-right text-text-secondary">500</td>
                </tr>
                <tr className="hover:bg-warning/5">
                  <td className="p-4 font-semibold">Amoxicillin 250mg Capsule</td>
                  <td className="p-4 text-right font-bold text-error">12</td>
                  <td className="p-4 text-right text-text-secondary">200</td>
                </tr>
              </tbody>
            </table>
          </CardContent>
        </Card>

        <Card className="border-error">
          <CardHeader className="bg-error/5 border-b border-error pb-3">
            <CardTitle className="text-error-dark flex items-center">
              FEFO Action Required: Expiring Soon
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-4 font-medium text-text-secondary">Drug & Batch</th>
                  <th className="p-4 font-medium text-text-secondary text-right">Qty</th>
                  <th className="p-4 font-medium text-text-secondary text-right">Expiry Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                <tr className="hover:bg-error/5">
                  <td className="p-4">
                    <div className="font-semibold">Ibuprofen 400mg</div>
                    <div className="text-xs text-text-secondary font-mono mt-1">BATCH: B-2025-X01</div>
                  </td>
                  <td className="p-4 text-right">150</td>
                  <td className="p-4 text-right font-bold text-error-dark">12 Days</td>
                </tr>
              </tbody>
            </table>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
