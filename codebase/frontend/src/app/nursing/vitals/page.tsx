"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";

export default function VitalsAndNews() {
  const [vitals, setVitals] = useState({
    rr: "", spo2: "", temp: "", sbp: "", hr: "", pain: ""
  });
  
  const [newsScore, setNewsScore] = useState<number | null>(null);

  const calculateNews = (e: React.FormEvent) => {
    e.preventDefault();
    // Front-end mock calculation for immediate feedback
    let score = 0;
    const rr = parseInt(vitals.rr);
    if (!isNaN(rr)) {
      if (rr <= 8 || rr >= 25) score += 3;
      else if (rr >= 21 || rr <= 11) score += 1;
    }
    const sbp = parseInt(vitals.sbp);
    if (!isNaN(sbp)) {
      if (sbp <= 90 || sbp >= 220) score += 3;
      else if (sbp <= 100) score += 2;
      else if (sbp <= 110) score += 1;
    }
    const hr = parseInt(vitals.hr);
    if (!isNaN(hr)) {
      if (hr <= 40 || hr >= 131) score += 3;
      else if (hr >= 111) score += 2;
      else if (hr <= 50 || hr >= 91) score += 1;
    }
    setNewsScore(score);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Patient Vitals & NEWS Score</h2>
          <p className="text-text-secondary text-sm">National Early Warning Score Auto-calculation.</p>
        </div>
      </div>

      <div className="grid grid-cols-3 gap-6">
        <div className="col-span-2">
          <Card>
            <CardHeader>
              <CardTitle>Record Vitals</CardTitle>
            </CardHeader>
            <CardContent>
              <form onSubmit={calculateNews} className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="text-xs font-medium text-text-secondary mb-1 block">Heart Rate (BPM)</label>
                    <Input type="number" required value={vitals.hr} onChange={e => setVitals({...vitals, hr: e.target.value})} />
                  </div>
                  <div>
                    <label className="text-xs font-medium text-text-secondary mb-1 block">Systolic BP (mmHg)</label>
                    <Input type="number" required value={vitals.sbp} onChange={e => setVitals({...vitals, sbp: e.target.value})} />
                  </div>
                  <div>
                    <label className="text-xs font-medium text-text-secondary mb-1 block">Respiratory Rate (Resp/Min)</label>
                    <Input type="number" required value={vitals.rr} onChange={e => setVitals({...vitals, rr: e.target.value})} />
                  </div>
                  <div>
                    <label className="text-xs font-medium text-text-secondary mb-1 block">SpO2 (%)</label>
                    <Input type="number" required value={vitals.spo2} onChange={e => setVitals({...vitals, spo2: e.target.value})} />
                  </div>
                  <div>
                    <label className="text-xs font-medium text-text-secondary mb-1 block">Temperature (°C)</label>
                    <Input type="number" step="0.1" required value={vitals.temp} onChange={e => setVitals({...vitals, temp: e.target.value})} />
                  </div>
                  <div>
                    <label className="text-xs font-medium text-text-secondary mb-1 block">Pain Score (0-10)</label>
                    <Input type="number" value={vitals.pain} onChange={e => setVitals({...vitals, pain: e.target.value})} />
                  </div>
                </div>
                <div className="flex justify-end mt-4">
                  <Button type="submit" variant="primary">Calculate & Save</Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>

        <div className="col-span-1 space-y-6">
          <Card className={`${newsScore !== null && newsScore >= 7 ? 'border-error bg-error/10' : newsScore !== null && newsScore >= 5 ? 'border-warning bg-warning/10' : 'border-success bg-success/10'}`}>
            <CardContent className="p-6 text-center">
              <h3 className="text-text-secondary text-sm font-medium mb-2">Calculated NEWS Score</h3>
              <div className={`text-6xl font-bold ${newsScore !== null && newsScore >= 7 ? 'text-error-dark' : newsScore !== null && newsScore >= 5 ? 'text-warning-dark' : 'text-success-dark'}`}>
                {newsScore !== null ? newsScore : "--"}
              </div>
              <div className="mt-4 text-sm font-medium">
                {newsScore === null ? "Awaiting Input" : newsScore >= 7 ? "High Clinical Risk (Call RRT)" : newsScore >= 5 ? "Medium Risk (Ward Review)" : "Low Risk (Routine)"}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
