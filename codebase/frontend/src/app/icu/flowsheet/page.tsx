"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";

export default function IcuFlowsheet() {
  const hours = ["08:00", "09:00", "10:00", "11:00", "12:00"];

  return (
    <div className="max-w-[1400px] mx-auto space-y-6">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">ICU Flowsheet</h2>
          <p className="text-text-secondary text-sm">Patient: Robert King (UHID-7721) | Bed: ICU-4</p>
        </div>
        <div className="flex gap-2">
          <Button variant="secondary">Calculate APACHE II</Button>
          <Button variant="primary">Add Column (Now)</Button>
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
              <td className="border border-border p-2 text-center text-error">115</td>
              <td className="border border-border p-2 text-center text-error">118</td>
              <td className="border border-border p-2 text-center text-error">120</td>
              <td className="border border-border p-2 text-center">105</td>
              <td className="border border-border p-2 text-center bg-surface-hover">
                <input className="w-full text-center outline-none bg-transparent" placeholder="---" />
              </td>
            </tr>
            <tr>
              <td className="border border-border p-2 font-medium">MAP</td>
              <td className="border border-border p-2 text-center">65</td>
              <td className="border border-border p-2 text-center">62</td>
              <td className="border border-border p-2 text-center text-error font-bold">58</td>
              <td className="border border-border p-2 text-center">60</td>
              <td className="border border-border p-2 text-center bg-surface-hover">
                <input className="w-full text-center outline-none bg-transparent" placeholder="---" />
              </td>
            </tr>
            
            <tr className="bg-primary/5 font-semibold">
              <td colSpan={hours.length + 1} className="p-2 border border-border mt-4">Ventilator (PRVC)</td>
            </tr>
            <tr>
              <td className="border border-border p-2 font-medium">FiO2 (%)</td>
              <td className="border border-border p-2 text-center">60</td>
              <td className="border border-border p-2 text-center">60</td>
              <td className="border border-border p-2 text-center">70</td>
              <td className="border border-border p-2 text-center">70</td>
              <td className="border border-border p-2 text-center bg-surface-hover">
                <input className="w-full text-center outline-none bg-transparent" placeholder="---" />
              </td>
            </tr>
            <tr>
              <td className="border border-border p-2 font-medium">PEEP (cmH2O)</td>
              <td className="border border-border p-2 text-center">8</td>
              <td className="border border-border p-2 text-center">8</td>
              <td className="border border-border p-2 text-center">10</td>
              <td className="border border-border p-2 text-center">10</td>
              <td className="border border-border p-2 text-center bg-surface-hover">
                <input className="w-full text-center outline-none bg-transparent" placeholder="---" />
              </td>
            </tr>

            <tr className="bg-primary/5 font-semibold">
              <td colSpan={hours.length + 1} className="p-2 border border-border mt-4">Infusions (ml/hr)</td>
            </tr>
            <tr>
              <td className="border border-border p-2 font-medium">Noradrenaline</td>
              <td className="border border-border p-2 text-center">5</td>
              <td className="border border-border p-2 text-center">5</td>
              <td className="border border-border p-2 text-center font-bold">10</td>
              <td className="border border-border p-2 text-center">10</td>
              <td className="border border-border p-2 text-center bg-surface-hover">
                <input className="w-full text-center outline-none bg-transparent" placeholder="---" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
}
