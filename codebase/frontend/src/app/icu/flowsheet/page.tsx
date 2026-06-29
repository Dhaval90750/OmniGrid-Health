"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";

export default function IcuFlowsheet() {
  const [showApache, setShowApache] = useState(false);
  const [apacheInputs, setApacheInputs] = useState({ temp: 37, map: 85, hr: 80, rr: 16, gcs: 15, age: 45 });
  const [apacheScore, setApacheScore] = useState<number | null>(null);

  const [hours, setHours] = useState(["08:00", "09:00", "10:00", "11:00", "12:00"]);
  const [vitals, setVitals] = useState({
    hr: ["115", "118", "120", "105", "---"],
    map: ["65", "62", "58", "60", "---"],
    fio2: ["60", "60", "70", "70", "---"],
    peep: ["8", "8", "10", "10", "---"],
    norad: ["5", "5", "10", "10", "---"]
  });

  const handleAddColumn = () => {
    const now = new Date();
    const timeStr = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
    setHours([...hours, timeStr]);
    setVitals({
      hr: [...vitals.hr, "---"],
      map: [...vitals.map, "---"],
      fio2: [...vitals.fio2, "---"],
      peep: [...vitals.peep, "---"],
      norad: [...vitals.norad, "---"]
    });
  };

  const calculateApache = (e: React.FormEvent) => {
    e.preventDefault();
    // Simplified APACHE II logic
    let score = 0;
    if (apacheInputs.temp > 39 || apacheInputs.temp < 36) score += 4;
    if (apacheInputs.map < 70) score += 2;
    if (apacheInputs.hr > 110) score += 2;
    if (apacheInputs.rr > 25) score += 2;
    score += (15 - apacheInputs.gcs); // GCS points
    if (apacheInputs.age > 44) score += 2;
    
    setApacheScore(score);
  };

  return (
    <div className="max-w-[1400px] mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">ICU Flowsheet</h2>
          <p className="text-text-secondary text-sm">Patient: Robert King (UHID-7721) | Bed: ICU-4</p>
        </div>
        <div className="flex gap-2">
          <Button variant="secondary" onClick={() => setShowApache(true)}>Calculate APACHE II</Button>
          <Button variant="primary" onClick={handleAddColumn}>Add Column (Now)</Button>
        </div>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-sm border-collapse border border-border">
          <thead>
            <tr className="bg-surface">
              <th className="border border-border p-2 w-48 text-left">Parameter</th>
              {hours.map(h => (
                <th key={h} className="border border-border p-2 text-center w-24">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            <tr className="bg-primary/5 font-semibold">
              <td colSpan={hours.length + 1} className="p-2 border border-border">Vitals</td>
            </tr>
            <tr>
              <td className="border border-border p-2 font-medium">Heart Rate</td>
              {vitals.hr.map((v, i) => (
                <td key={i} className="border border-border p-2 text-center">
                  <input className="w-full text-center outline-none bg-transparent" value={v} onChange={(e) => {
                    const newHr = [...vitals.hr]; newHr[i] = e.target.value; setVitals({...vitals, hr: newHr});
                  }} />
                </td>
              ))}
            </tr>
            <tr>
              <td className="border border-border p-2 font-medium">MAP</td>
              {vitals.map.map((v, i) => (
                <td key={i} className="border border-border p-2 text-center">
                  <input className="w-full text-center outline-none bg-transparent" value={v} onChange={(e) => {
                    const newMap = [...vitals.map]; newMap[i] = e.target.value; setVitals({...vitals, map: newMap});
                  }} />
                </td>
              ))}
            </tr>
            
            <tr className="bg-primary/5 font-semibold">
              <td colSpan={hours.length + 1} className="p-2 border border-border mt-4">Ventilator (PRVC)</td>
            </tr>
            <tr>
              <td className="border border-border p-2 font-medium">FiO2 (%)</td>
              {vitals.fio2.map((v, i) => (
                <td key={i} className="border border-border p-2 text-center">
                  <input className="w-full text-center outline-none bg-transparent" value={v} onChange={(e) => {
                    const newFio2 = [...vitals.fio2]; newFio2[i] = e.target.value; setVitals({...vitals, fio2: newFio2});
                  }} />
                </td>
              ))}
            </tr>
            <tr>
              <td className="border border-border p-2 font-medium">PEEP (cmH2O)</td>
              {vitals.peep.map((v, i) => (
                <td key={i} className="border border-border p-2 text-center">
                  <input className="w-full text-center outline-none bg-transparent" value={v} onChange={(e) => {
                    const newPeep = [...vitals.peep]; newPeep[i] = e.target.value; setVitals({...vitals, peep: newPeep});
                  }} />
                </td>
              ))}
            </tr>

            <tr className="bg-primary/5 font-semibold">
              <td colSpan={hours.length + 1} className="p-2 border border-border mt-4">Infusions (ml/hr)</td>
            </tr>
            <tr>
              <td className="border border-border p-2 font-medium">Noradrenaline</td>
              {vitals.norad.map((v, i) => (
                <td key={i} className="border border-border p-2 text-center">
                  <input className="w-full text-center outline-none bg-transparent" value={v} onChange={(e) => {
                    const newNorad = [...vitals.norad]; newNorad[i] = e.target.value; setVitals({...vitals, norad: newNorad});
                  }} />
                </td>
              ))}
            </tr>
          </tbody>
        </table>
      </div>

      {showApache && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-[500px]">
            <CardHeader><CardTitle>APACHE II Score Calculator</CardTitle></CardHeader>
            <CardContent>
              <form onSubmit={calculateApache} className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="text-sm font-medium mb-1 block">Temperature (°C)</label>
                    <Input type="number" step="0.1" value={apacheInputs.temp} onChange={e => setApacheInputs({...apacheInputs, temp: parseFloat(e.target.value)})} />
                  </div>
                  <div>
                    <label className="text-sm font-medium mb-1 block">MAP (mmHg)</label>
                    <Input type="number" value={apacheInputs.map} onChange={e => setApacheInputs({...apacheInputs, map: parseInt(e.target.value)})} />
                  </div>
                  <div>
                    <label className="text-sm font-medium mb-1 block">Heart Rate</label>
                    <Input type="number" value={apacheInputs.hr} onChange={e => setApacheInputs({...apacheInputs, hr: parseInt(e.target.value)})} />
                  </div>
                  <div>
                    <label className="text-sm font-medium mb-1 block">Respiratory Rate</label>
                    <Input type="number" value={apacheInputs.rr} onChange={e => setApacheInputs({...apacheInputs, rr: parseInt(e.target.value)})} />
                  </div>
                  <div>
                    <label className="text-sm font-medium mb-1 block">GCS (3-15)</label>
                    <Input type="number" min="3" max="15" value={apacheInputs.gcs} onChange={e => setApacheInputs({...apacheInputs, gcs: parseInt(e.target.value)})} />
                  </div>
                  <div>
                    <label className="text-sm font-medium mb-1 block">Age</label>
                    <Input type="number" value={apacheInputs.age} onChange={e => setApacheInputs({...apacheInputs, age: parseInt(e.target.value)})} />
                  </div>
                </div>

                {apacheScore !== null && (
                  <div className="mt-4 p-4 bg-info/10 border border-info rounded-md text-center">
                    <div className="text-sm text-text-secondary">Estimated APACHE II Score</div>
                    <div className="text-3xl font-bold text-info-dark">{apacheScore}</div>
                  </div>
                )}

                <div className="flex justify-end gap-2 pt-4">
                  <Button type="button" variant="secondary" onClick={() => setShowApache(false)}>Close</Button>
                  <Button type="submit" variant="primary">Calculate</Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
