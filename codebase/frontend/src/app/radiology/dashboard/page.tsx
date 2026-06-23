"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";

export default function RadiologyDashboard() {
  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Radiology Command Center</h2>
          <p className="text-text-secondary text-sm">Imaging volume metrics and critical findings queue.</p>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card className="bg-surface border-border">
          <CardContent className="p-6">
            <div className="text-text-secondary text-sm font-medium">Pending Studies</div>
            <div className="text-3xl font-bold text-text-primary mt-2">18</div>
          </CardContent>
        </Card>
        <Card className="bg-surface border-border">
          <CardContent className="p-6">
            <div className="text-text-secondary text-sm font-medium">Completed (Unread)</div>
            <div className="text-3xl font-bold text-text-primary mt-2">14</div>
          </CardContent>
        </Card>
        <Card className="bg-warning/10 border-warning">
          <CardContent className="p-6">
            <div className="text-warning-dark text-sm font-medium">STAT Orders Active</div>
            <div className="text-3xl font-bold text-warning-dark mt-2">5</div>
          </CardContent>
        </Card>
        <Card className="bg-error/10 border-error">
          <CardContent className="p-6">
            <div className="text-error-dark text-sm font-medium">Critical Findings</div>
            <div className="text-3xl font-bold text-error-dark mt-2">1</div>
          </CardContent>
        </Card>
      </div>

      <Card className="border-error">
        <CardHeader className="bg-error/5 border-b border-error pb-3">
          <CardTitle className="text-error-dark flex items-center">
            <span className="w-2 h-2 rounded-full bg-error animate-pulse mr-2"></span>
            Critical Finding Alerts (Action Required)
          </CardTitle>
        </CardHeader>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">Patient</th>
                <th className="p-4 font-medium text-text-secondary">Modality / Study</th>
                <th className="p-4 font-medium text-text-secondary">Reported By</th>
                <th className="p-4 font-medium text-text-secondary">Summary</th>
                <th className="p-4 font-medium text-text-secondary text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              <tr className="hover:bg-error/5">
                <td className="p-4 font-semibold">David Chen (UHID-7721)</td>
                <td className="p-4">CT - Head w/o Contrast</td>
                <td className="p-4">Dr. Sarah Radiology</td>
                <td className="p-4 font-medium text-error-dark">Acute subdural hematoma</td>
                <td className="p-4 text-right space-x-2">
                  <Button variant="danger" size="sm">Acknowledge</Button>
                  <Button variant="secondary" size="sm">View Report</Button>
                </td>
              </tr>
            </tbody>
          </table>
        </CardContent>
      </Card>
    </div>
  );
}
