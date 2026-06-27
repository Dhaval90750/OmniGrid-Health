"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function IpdDashboard() {
  const router = useRouter();
  const [searchQuery, setSearchQuery] = useState("");
  
  const [admissions, setAdmissions] = useState<any[]>([]);
  const [wards, setWards] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  
  // Modals state
  const [selectedAdm, setSelectedAdm] = useState<any>(null);
  const [showDischargeModal, setShowDischargeModal] = useState(false);
  const [showTransferModal, setShowTransferModal] = useState(false);
  const [dischargeSummary, setDischargeSummary] = useState("");
  const [transferWardId, setTransferWardId] = useState("");
  const [transferBedId, setTransferBedId] = useState("");
  const [transferAvailableBeds, setTransferAvailableBeds] = useState<any[]>([]);

  // Stats
  const [totalBeds, setTotalBeds] = useState(0);
  const [availableBeds, setAvailableBeds] = useState(0);
  const [occupiedBeds, setOccupiedBeds] = useState(0);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [admRes, matrixRes] = await Promise.all([
        api.get("/admissions/active"),
        api.get("/admin/bed-matrix")
      ]);
      setAdmissions(admRes.data);
      
      const wardData = matrixRes.data.wards || [];
      setWards(wardData);
      
      let tot = 0;
      let occ = 0;
      wardData.forEach((w: any) => {
        tot += w.totalBeds;
        occ += w.occupied;
      });
      setTotalBeds(tot);
      setOccupiedBeds(occ);
      setAvailableBeds(tot - occ);

    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleDischarge = async () => {
    if (!selectedAdm) return;
    try {
      await api.put(`/admissions/${selectedAdm.id}/discharge`, { dischargeSummary });
      setShowDischargeModal(false);
      fetchData();
      alert("Patient discharged successfully.");
    } catch (e) {
      alert("Failed to discharge");
    }
  };

  const handleTransfer = async () => {
    if (!selectedAdm || !transferBedId) return;
    try {
      await api.put(`/admissions/${selectedAdm.id}/transfer`, { newBedId: transferBedId });
      setShowTransferModal(false);
      fetchData();
      alert("Patient transferred successfully.");
    } catch (e) {
      alert("Failed to transfer");
    }
  };

  const openTransferModal = (adm: any) => {
    setSelectedAdm(adm);
    setShowTransferModal(true);
    setTransferWardId(wards.length > 0 ? wards[0].wardId : "");
    // Available beds will update via useEffect below if we set it up, or just manually trigger
  };

  useEffect(() => {
    if (transferWardId && showTransferModal) {
      const selectedWard = wards.find((w: any) => w.wardId === transferWardId);
      if (selectedWard) {
        setTransferAvailableBeds(selectedWard.beds.filter((b: any) => b.status === "AVAILABLE"));
      } else {
        setTransferAvailableBeds([]);
      }
    }
  }, [transferWardId, showTransferModal, wards]);

  const getStatusColor = (status: string) => {
    switch(status?.toUpperCase()) {
      case "AVAILABLE": return "bg-success text-white border-success-dark";
      case "OCCUPIED": return "bg-error text-white border-error-dark";
      case "CLEANING": return "bg-warning text-white border-warning-dark";
      case "MAINTENANCE": return "bg-gray-500 text-white border-gray-700";
      default: return "bg-surface text-text-primary border-border";
    }
  };

  const calculateLos = (admissionDate: string) => {
    if (!admissionDate) return "0 days";
    const diffTime = Math.abs(new Date().getTime() - new Date(admissionDate).getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return `${diffDays} days`;
  };

  return (
    <div className="max-w-7xl space-y-6 mx-auto">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">IPD Census & Bed Matrix</h2>
          <p className="text-text-secondary text-sm">Real-time hospital bed availability and active admissions</p>
        </div>
        <Button onClick={() => router.push("/admissions/new")}>+ Admit Patient</Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">

        {/* Quick Stats */}
        <div className="md:col-span-4 grid grid-cols-1 md:grid-cols-4 gap-4">
          <Card>
            <CardContent className="p-4 flex flex-col items-center justify-center">
              <div className="text-sm text-text-secondary">Active Admissions</div>
              <div className="text-3xl font-bold text-primary">{admissions.length}</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4 flex flex-col items-center justify-center">
              <div className="text-sm text-text-secondary">Available Beds</div>
              <div className="text-3xl font-bold text-success">{availableBeds}</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4 flex flex-col items-center justify-center">
              <div className="text-sm text-text-secondary">Occupancy Rate</div>
              <div className="text-3xl font-bold text-error">
                {totalBeds > 0 ? Math.round((occupiedBeds / totalBeds) * 100) : 0}%
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4 flex flex-col items-center justify-center">
              <div className="text-sm text-text-secondary">Total Beds</div>
              <div className="text-3xl font-bold text-text-primary">{totalBeds}</div>
            </CardContent>
          </Card>
        </div>

        {/* Bed Matrix Layout */}
        <div className="md:col-span-4">
          <Card>
            <CardHeader className="pb-2 border-b border-border">
              <div className="flex justify-between items-center w-full">
                <CardTitle>Hospital Bed Matrix</CardTitle>
                <div className="flex gap-4 text-xs font-medium">
                  <div className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-success"></span> Available</div>
                  <div className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-error"></span> Occupied</div>
                  <div className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-warning"></span> Cleaning</div>
                  <div className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-gray-500"></span> Maintenance</div>
                </div>
              </div>
            </CardHeader>
            <CardContent className="p-6">
              {loading ? (
                <div className="text-center p-8 text-text-secondary">Loading matrix...</div>
              ) : wards.length === 0 ? (
                <div className="text-center p-8 text-text-secondary">No wards configured in the system.</div>
              ) : (
                <div className="space-y-8">
                  {wards.map(ward => (
                    <div key={ward.wardId}>
                      <div className="flex items-center gap-4 mb-3">
                        <h3 className="font-bold text-lg">{ward.wardName}</h3>
                        <Badge variant="secondary">{ward.occupied} / {ward.totalBeds} Occupied</Badge>
                      </div>
                      <div className="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-6 lg:grid-cols-8 gap-3">
                        {ward.beds.map((bed: any) => (
                          <div 
                            key={bed.id} 
                            className={`p-3 rounded-md border-b-4 flex flex-col items-center justify-center text-center shadow-sm ${getStatusColor(bed.status)}`}
                          >
                            <div className="font-bold text-sm">{bed.bedId}</div>
                            {bed.patient && <div className="text-[10px] mt-1 font-medium truncate w-full px-1 bg-black/20 rounded py-0.5">{bed.patient}</div>}
                          </div>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Data Table */}
        <Card className="md:col-span-4">
          <CardHeader>
            <div className="flex justify-between items-center w-full">
              <CardTitle>Active Admissions List</CardTitle>
              <div className="w-64">
                <Input
                  placeholder="Search by UHID or Name..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
              </div>
            </div>
          </CardHeader>
          <CardContent className="p-0">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-border bg-surface text-text-secondary text-sm">
                  <th className="p-4 font-medium">Ward & Bed</th>
                  <th className="p-4 font-medium">Patient Details</th>
                  <th className="p-4 font-medium">Admitting Dr</th>
                  <th className="p-4 font-medium">Admission Date</th>
                  <th className="p-4 font-medium text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {loading ? (
                  <tr><td colSpan={5} className="p-8 text-center">Loading...</td></tr>
                ) : admissions.filter(a => (a.patient?.firstName + " " + a.patient?.lastName).toLowerCase().includes(searchQuery.toLowerCase()) || a.patient?.uhid?.toLowerCase().includes(searchQuery.toLowerCase())).length === 0 ? (
                  <tr><td colSpan={5} className="p-8 text-center text-text-secondary">No active admissions found.</td></tr>
                ) : admissions.filter(a => (a.patient?.firstName + " " + a.patient?.lastName).toLowerCase().includes(searchQuery.toLowerCase()) || a.patient?.uhid?.toLowerCase().includes(searchQuery.toLowerCase())).map((adm) => (
                  <tr key={adm.id} className="hover:bg-surface-hover transition-colors">
                    <td className="p-4">
                      <div className="font-bold text-primary">{adm.ward?.name}</div>
                      <div className="text-sm text-text-secondary">Bed: {adm.bed?.bedNumber}</div>
                    </td>
                    <td className="p-4">
                      <div className="font-medium">{adm.patient?.firstName} {adm.patient?.lastName}</div>
                      <div className="text-xs text-text-tertiary">{adm.patient?.uhid}</div>
                    </td>
                    <td className="p-4">{adm.attendingDoctor ? `Dr. ${adm.attendingDoctor.lastName}` : 'Unassigned'}</td>
                    <td className="p-4">
                      <div>{new Date(adm.admissionDate).toLocaleDateString()}</div>
                      <Badge variant="info" className="mt-1">LOS: {calculateLos(adm.admissionDate)}</Badge>
                    </td>
                    <td className="p-4 text-right flex justify-end gap-2">
                      <Button variant="outline" size="sm" onClick={() => openTransferModal(adm)}>Transfer</Button>
                      <Button variant="secondary" size="sm" onClick={() => { setSelectedAdm(adm); setShowDischargeModal(true); }}>Discharge</Button>
                      <Button variant="primary" size="sm" onClick={() => router.push(`/patients/${adm.patient?.id}`)}>View</Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>

      </div>

      {/* Discharge Modal */}
      {showDischargeModal && selectedAdm && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader>
              <CardTitle>Discharge Patient</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <div className="font-semibold">{selectedAdm.patient?.firstName} {selectedAdm.patient?.lastName}</div>
                <div className="text-sm text-text-secondary">Admitted on {new Date(selectedAdm.admissionDate).toLocaleDateString()}</div>
              </div>
              <div>
                <label className="text-sm font-medium mb-1 block">Discharge Summary</label>
                <textarea 
                  className="w-full h-32 p-3 border border-border rounded-md text-sm outline-none focus:border-primary resize-none"
                  placeholder="Enter final clinical summary for discharge..."
                  value={dischargeSummary}
                  onChange={(e) => setDischargeSummary(e.target.value)}
                ></textarea>
              </div>
              <div className="flex justify-end gap-2 pt-4">
                <Button variant="secondary" onClick={() => setShowDischargeModal(false)}>Cancel</Button>
                <Button variant="primary" onClick={handleDischarge}>Confirm Discharge</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Transfer Modal */}
      {showTransferModal && selectedAdm && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md bg-white">
            <CardHeader>
              <CardTitle>Transfer Bed</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <div className="font-semibold">{selectedAdm.patient?.firstName} {selectedAdm.patient?.lastName}</div>
                <div className="text-sm text-text-secondary">Current: {selectedAdm.ward?.name} - {selectedAdm.bed?.bedNumber}</div>
              </div>
              
              <div>
                <label className="text-sm font-medium mb-1 block">New Ward</label>
                <select 
                  className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm outline-none focus:border-primary bg-white mb-4"
                  value={transferWardId} 
                  onChange={e => { setTransferWardId(e.target.value); setTransferBedId(""); }}
                >
                  {wards.map(w => (
                    <option key={w.wardId} value={w.wardId}>{w.wardName}</option>
                  ))}
                </select>

                <label className="text-sm font-medium mb-1 block">New Available Bed</label>
                <select 
                  className="w-full h-10 px-3 py-2 border border-border rounded-md text-sm outline-none focus:border-primary bg-white"
                  value={transferBedId} 
                  onChange={e => setTransferBedId(e.target.value)}
                >
                  <option value="">Select Bed</option>
                  {transferAvailableBeds.map(b => (
                    <option key={b.id} value={b.id}>{b.bedId}</option>
                  ))}
                </select>
                {transferAvailableBeds.length === 0 && <div className="text-xs text-error mt-1">No beds available</div>}
              </div>

              <div className="flex justify-end gap-2 pt-4">
                <Button variant="secondary" onClick={() => setShowTransferModal(false)}>Cancel</Button>
                <Button variant="primary" disabled={!transferBedId} onClick={handleTransfer}>Confirm Transfer</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

    </div>
  );
}
