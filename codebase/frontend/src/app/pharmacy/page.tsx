"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

export default function PharmacyDashboard() {
  const [activeTab, setActiveTab] = useState("queue");
  const [selectedRx, setSelectedRx] = useState<any>(null);

  const handleDispense = () => {
    alert("Medications Dispensed & Inventory Updated!");
    setSelectedRx(null);
    setActiveTab("queue");
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Pharmacy Operations</h1>
          <p className="text-text-secondary text-sm">Manage dispensing, inventory, and stock movements</p>
        </div>
        <Button variant="secondary" onClick={() => setActiveTab("stock")}>Inventory Dashboard</Button>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-border">
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'queue' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => { setActiveTab('queue'); setSelectedRx(null); }}
        >
          Prescription Queue
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'dispensing' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('dispensing')}
        >
          Dispensing Station
        </button>
        <button 
          className={`pb-2 px-1 text-sm font-medium border-b-2 ${activeTab === 'stock' ? 'border-primary text-primary' : 'border-transparent text-text-secondary hover:text-text-primary'}`}
          onClick={() => setActiveTab('stock')}
        >
          Stock Management
        </button>
      </div>

      {/* Content */}
      {activeTab === "queue" && (
        <Card>
          <CardHeader><CardTitle>Incoming E-Prescriptions</CardTitle></CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Time</th>
                  <th className="p-3">Patient</th>
                  <th className="p-3">Doctor</th>
                  <th className="p-3">Items</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Action</th>
                </tr>
              </thead>
              <tbody>
                <tr className="border-b border-surface-hover">
                  <td className="p-3 font-medium">10:45 AM</td>
                  <td className="p-3">
                    <div className="font-medium">Rahul Sharma</div>
                    <div className="text-xs text-text-secondary">UHID: MED-2026-000001</div>
                  </td>
                  <td className="p-3">Dr. Anjali Desai</td>
                  <td className="p-3">3 Drugs</td>
                  <td className="p-3"><Badge variant="warning">Pending Fulfillment</Badge></td>
                  <td className="p-3">
                    <Button 
                      variant="primary" 
                      size="sm" 
                      onClick={() => {
                        setSelectedRx({ id: 1, patient: 'Rahul Sharma', doctor: 'Dr. Anjali Desai' });
                        setActiveTab('dispensing');
                      }}
                    >
                      Process Rx
                    </Button>
                  </td>
                </tr>
                <tr className="border-b border-surface-hover">
                  <td className="p-3 font-medium">10:30 AM</td>
                  <td className="p-3">
                    <div className="font-medium">Sneha Patel</div>
                    <div className="text-xs text-text-secondary">UHID: MED-2026-000045</div>
                  </td>
                  <td className="p-3">Dr. Vikram Singh</td>
                  <td className="p-3">1 Drug</td>
                  <td className="p-3"><Badge variant="success">Dispensed</Badge></td>
                  <td className="p-3">
                    <Button variant="secondary" size="sm" disabled>View Only</Button>
                  </td>
                </tr>
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {activeTab === "dispensing" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Dispensing Station</CardTitle>
              {selectedRx && <Badge variant="info">Rx: {selectedRx.patient} (Prescribed by {selectedRx.doctor})</Badge>}
            </div>
          </CardHeader>
          <CardContent>
            {selectedRx ? (
              <div className="space-y-6">
                <div className="border border-border rounded-md overflow-hidden">
                  <table className="w-full text-left text-sm">
                    <thead className="bg-surface border-b border-border">
                      <tr>
                        <th className="p-3">Prescribed Drug</th>
                        <th className="p-3">Dosage & Freq</th>
                        <th className="p-3">Req. Qty</th>
                        <th className="p-3 bg-primary-light text-primary-dark">Select Batch (Inventory)</th>
                        <th className="p-3">Dispense Qty</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr className="border-b border-surface-hover">
                        <td className="p-3 font-medium">Paracetamol 650mg Tablet</td>
                        <td className="p-3 text-text-secondary">1 Tab TDS x 5 Days</td>
                        <td className="p-3 font-bold text-center">15</td>
                        <td className="p-3 bg-primary-light/10">
                          <select className="w-full p-2 border border-border rounded-md text-sm outline-none">
                            <option>Batch: BATCH-001 (Exp: 2027-12) - Qty: 450</option>
                            <option>Batch: BATCH-005 (Exp: 2028-06) - Qty: 1000</option>
                          </select>
                        </td>
                        <td className="p-3">
                           <input type="number" defaultValue={15} className="w-16 p-2 border border-border rounded-md text-sm outline-none text-center" />
                        </td>
                      </tr>
                      <tr className="border-b border-surface-hover">
                        <td className="p-3 font-medium">Amoxicillin 500mg Capsule</td>
                        <td className="p-3 text-text-secondary">1 Cap BD x 7 Days</td>
                        <td className="p-3 font-bold text-center">14</td>
                        <td className="p-3 bg-primary-light/10">
                          <select className="w-full p-2 border border-border rounded-md text-sm outline-none">
                            <option>Batch: AMOX-88 (Exp: 2026-10) - Qty: 56</option>
                          </select>
                        </td>
                        <td className="p-3">
                           <input type="number" defaultValue={14} className="w-16 p-2 border border-border rounded-md text-sm outline-none text-center" />
                        </td>
                      </tr>
                      <tr className="border-b border-surface-hover">
                        <td className="p-3 font-medium">Pantoprazole 40mg Tablet</td>
                        <td className="p-3 text-text-secondary">1 Tab OD x 7 Days</td>
                        <td className="p-3 font-bold text-center">7</td>
                        <td className="p-3 bg-primary-light/10">
                          <select className="w-full p-2 border border-border rounded-md text-sm outline-none">
                            <option>Batch: PAN-12 (Exp: 2028-01) - Qty: 800</option>
                          </select>
                        </td>
                        <td className="p-3">
                           <input type="number" defaultValue={7} className="w-16 p-2 border border-border rounded-md text-sm outline-none text-center" />
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>

                <div className="flex justify-between items-center border-t border-border pt-4">
                   <div className="text-sm text-text-secondary">
                     * Dispensing will automatically deduct quantities from the selected batches based on FEFO selection.
                   </div>
                   <div className="flex gap-4">
                     <Button variant="secondary" onClick={() => setActiveTab('queue')}>Cancel</Button>
                     <Button variant="primary" onClick={handleDispense}>Confirm & Dispense</Button>
                   </div>
                </div>
              </div>
            ) : (
              <div className="py-12 text-center text-text-secondary">
                Please select a prescription from the queue to begin dispensing.
              </div>
            )}
          </CardContent>
        </Card>
      )}

      {activeTab === "stock" && (
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Current Inventory Stock</CardTitle>
              <div className="flex gap-2">
                 <input type="text" placeholder="Search drug name..." className="p-2 border border-border rounded-md text-sm outline-none" />
                 <Button variant="secondary">Add GRN (Stock Receipt)</Button>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <table className="w-full text-left text-sm">
              <thead className="bg-surface border-b border-border">
                <tr>
                  <th className="p-3">Drug Name</th>
                  <th className="p-3">Batch Number</th>
                  <th className="p-3">Expiry Date</th>
                  <th className="p-3 text-right">Qty Available</th>
                  <th className="p-3">Status</th>
                </tr>
              </thead>
              <tbody>
                <tr className="border-b border-surface-hover">
                  <td className="p-3 font-medium">Paracetamol 650mg Tablet</td>
                  <td className="p-3 font-mono text-xs">BATCH-001</td>
                  <td className="p-3 text-error">2026-08-01</td>
                  <td className="p-3 text-right font-medium">450</td>
                  <td className="p-3"><Badge variant="warning">Expiring Soon</Badge></td>
                </tr>
                <tr className="border-b border-surface-hover">
                  <td className="p-3 font-medium">Paracetamol 650mg Tablet</td>
                  <td className="p-3 font-mono text-xs">BATCH-005</td>
                  <td className="p-3">2028-06-15</td>
                  <td className="p-3 text-right font-medium">1000</td>
                  <td className="p-3"><Badge variant="success">Healthy</Badge></td>
                </tr>
                <tr className="border-b border-surface-hover">
                  <td className="p-3 font-medium">Amoxicillin 500mg Capsule</td>
                  <td className="p-3 font-mono text-xs">AMOX-88</td>
                  <td className="p-3">2026-10-20</td>
                  <td className="p-3 text-right font-bold text-error">56</td>
                  <td className="p-3"><Badge variant="error">Low Stock</Badge></td>
                </tr>
                <tr className="border-b border-surface-hover">
                  <td className="p-3 font-medium">Pantoprazole 40mg Tablet</td>
                  <td className="p-3 font-mono text-xs">PAN-12</td>
                  <td className="p-3">2028-01-10</td>
                  <td className="p-3 text-right font-medium">800</td>
                  <td className="p-3"><Badge variant="success">Healthy</Badge></td>
                </tr>
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
