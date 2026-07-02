"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";
import { useAuthStore } from "@/store/useAuthStore";

export default function Dashboard() {
  const router = useRouter();
  const { user } = useAuthStore();
  
  // Determine primary role for dashboard routing
  const primaryRole = user?.roles?.[0] || "";
  const isAdmin = primaryRole === "ROLE_ADMIN";
  const isReceptionist = primaryRole === "ROLE_RECEPTIONIST";
  const isDoctor = primaryRole === "ROLE_DOCTOR";
  const isPharmacist = primaryRole === "ROLE_PHARMACIST";

  // --- RECEPTIONIST STATE ---
  const [formData, setFormData] = useState({
    firstName: "", lastName: "", dateOfBirth: "", gender: "", mobileNumber: "", email: ""
  });
  const [isLoading, setIsLoading] = useState(false);
  const [successMsg, setSuccessMsg] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };
  const handleClear = () => {
    setFormData({ firstName: "", lastName: "", dateOfBirth: "", gender: "", mobileNumber: "", email: "" });
    setErrorMsg("");
  };
  const handleSubmit = async () => {
    try {
      setIsLoading(true); setErrorMsg(""); setSuccessMsg("");
      const response = await api.post("/patients", formData);
      setSuccessMsg(`Success! Generated UHID: ${response.data.uhid}`);
      handleClear();
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || "Failed to register patient");
    } finally {
      setIsLoading(false);
    }
  };

  // --- RENDER DYNAMIC DASHBOARDS ---

  // 1. ADMIN DASHBOARD
  if (isAdmin) {
    return (
      <div className="max-w-6xl space-y-8">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-semibold text-text-primary mb-1">Administrator Dashboard</h2>
            <p className="text-text-secondary text-sm">Welcome back, {user?.username}. System operations overview.</p>
          </div>
          <div className="flex gap-3">
            <Button variant="secondary" onClick={() => router.push('/admin/users')}>Manage Users</Button>
            <Button variant="primary" onClick={() => router.push('/admin/settings')}>System Settings</Button>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <Card>
            <CardContent className="p-6">
              <div className="text-sm text-text-secondary">Active Sessions</div>
              <div className="text-3xl font-bold text-primary mt-2">124</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-6">
              <div className="text-sm text-text-secondary">Server CPU Load</div>
              <div className="text-3xl font-bold text-warning-dark mt-2">42%</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-6">
              <div className="text-sm text-text-secondary">Database Health</div>
              <div className="text-3xl font-bold text-success mt-2">Optimal</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-6">
              <div className="text-sm text-text-secondary">Pending Security Audits</div>
              <div className="text-3xl font-bold text-error mt-2">3</div>
            </CardContent>
          </Card>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <Card>
            <CardHeader><CardTitle>Recent Activity Logs</CardTitle></CardHeader>
            <CardContent>
              <ul className="space-y-4 text-sm">
                <li className="flex justify-between border-b pb-2"><span className="text-text-primary">System Backup Completed</span><span className="text-text-secondary">02:00 AM</span></li>
                <li className="flex justify-between border-b pb-2"><span className="text-text-primary">New User Registered (Dr. Jane Doe)</span><span className="text-text-secondary">08:15 AM</span></li>
                <li className="flex justify-between"><span className="text-text-primary text-error">Failed Login Attempt (IP: 192.168.1.5)</span><span className="text-text-secondary">09:42 AM</span></li>
              </ul>
            </CardContent>
          </Card>
          <Card>
            <CardHeader><CardTitle>Module Status</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div className="flex justify-between items-center pb-2 border-b"><span className="text-sm">Authentication</span><Badge variant="success">Online</Badge></div>
              <div className="flex justify-between items-center pb-2 border-b"><span className="text-sm">Billing Gateway (Stripe)</span><Badge variant="success">Online</Badge></div>
              <div className="flex justify-between items-center pb-2 border-b"><span className="text-sm">ABDM Sandbox Sync</span><Badge variant="warning">Syncing</Badge></div>
              <div className="flex justify-between items-center"><span className="text-sm">Telemedicine WebSockets</span><Badge variant="success">Active</Badge></div>
            </CardContent>
          </Card>
        </div>
      </div>
    );
  }

  // 2. DOCTOR DASHBOARD
  if (isDoctor) {
    return (
      <div className="max-w-6xl space-y-8">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-semibold text-text-primary mb-1">Welcome back, Dr. {user?.username}</h2>
            <p className="text-text-secondary text-sm">Here is your schedule for today.</p>
          </div>
          <Button variant="primary" onClick={() => router.push('/patients')}>View All Patients</Button>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card className="col-span-2">
            <CardHeader><CardTitle>Today&apos;s Appointments</CardTitle></CardHeader>
            <CardContent>
              <div className="text-center p-8 text-text-secondary border-2 border-dashed rounded-lg">
                No upcoming appointments scheduled for today.
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardHeader><CardTitle>Quick Actions</CardTitle></CardHeader>
            <CardContent className="space-y-3">
              <Button className="w-full justify-start" variant="secondary" onClick={() => router.push('/doctor/ai-scribe')}>🎙️ Start AI Scribe</Button>
              <Button className="w-full justify-start" variant="secondary" onClick={() => router.push('/doctor/telemedicine')}>💻 Join Telemedicine Room</Button>
              <Button className="w-full justify-start" variant="secondary" onClick={() => router.push('/patients')}>📋 Write Prescription</Button>
            </CardContent>
          </Card>
        </div>
      </div>
    );
  }

  // 3. RECEPTIONIST DASHBOARD (Default / Registration)
  return (
    <div className="max-w-5xl space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary mb-1">Welcome, {user?.username || 'Staff'}</h2>
          <p className="text-text-secondary text-sm">OmniGrid Health Front Desk Operations</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="col-span-1 md:col-span-2">
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Quick Patient Registration</CardTitle>
              <Badge variant="info">OPD Flow</Badge>
            </div>
          </CardHeader>
          <CardContent className="space-y-4">
            {successMsg && <div className="p-3 bg-success/10 text-success border border-success/20 rounded-[6px] text-sm font-medium">{successMsg}</div>}
            {errorMsg && <div className="p-3 bg-error/10 text-error border border-error/20 rounded-[6px] text-sm font-medium">{errorMsg}</div>}

            <div className="grid grid-cols-2 gap-4">
              <Input name="firstName" value={formData.firstName} onChange={handleChange} label="First Name" placeholder="Enter first name" />
              <Input name="lastName" value={formData.lastName} onChange={handleChange} label="Last Name" placeholder="Enter last name" />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <Input name="dateOfBirth" value={formData.dateOfBirth} onChange={handleChange} label="Date of Birth" type="date" />
              <div className="flex flex-col gap-1 w-full">
                <label className="text-xs font-medium text-text-secondary">Gender</label>
                <select 
                  name="gender" 
                  value={formData.gender} 
                  onChange={handleChange}
                  className="bg-background border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none transition-colors border-border hover:border-text-tertiary focus:border-primary focus:ring-1 focus:ring-primary"
                >
                  <option value="">Select Gender</option>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Other">Other</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <Input name="mobileNumber" value={formData.mobileNumber} onChange={handleChange} label="Mobile Number" placeholder="+91 9876543210" type="tel" />
              <Input name="email" value={formData.email} onChange={handleChange} label="Email Address (Optional)" placeholder="patient@example.com" type="email" />
            </div>
          </CardContent>
          <CardFooter className="justify-end gap-3">
            <Button variant="secondary" onClick={handleClear}>Clear Form</Button>
            <Button variant="primary" onClick={handleSubmit} disabled={isLoading}>
              {isLoading ? "Generating..." : "Generate UHID & QR"}
            </Button>
          </CardFooter>
        </Card>

        {/* System Status Card */}
        <Card>
          <CardHeader>
            <CardTitle>Department Status</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex justify-between items-center pb-2 border-b border-surface-hover">
              <span className="text-sm text-text-secondary">OPD Queue</span>
              <Badge variant="info">12 Waiting</Badge>
            </div>
            <div className="flex justify-between items-center pb-2 border-b border-surface-hover">
              <span className="text-sm text-text-secondary">Lab Reports</span>
              <Badge variant="success">All Cleared</Badge>
            </div>
            <div className="flex justify-between items-center pb-2 border-b border-surface-hover">
              <span className="text-sm text-text-secondary">Pharmacy</span>
              <Badge variant="success">Operating</Badge>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
