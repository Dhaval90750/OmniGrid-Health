"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";

export default function LabWorklist() {
  const [results, setResults] = useState([
    { id: "1", parameter: "Hemoglobin", value: "", unit: "g/dL", refLow: 13.5, refHigh: 17.5 },
    { id: "2", parameter: "WBC Count", value: "", unit: "x10^9/L", refLow: 4.5, refHigh: 11.0 },
    { id: "3", parameter: "Platelet Count", value: "", unit: "x10^9/L", refLow: 150, refHigh: 450 }
  ]);

  const handleValueChange = (id: string, val: string) => {
    setResults(results.map(r => r.id === id ? { ...r, value: val } : r));
  };

  const getFlag = (val: string, low: number, high: number) => {
    if (!val) return null;
    const num = parseFloat(val);
    if (isNaN(num)) return null;
    
    const range = high - low;
    const criticalMargin = range * 0.2;
    
    if (num < low - criticalMargin || num > high + criticalMargin) {
      return <Badge variant="error" className="ml-2">CRITICAL</Badge>;
    } else if (num < low || num > high) {
      return <Badge variant="warning" className="ml-2">ABNORMAL</Badge>;
    }
    return <Badge variant="success" className="ml-2">NORMAL</Badge>;
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Result Entry Worklist</h2>
          <p className="text-text-secondary text-sm">Rapid entry for CBC - Barcode LAB-2026-A1B2 (Patient: Alice Smith)</p>
        </div>
        <Button className="bg-primary hover:bg-primary-dark">Save & Authorize Results</Button>
      </div>

      <Card>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary w-1/4">Parameter</th>
                <th className="p-4 font-medium text-text-secondary w-1/4">Result Value</th>
                <th className="p-4 font-medium text-text-secondary">Unit</th>
                <th className="p-4 font-medium text-text-secondary">Reference Range</th>
                <th className="p-4 font-medium text-text-secondary">Status Flag</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {results.map((r) => (
                <tr key={r.id} className="hover:bg-surface-hover">
                  <td className="p-4 font-semibold">{r.parameter}</td>
                  <td className="p-4">
                    <Input 
                      type="number" 
                      className="w-32 h-9" 
                      value={r.value} 
                      onChange={e => handleValueChange(r.id, e.target.value)} 
                    />
                  </td>
                  <td className="p-4 text-text-secondary">{r.unit}</td>
                  <td className="p-4 text-text-secondary">{r.refLow} - {r.refHigh}</td>
                  <td className="p-4">
                    {getFlag(r.value, r.refLow, r.refHigh)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>
      
      <div className="bg-info-light/20 border border-info border-l-4 p-4 rounded text-sm text-info-dark flex items-start">
        <span className="font-bold mr-2">Delta Warning Engine Active:</span> 
        If these results deviate by &gt;25% from Alice Smith&apos;s last CBC, a Delta Warning will be generated upon saving.
      </div>
    </div>
  );
}
