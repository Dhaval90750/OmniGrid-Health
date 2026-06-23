"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";

export default function LabDashboard() {
  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Laboratory Command Center</h2>
          <p className="text-text-secondary text-sm">Live volume tracking and critical alerts.</p>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card className="bg-surface border-border">
          <CardContent className="p-6">
            <div className="text-text-secondary text-sm font-medium">Pending Collection</div>
            <div className="text-3xl font-bold text-text-primary mt-2">24</div>
          </CardContent>
        </Card>
        <Card className="bg-surface border-border">
          <CardContent className="p-6">
            <div className="text-text-secondary text-sm font-medium">Awaiting Reception</div>
            <div className="text-3xl font-bold text-text-primary mt-2">12</div>
          </CardContent>
        </Card>
        <Card className="bg-warning/10 border-warning">
          <CardContent className="p-6">
            <div className="text-warning-dark text-sm font-medium">TAT Breached</div>
            <div className="text-3xl font-bold text-warning-dark mt-2">3</div>
          </CardContent>
        </Card>
        <Card className="bg-error/10 border-error">
          <CardContent className="p-6">
            <div className="text-error-dark text-sm font-medium">Critical Values</div>
            <div className="text-3xl font-bold text-error-dark mt-2">2</div>
          </CardContent>
        </Card>
      </div>

      <Card className="border-error">
        <CardHeader className="bg-error/5 border-b border-error pb-3">
          <CardTitle className="text-error-dark flex items-center">
            <span className="w-2 h-2 rounded-full bg-error animate-pulse mr-2"></span>
            Critical Value Action Required
          </CardTitle>
        </CardHeader>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">Patient</th>
                <th className="p-4 font-medium text-text-secondary">Test / Parameter</th>
                <th className="p-4 font-medium text-text-secondary">Result</th>
                <th className="p-4 font-medium text-text-secondary">Ref Range</th>
                <th className="p-4 font-medium text-text-secondary text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              <tr className="hover:bg-error/5">
                <td className="p-4 font-semibold">Robert King (UHID-4022)</td>
                <td className="p-4">Potassium (Serum)</td>
                <td className="p-4 font-bold text-error-dark">6.8 mmol/L</td>
                <td className="p-4 text-text-secondary">3.5 - 5.1</td>
                <td className="p-4 text-right">
                  <Button variant="danger" size="sm">Notify Doctor</Button>
                </td>
              </tr>
              <tr className="hover:bg-error/5">
                <td className="p-4 font-semibold">Jane Doe (UHID-1055)</td>
                <td className="p-4">Hemoglobin</td>
                <td className="p-4 font-bold text-error-dark">6.2 g/dL</td>
                <td className="p-4 text-text-secondary">12.0 - 15.5</td>
                <td className="p-4 text-right">
                  <Button variant="danger" size="sm">Notify Doctor</Button>
                </td>
              </tr>
            </tbody>
          </table>
        </CardContent>
      </Card>
    </div>
  );
}
