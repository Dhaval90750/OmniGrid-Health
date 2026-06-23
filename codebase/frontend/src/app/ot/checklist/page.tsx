"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";

export default function WhoChecklist() {
  const [checked, setChecked] = useState({
    patientConfirmed: false,
    siteMarked: false,
    anesthesiaSafety: false,
    pulseOximeter: false,
    allergies: false,
    airwayRisk: false,
    bloodLossRisk: false
  });

  const allChecked = Object.values(checked).every(v => v);

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">WHO Safe Surgery Checklist</h2>
          <p className="text-text-secondary text-sm">Booking: OTB-402 | Patient: Sarah Miller</p>
        </div>
      </div>

      <Card className="border-info">
        <CardHeader className="bg-info/10 border-b border-info pb-3">
          <CardTitle className="text-info-dark flex items-center gap-2">
            <span className="bg-info text-white rounded-full w-6 h-6 flex items-center justify-center text-sm">1</span>
            Sign In (Before induction of anesthesia)
          </CardTitle>
        </CardHeader>
        <CardContent className="p-6 space-y-4">
          <label className="flex items-center gap-3">
            <input type="checkbox" className="w-5 h-5 accent-info" checked={checked.patientConfirmed} onChange={e => setChecked({...checked, patientConfirmed: e.target.checked})} />
            <span>Has the patient confirmed their identity, site, procedure, and consent?</span>
          </label>
          <label className="flex items-center gap-3">
            <input type="checkbox" className="w-5 h-5 accent-info" checked={checked.siteMarked} onChange={e => setChecked({...checked, siteMarked: e.target.checked})} />
            <span>Is the surgical site marked?</span>
          </label>
          <label className="flex items-center gap-3">
            <input type="checkbox" className="w-5 h-5 accent-info" checked={checked.anesthesiaSafety} onChange={e => setChecked({...checked, anesthesiaSafety: e.target.checked})} />
            <span>Is the anesthesia machine and medication check complete?</span>
          </label>
          <label className="flex items-center gap-3">
            <input type="checkbox" className="w-5 h-5 accent-info" checked={checked.pulseOximeter} onChange={e => setChecked({...checked, pulseOximeter: e.target.checked})} />
            <span>Is the pulse oximeter on the patient and functioning?</span>
          </label>

          <div className="pt-4 border-t border-border mt-4">
            <div className="font-semibold mb-3 text-text-primary">Does the patient have a:</div>
            <label className="flex items-center gap-3 mb-2">
              <input type="checkbox" className="w-5 h-5 accent-info" checked={checked.allergies} onChange={e => setChecked({...checked, allergies: e.target.checked})} />
              <span>Known Allergy?</span>
            </label>
            <label className="flex items-center gap-3 mb-2">
              <input type="checkbox" className="w-5 h-5 accent-info" checked={checked.airwayRisk} onChange={e => setChecked({...checked, airwayRisk: e.target.checked})} />
              <span>Difficult airway or aspiration risk?</span>
            </label>
            <label className="flex items-center gap-3">
              <input type="checkbox" className="w-5 h-5 accent-info" checked={checked.bloodLossRisk} onChange={e => setChecked({...checked, bloodLossRisk: e.target.checked})} />
              <span>Risk of >500ml blood loss?</span>
            </label>
          </div>

          <div className="flex justify-end pt-4 mt-4">
            <Button variant={allChecked ? "success" : "secondary"} disabled={!allChecked}>Complete Sign-In</Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
