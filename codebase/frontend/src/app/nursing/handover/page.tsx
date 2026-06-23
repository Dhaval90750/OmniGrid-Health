"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";

export default function ShiftHandover() {
  const [sbar, setSbar] = useState({ s: "", b: "", a: "", r: "" });

  const handleSave = () => {
    alert("SBAR Handover Saved.");
    setSbar({ s: "", b: "", a: "", r: "" });
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Shift Handover (SBAR)</h2>
          <p className="text-text-secondary text-sm">Structured clinical communication framework.</p>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Create Handover Note</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-6">
            <div>
              <label className="flex items-center gap-2 font-bold text-primary-dark mb-2">
                <span className="w-6 h-6 rounded bg-primary text-white flex items-center justify-center">S</span>
                Situation
              </label>
              <textarea 
                className="w-full h-20 p-3 border border-border rounded-md focus:border-primary outline-none resize-none text-sm"
                placeholder="What is the current situation? (e.g. Patient complaining of severe chest pain)"
                value={sbar.s} onChange={e => setSbar({...sbar, s: e.target.value})}
              />
            </div>
            
            <div>
              <label className="flex items-center gap-2 font-bold text-info-dark mb-2">
                <span className="w-6 h-6 rounded bg-info text-white flex items-center justify-center">B</span>
                Background
              </label>
              <textarea 
                className="w-full h-20 p-3 border border-border rounded-md focus:border-info outline-none resize-none text-sm"
                placeholder="What is the clinical background? (e.g. Admitted yesterday for observation, history of HTN)"
                value={sbar.b} onChange={e => setSbar({...sbar, b: e.target.value})}
              />
            </div>

            <div>
              <label className="flex items-center gap-2 font-bold text-warning-dark mb-2">
                <span className="w-6 h-6 rounded bg-warning text-white flex items-center justify-center">A</span>
                Assessment
              </label>
              <textarea 
                className="w-full h-20 p-3 border border-border rounded-md focus:border-warning outline-none resize-none text-sm"
                placeholder="What do I think the problem is? (e.g. Suspected acute MI, Trop-T elevated)"
                value={sbar.a} onChange={e => setSbar({...sbar, a: e.target.value})}
              />
            </div>

            <div>
              <label className="flex items-center gap-2 font-bold text-success-dark mb-2">
                <span className="w-6 h-6 rounded bg-success text-white flex items-center justify-center">R</span>
                Recommendation
              </label>
              <textarea 
                className="w-full h-20 p-3 border border-border rounded-md focus:border-success outline-none resize-none text-sm"
                placeholder="What should be done? (e.g. Cardiology consult stat, monitor ECG)"
                value={sbar.r} onChange={e => setSbar({...sbar, r: e.target.value})}
              />
            </div>

            <div className="flex justify-end pt-4 border-t border-border">
              <Button onClick={handleSave} variant="primary">Submit Handover</Button>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
