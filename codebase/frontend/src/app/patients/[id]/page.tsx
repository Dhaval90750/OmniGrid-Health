"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function PatientProfile() {
  const { id } = useParams();
  const router = useRouter();
  const [patient, setPatient] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) {
      fetchPatientDetails();
    }
  }, [id]);

  const fetchPatientDetails = async () => {
    try {
      const response = await api.get(`/patients/${id}`);
      setPatient(response.data);
    } catch (error) {
      console.error("Failed to fetch patient", error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="p-8 text-text-secondary">Loading patient profile...</div>;
  if (!patient) return <div className="p-8 text-error">Patient not found.</div>;

  return (
    <div className="max-w-6xl space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button variant="secondary" onClick={() => router.push("/patients")}>← Back</Button>
          <h2 className="text-2xl font-semibold text-text-primary">Patient Profile</h2>
        </div>
        <div className="flex gap-2">
          <Button variant="secondary">Admit Patient</Button>
          <Button variant="primary">Start OPD Visit</Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Core Identity & QR */}
        <Card className="lg:col-span-1 border-primary/20 shadow-md">
          <CardContent className="p-6 flex flex-col items-center text-center">
            <div className="w-24 h-24 bg-primary-light text-primary rounded-full flex items-center justify-center text-3xl font-bold mb-4">
              {patient.firstName[0]}{patient.lastName[0]}
            </div>
            <h3 className="text-xl font-bold text-text-primary">{patient.firstName} {patient.lastName}</h3>
            <div className="text-sm text-text-secondary mb-2">{patient.gender} • {patient.dateOfBirth}</div>
            
            <div className="bg-surface px-4 py-2 rounded-md border border-border w-full flex justify-between items-center mb-6 mt-4">
              <span className="text-text-secondary text-sm font-medium">UHID</span>
              <span className="text-primary font-bold">{patient.uhid}</span>
            </div>

            {/* QR Code Display */}
            {patient.qrCodeBase64 && (
              <div className="border border-border p-2 rounded-lg bg-white inline-block">
                <img src={patient.qrCodeBase64} alt="Patient QR Code" className="w-40 h-40" />
                <p className="text-xs text-text-secondary mt-1">Scan for records</p>
              </div>
            )}
            
            <Button variant="secondary" className="w-full mt-6">Print Wristband</Button>
          </CardContent>
        </Card>

        {/* Details Sections */}
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <CardHeader><CardTitle>Demographics & Contact</CardTitle></CardHeader>
            <CardContent>
              <div className="grid grid-cols-2 md:grid-cols-3 gap-y-6 gap-x-4">
                <div>
                  <div className="text-xs text-text-secondary mb-1">Mobile Number</div>
                  <div className="font-medium text-text-primary">{patient.mobileNumber}</div>
                </div>
                <div>
                  <div className="text-xs text-text-secondary mb-1">Email</div>
                  <div className="font-medium text-text-primary">{patient.email || "—"}</div>
                </div>
                <div>
                  <div className="text-xs text-text-secondary mb-1">Blood Group</div>
                  <div className="font-medium text-text-primary">
                    {patient.bloodGroup ? <Badge variant="error">{patient.bloodGroup}</Badge> : "—"}
                  </div>
                </div>
                <div className="col-span-2 md:col-span-3">
                  <div className="text-xs text-text-secondary mb-1">Address</div>
                  <div className="font-medium text-text-primary">
                    {patient.addressLine1}, {patient.city}, {patient.state}
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex justify-between items-center flex-row">
              <CardTitle>Emergency Contact</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <div className="text-xs text-text-secondary mb-1">Name</div>
                  <div className="font-medium text-text-primary">{patient.emergencyContactName || "—"}</div>
                </div>
                <div>
                  <div className="text-xs text-text-secondary mb-1">Phone</div>
                  <div className="font-medium text-text-primary">{patient.emergencyContactPhone || "—"}</div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
