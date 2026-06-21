"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function PatientDirectory() {
  const [patients, setPatients] = useState<any[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const router = useRouter();

  useEffect(() => {
    fetchPatients();
  }, []);

  const fetchPatients = async (query: string = "") => {
    try {
      const response = await api.get(`/patients/search?q=${query}`);
      setPatients(response.data);
    } catch (error) {
      console.error("Failed to fetch patients", error);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    fetchPatients(searchQuery);
  };

  return (
    <div className="max-w-6xl space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Patient Directory</h2>
          <p className="text-text-secondary text-sm">Search and manage registered patients.</p>
        </div>
        <Button onClick={() => router.push("/patients/new")}>+ Register New Patient</Button>
      </div>

      <Card>
        <CardHeader>
          <form onSubmit={handleSearch} className="flex gap-4">
            <div className="flex-1">
              <Input 
                placeholder="Search by UHID, Name, or Mobile Number..." 
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
            <Button type="submit" variant="secondary">Search</Button>
          </form>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-border bg-surface text-text-secondary text-sm">
                  <th className="p-4 font-medium rounded-tl-lg">UHID</th>
                  <th className="p-4 font-medium">Patient Name</th>
                  <th className="p-4 font-medium">Gender/Age</th>
                  <th className="p-4 font-medium">Mobile</th>
                  <th className="p-4 font-medium text-right rounded-tr-lg">Action</th>
                </tr>
              </thead>
              <tbody>
                {patients.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="p-8 text-center text-text-tertiary">
                      No patients found. Try a different search term or register a new patient.
                    </td>
                  </tr>
                ) : (
                  patients.map((p) => (
                    <tr key={p.id} className="border-b border-surface-hover hover:bg-surface-hover transition-colors">
                      <td className="p-4 font-medium text-primary">{p.uhid}</td>
                      <td className="p-4">{p.firstName} {p.lastName}</td>
                      <td className="p-4">{p.gender}</td>
                      <td className="p-4">{p.mobileNumber}</td>
                      <td className="p-4 text-right">
                        <Button variant="secondary" onClick={() => router.push(`/patients/${p.id}`)}>
                          View Profile
                        </Button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
