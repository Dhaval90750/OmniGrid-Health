"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";

export default function IcuDashboard() {
  const beds = [
    { bed: "ICU-1", patient: "Alice Smith (UHID-1002)", apache: 18, vent: "SIMV (Volume)", map: 65, status: "Critical" },
    { bed: "ICU-2", patient: "John Doe (UHID-5401)", apache: 12, vent: "CPAP", map: 75, status: "Stable" },
    { bed: "ICU-3", patient: "Empty", apache: null, vent: null, map: null, status: "Available" },
    { bed: "ICU-4", patient: "Robert King (UHID-7721)", apache: 24, vent: "PRVC", map: 58, status: "Critical" }
  ];

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">ICU Command Center</h2>
          <p className="text-text-secondary text-sm">Real-time telemetry and severity scoring.</p>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4">
        {beds.map(b => (
          <Card key={b.bed} className={b.status === 'Critical' ? 'border-error bg-error/5' : b.status === 'Available' ? 'border-dashed border-border' : 'border-success'}>
            <CardHeader className="pb-2">
              <CardTitle className="text-lg flex justify-between items-center">
                {b.bed}
                {b.status === 'Critical' && <Badge variant="error" className="animate-pulse">CRITICAL</Badge>}
                {b.status === 'Stable' && <Badge variant="success">STABLE</Badge>}
                {b.status === 'Available' && <Badge variant="default">EMPTY</Badge>}
              </CardTitle>
            </CardHeader>
            <CardContent>
              {b.status !== 'Available' ? (
                <div className="space-y-3">
                  <div className="font-semibold text-text-primary">{b.patient}</div>
                  
                  <div className="grid grid-cols-2 gap-2 text-sm">
                    <div className="bg-surface p-2 rounded">
                      <div className="text-xs text-text-secondary">APACHE II</div>
                      <div className="font-bold text-lg">{b.apache}</div>
                    </div>
                    <div className="bg-surface p-2 rounded">
                      <div className="text-xs text-text-secondary">MAP</div>
                      <div className={`font-bold text-lg ${b.map && b.map < 65 ? 'text-error' : ''}`}>{b.map}</div>
                    </div>
                  </div>
                  
                  <div className="bg-surface p-2 rounded text-sm flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-2 text-info" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8.111 16.404a5.5 5.5 0 017.778 0M12 20h.01m-7.08-7.071c3.904-3.905 10.236-3.905 14.141 0M1.394 9.393c5.857-5.857 15.355-5.857 21.213 0" /></svg>
                    Vent: <span className="font-semibold ml-1">{b.vent}</span>
                  </div>

                  <Button size="sm" variant="secondary" className="w-full mt-2">Open Flowsheet</Button>
                </div>
              ) : (
                <div className="h-32 flex items-center justify-center text-text-secondary">
                  Ready for admission
                </div>
              )}
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
