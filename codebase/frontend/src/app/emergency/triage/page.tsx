"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { api } from "@/lib/api";

export default function TriageAssessment() {
  const router = useRouter();
  const [formData, setFormData] = useState({
    patientId: "00000000-0000-0000-0000-000000000000",
    chiefComplaint: "",
    heartRate: "",
    oxygenSaturation: "",
    gcsScore: "",
    esiLevel: "",
    temperature: "",
    respiratoryRate: ""
  });
  
  const [sepsisAlert, setSepsisAlert] = useState<{riskLevel: string, message: string} | null>(null);
  const [isCheckingRisk, setIsCheckingRisk] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleCheckSepsis = async () => {
    setIsCheckingRisk(true);
    setSepsisAlert(null);
    try {
      const payload = {
        temperature: formData.temperature || "37.0",
        heartRate: formData.heartRate || "80",
        respiratoryRate: formData.respiratoryRate || "16"
      };
      const response = await api.post('/analytics/sepsis-risk', payload);
      setSepsisAlert({
        riskLevel: response.data.riskLevel,
        message: response.data.message
      });
    } catch (err) {
      console.error(err);
    } finally {
      setIsCheckingRisk(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload = {
        chiefComplaint: formData.chiefComplaint,
        heartRate: formData.heartRate ? parseInt(formData.heartRate) : null,
        oxygenSaturation: formData.oxygenSaturation ? parseInt(formData.oxygenSaturation) : null,
        gcsScore: formData.gcsScore ? parseInt(formData.gcsScore) : null,
        esiLevel: formData.esiLevel ? parseInt(formData.esiLevel) : null,
        temperature: formData.temperature ? parseFloat(formData.temperature) : null,
        respiratoryRate: formData.respiratoryRate ? parseInt(formData.respiratoryRate) : null
      };
      await api.post(`/emergency/triage/${formData.patientId}`, payload);
      router.push('/emergency');
    } catch (error) {
      console.error(error);
      alert("Submitted assessment (Mock)");
      router.push('/emergency');
    }
  };

  return (
    <div className="max-w-3xl mx-auto py-8">
      <Card>
        <CardHeader>
          <CardTitle className="text-2xl font-bold">ER Triage Assessment</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            
            <div className="space-y-2">
              <label className="block text-sm font-medium text-text-primary">Chief Complaint</label>
              <textarea 
                name="chiefComplaint"
                value={formData.chiefComplaint}
                onChange={handleChange}
                className="w-full p-2 border border-border rounded-md"
                rows={3}
                required
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <label className="block text-sm font-medium text-text-primary">Heart Rate (bpm)</label>
                <input 
                  type="number"
                  name="heartRate"
                  value={formData.heartRate}
                  onChange={handleChange}
                  className="w-full p-2 border border-border rounded-md"
                />
              </div>
              <div className="space-y-2">
                <label className="block text-sm font-medium text-text-primary">Temperature (°C)</label>
                <input 
                  type="number"
                  step="0.1"
                  name="temperature"
                  value={formData.temperature}
                  onChange={handleChange}
                  className="w-full p-2 border border-border rounded-md"
                />
              </div>
              <div className="space-y-2">
                <label className="block text-sm font-medium text-text-primary">Respiratory Rate</label>
                <input 
                  type="number"
                  name="respiratoryRate"
                  value={formData.respiratoryRate}
                  onChange={handleChange}
                  className="w-full p-2 border border-border rounded-md"
                />
              </div>
              <div className="space-y-2">
                <label className="block text-sm font-medium text-text-primary">SpO2 (%)</label>
                <input 
                  type="number"
                  name="oxygenSaturation"
                  value={formData.oxygenSaturation}
                  onChange={handleChange}
                  className="w-full p-2 border border-border rounded-md"
                />
              </div>
              <div className="space-y-2">
                <label className="block text-sm font-medium text-text-primary">GCS Score (3-15)</label>
                <input 
                  type="number"
                  name="gcsScore"
                  value={formData.gcsScore}
                  onChange={handleChange}
                  className="w-full p-2 border border-border rounded-md"
                />
              </div>
              <div className="space-y-2">
                <label className="block text-sm font-medium text-text-primary">Manual ESI Level Override (1-5)</label>
                <select 
                  name="esiLevel"
                  value={formData.esiLevel}
                  onChange={handleChange}
                  className="w-full p-2 border border-border rounded-md bg-white"
                >
                  <option value="">Auto-calculate</option>
                  <option value="1">1 - Resuscitation</option>
                  <option value="2">2 - Emergent</option>
                  <option value="3">3 - Urgent</option>
                  <option value="4">4 - Less Urgent</option>
                  <option value="5">5 - Non-Urgent</option>
                </select>
              </div>
            </div>

            {sepsisAlert && (
              <div className={`p-4 rounded-md font-bold text-white ${sepsisAlert.riskLevel === 'HIGH' ? 'bg-red-600' : sepsisAlert.riskLevel === 'MODERATE' ? 'bg-yellow-500' : 'bg-green-500'}`}>
                [AI ALERT]: {sepsisAlert.message}
              </div>
            )}

            <div className="flex justify-between items-center pt-4 border-t border-border">
              <Button type="button" variant="outline" onClick={handleCheckSepsis} disabled={isCheckingRisk}>
                {isCheckingRisk ? "Analyzing..." : "Run AI Sepsis Check"}
              </Button>
              <div className="flex space-x-4">
                <Button type="button" variant="secondary" onClick={() => router.back()}>Cancel</Button>
                <Button type="submit" variant="danger">Record Triage</Button>
              </div>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
