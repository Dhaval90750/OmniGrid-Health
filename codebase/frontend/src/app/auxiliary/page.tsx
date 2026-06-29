"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";

import { api } from "@/lib/api";

export default function AuxiliaryDashboard() {
  const [activeTab, setActiveTab] = useState("blood");

  const [bloodInventory, setBloodInventory] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  // Modals state
  const [showDonorModal, setShowDonorModal] = useState(false);
  const [showCrossMatchModal, setShowCrossMatchModal] = useState(false);
  const [selectedUnit, setSelectedUnit] = useState<any>(null);

  // Form states
  const [donorForm, setDonorForm] = useState({ name: "", bloodGroup: "O+", contact: "" });
  const [crossMatchForm, setCrossMatchForm] = useState({ patientName: "", uhid: "", requiredDate: "" });

  const fetchInventory = () => {
    setLoading(true);
    api.get('/auxiliary/blood-bank')
      .then(res => {
        setBloodInventory(res.data);
        setLoading(false);
      })
      .catch(err => {
        console.error(err);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchInventory();
  }, []);

  const handleLogDonor = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/auxiliary/blood-bank/donations', {
        donorName: donorForm.name,
        bloodGroup: donorForm.bloodGroup,
        contactNumber: donorForm.contact,
        donationDate: new Date().toISOString()
      });
      alert("Donor logged successfully!");
      setShowDonorModal(false);
      setDonorForm({ name: "", bloodGroup: "O+", contact: "" });
    } catch (err) {
      console.error(err);
      alert("Failed to log donor.");
    }
  };

  const handleCrossMatch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedUnit) return;
    try {
      await api.post('/auxiliary/blood-bank/transfusions', {
        unitId: selectedUnit.id,
        patientName: crossMatchForm.patientName,
        uhid: crossMatchForm.uhid,
        requestDate: crossMatchForm.requiredDate,
        status: "CROSS_MATCHED"
      });
      alert(`Unit ${selectedUnit.unitNumber} cross-matched for ${crossMatchForm.patientName}`);
      setShowCrossMatchModal(false);
      setCrossMatchForm({ patientName: "", uhid: "", requiredDate: "" });
      fetchInventory();
    } catch (err) {
      console.error(err);
      alert("Failed to cross-match unit.");
    }
  };

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
                <Button variant="secondary" onClick={() => setShowDonorModal(true)}>Log Donor</Button>
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
                        <Button 
                          variant="secondary" 
                          size="sm"
                          disabled={b.status !== "Available"}
                          onClick={() => { setSelectedUnit(b); setShowCrossMatchModal(true); }}
                        >
                          Cross-match
                        </Button>
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

      {/* Log Donor Modal */}
      {showDonorModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-[400px]">
            <CardHeader><CardTitle>Log Blood Donor</CardTitle></CardHeader>
            <CardContent>
              <form onSubmit={handleLogDonor} className="space-y-4">
                <div>
                  <label className="text-sm font-medium mb-1 block">Donor Name</label>
                  <Input required placeholder="Name" value={donorForm.name} onChange={e => setDonorForm({...donorForm, name: e.target.value})} />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Blood Group</label>
                  <select 
                    className="w-full bg-background border border-border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-primary"
                    value={donorForm.bloodGroup}
                    onChange={e => setDonorForm({...donorForm, bloodGroup: e.target.value})}
                  >
                    <option>O+</option><option>O-</option><option>A+</option><option>A-</option>
                    <option>B+</option><option>B-</option><option>AB+</option><option>AB-</option>
                  </select>
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Contact Number</label>
                  <Input required placeholder="Phone number" value={donorForm.contact} onChange={e => setDonorForm({...donorForm, contact: e.target.value})} />
                </div>
                <div className="flex justify-end gap-2 pt-4">
                  <Button type="button" variant="secondary" onClick={() => setShowDonorModal(false)}>Cancel</Button>
                  <Button type="submit" variant="primary">Log Donor</Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Cross-match Modal */}
      {showCrossMatchModal && selectedUnit && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-[400px]">
            <CardHeader><CardTitle>Cross-match Unit {selectedUnit.unitNumber}</CardTitle></CardHeader>
            <CardContent>
              <form onSubmit={handleCrossMatch} className="space-y-4">
                <div className="bg-surface p-3 rounded-md mb-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-text-secondary">Blood Group:</span>
                    <span className="font-bold text-error">{selectedUnit.bloodGroup}</span>
                  </div>
                  <div className="flex justify-between mt-1">
                    <span className="text-text-secondary">Component:</span>
                    <span className="font-medium">{selectedUnit.componentType}</span>
                  </div>
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Patient Name</label>
                  <Input required placeholder="Patient Name" value={crossMatchForm.patientName} onChange={e => setCrossMatchForm({...crossMatchForm, patientName: e.target.value})} />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Patient UHID</label>
                  <Input required placeholder="UHID-XXXX" value={crossMatchForm.uhid} onChange={e => setCrossMatchForm({...crossMatchForm, uhid: e.target.value})} />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1 block">Required Date</label>
                  <Input required type="date" value={crossMatchForm.requiredDate} onChange={e => setCrossMatchForm({...crossMatchForm, requiredDate: e.target.value})} />
                </div>
                <div className="flex justify-end gap-2 pt-4">
                  <Button type="button" variant="secondary" onClick={() => setShowCrossMatchModal(false)}>Cancel</Button>
                  <Button type="submit" variant="primary">Confirm Cross-match</Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}

    </div>
  );
}
