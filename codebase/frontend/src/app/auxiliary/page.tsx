"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

export default function AuxiliaryDashboard() {
  const [activeTab, setActiveTab] = useState("blood");

  const [bloodInventory, setBloodInventory] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('http://localhost:8080/api/v1/auxiliary/blood-bank')
      .then(res => res.json())
      .then(data => {
        setBloodInventory(data);
        setLoading(false);
      })
      .catch(err => {
        console.error(err);
        setLoading(false);
      });
  }, []);

  const infections = [
    { id: "INF-2001", patient: "Rahul Sharma", ward: "ICU Bed 04", type: "VAP (Ventilator-Associated Pneumonia)", date: "2026-06-19", status: "Active" },
    { id: "INF-2002", patient: "Sneha Patel", ward: "Ward B", type: "SSI (Surgical Site Infection)", date: "2026-06-20", status: "Active" }
  ];

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Auxiliary Clinical Modules</h1>
          <p className="text-text-secondary text-sm">Manage Blood Bank, Infection Control, Dietary, and Medical Records (MRD)</p>
        </div>
      </div>

      <div className="flex gap-4 border-b border-border">
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'blood' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('blood')}
        >
          Blood Bank
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'infection' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('infection')}
        >
          Infection Control
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'diet' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('diet')}
        >
          Dietary
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'mrd' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('mrd')}
        >
          MRD (Medical Records)
        </button>
      </div>

      {activeTab === "blood" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Blood Bank Inventory</CardTitle>
              <div className="space-x-2">
                <Button variant="secondary">Log Donor</Button>
                <Button variant="primary">Add Unit</Button>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Unit Number</th>
                  <th className="p-3">Blood Group</th>
                  <th className="p-3">Component Type</th>
                  <th className="p-3">Expiry Date</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Action</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr><td colSpan={6} className="p-4 text-center">Loading...</td></tr>
                ) : (
                  bloodInventory.map(b => (
                    <tr key={b.id} className="border-b border-surface-hover">
                      <td className="p-3 font-medium text-primary-dark">{b.unitNumber}</td>
                      <td className="p-3 font-bold text-error">{b.bloodGroup}</td>
                      <td className="p-3">{b.componentType}</td>
                      <td className="p-3">{b.expiryDate}</td>
                      <td className="p-3">
                        {b.status === "Available" ? <Badge variant="success">Available</Badge> : <Badge variant="warning">Cross-matched</Badge>}
                      </td>
                      <td className="p-3">
                        <Button variant="secondary" size="sm">Cross-match</Button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "infection" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>HAI Surveillance (Hospital Acquired Infections)</CardTitle>
              <Button variant="danger">Report Infection Incident</Button>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Incident ID</th>
                  <th className="p-3">Patient</th>
                  <th className="p-3">Location</th>
                  <th className="p-3">Infection Type</th>
                  <th className="p-3">Date Identified</th>
                  <th className="p-3">Status</th>
                </tr>
              </thead>
              <tbody>
                {infections.map(inf => (
                  <tr key={inf.id} className="border-b border-surface-hover">
                    <td className="p-3 font-medium text-primary-dark">{inf.id}</td>
                    <td className="p-3 font-bold">{inf.patient}</td>
                    <td className="p-3">{inf.ward}</td>
                    <td className="p-3"><Badge variant="error">{inf.type}</Badge></td>
                    <td className="p-3">{inf.date}</td>
                    <td className="p-3"><Badge variant="warning">Active Investigation</Badge></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "diet" && (
        <Card>
          <CardHeader><CardTitle>Dietary & Nutrition Services</CardTitle></CardHeader>
          <CardContent className="h-64 flex items-center justify-center text-text-secondary border-t border-border mt-4">
            <div>
              <p className="text-lg font-bold mb-2">Dietary Module is integrated via Core Operations.</p>
              <p>Nutrition plans and active diet orders for admitted patients are pushed to the Kitchen Dispatch queue.</p>
            </div>
          </CardContent>
        </Card>
      )}

      {activeTab === "mrd" && (
        <Card>
          <CardHeader><CardTitle>Medical Records Department (MRD)</CardTitle></CardHeader>
          <CardContent className="h-64 flex items-center justify-center text-text-secondary border-t border-border mt-4">
            <div>
              <p className="text-lg font-bold mb-2">MRD Module is integrated via Analytics.</p>
              <p>Medical record audits, ICD-10 coding completeness, and death certificates are managed via the Command Center.</p>
            </div>
          </CardContent>
        </Card>
      )}

    </div>
  );
}
