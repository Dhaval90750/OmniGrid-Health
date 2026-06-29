"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function PatientConsentDashboard() {
  const [consents, setConsents] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  
  // Mock Patient ID
  const patientId = "00000000-0000-0000-0000-000000000000";

  useEffect(() => {
    fetchConsents();
  }, []);

  const fetchConsents = async () => {
    try {
      const res = await api.get(`/consent/patient/${patientId}`);
      setConsents(res.data);
    } catch (e) {
      console.error(e);
      setConsents([
        { id: "1", consentType: "GENERAL_ADMISSION", signedBy: "John Doe", relationToPatient: "Self", signedAt: "2026-10-15T08:30:00", isRevoked: false },
        { id: "2", consentType: "SURGICAL_PROCEDURE", signedBy: "John Doe", relationToPatient: "Self", signedAt: "2026-10-16T14:15:00", isRevoked: false }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleSignConsent = async (type: string) => {
    try {
      const payload = {
        consentType: type,
        signedBy: "John Doe",
        relationToPatient: "Self",
        ipAddress: "192.168.1.1"
      };
      await api.post(`/consent/patient/${patientId}`, payload);
      alert("Consent signed successfully.");
      fetchConsents();
    } catch (error) {
      console.error(error);
      alert("Signed Mock Consent");
      fetchConsents();
    }
  };

  const handleRevokeConsent = async (id: string) => {
    if (!confirm("Are you sure you want to revoke this consent?")) return;
    try {
      await api.put(`/consent/${id}/revoke`);
      alert("Consent revoked.");
      fetchConsents();
    } catch (error) {
      console.error(error);
      alert("Revoked Mock Consent");
    }
  };

  return (
    <div className="max-w-5xl mx-auto py-8 space-y-8">
      <h1 className="text-3xl font-bold text-text-primary border-b border-border pb-4">Patient Consents (e-Sign)</h1>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Pending Actions */}
        <Card className="border-warning border-l-4">
          <CardHeader>
            <CardTitle>Required Consents</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex justify-between items-center p-4 bg-surface rounded-md border border-border">
              <div>
                <div className="font-bold">Blood Transfusion Consent</div>
                <div className="text-sm text-text-secondary">Required for upcoming surgery.</div>
              </div>
              <Button variant="primary" onClick={() => handleSignConsent("BLOOD_TRANSFUSION")}>Review & Sign</Button>
            </div>
          </CardContent>
        </Card>

        {/* Consent History */}
        <Card>
          <CardHeader>
            <CardTitle>Consent History</CardTitle>
          </CardHeader>
          <CardContent>
            {loading ? (
              <div>Loading...</div>
            ) : (
              <div className="space-y-4">
                {consents.map(c => (
                  <div key={c.id} className={`flex justify-between items-center p-4 border rounded-md shadow-sm ${c.isRevoked ? 'bg-surface opacity-60' : 'bg-white'}`}>
                    <div>
                      <div className="font-bold text-lg">{c.consentType.replace('_', ' ')}</div>
                      <div className="text-sm text-text-secondary">
                        Signed by {c.signedBy} ({c.relationToPatient}) on {new Date(c.signedAt).toLocaleDateString()}
                      </div>
                    </div>
                    <div className="flex items-center gap-4">
                      {c.isRevoked ? (
                        <Badge variant="error">Revoked</Badge>
                      ) : (
                        <>
                          <Badge variant="success">Active</Badge>
                          <Button variant="danger" size="sm" onClick={() => handleRevokeConsent(c.id)}>Revoke</Button>
                        </>
                      )}
                    </div>
                  </div>
                ))}
                {consents.length === 0 && <div className="text-sm text-text-secondary">No consents found.</div>}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
