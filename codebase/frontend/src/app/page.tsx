"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function Home() {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    dateOfBirth: "",
    gender: "",
    mobileNumber: "",
    email: ""
  });
  
  const [isLoading, setIsLoading] = useState(false);
  const [successMsg, setSuccessMsg] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleClear = () => {
    setFormData({
      firstName: "", lastName: "", dateOfBirth: "", gender: "", mobileNumber: "", email: ""
    });
    setErrorMsg("");
  };

  const handleSubmit = async () => {
    try {
      setIsLoading(true);
      setErrorMsg("");
      setSuccessMsg("");
      const response = await api.post("/patients", formData);
      setSuccessMsg(`Success! Generated UHID: ${response.data.uhid}`);
      setFormData({
        firstName: "", lastName: "", dateOfBirth: "", gender: "", mobileNumber: "", email: ""
      });
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || err.response?.data?.error || "Failed to register patient");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-5xl space-y-8">
      {/* Welcome Section */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary mb-1">Welcome back, Dr. Smith</h2>
          <p className="text-text-secondary text-sm">Here is what&apos;s happening today in OmniGrid Health.</p>
        </div>
        <div className="flex gap-3">
          <Button variant="secondary">View Schedule</Button>
          <Button variant="primary">New Patient Registration</Button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        
        {/* Patient Registration Card */}
        <Card className="col-span-1 md:col-span-2">
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Quick Registration</CardTitle>
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
            <CardTitle>System Status</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex justify-between items-center pb-2 border-b border-surface-hover">
              <span className="text-sm text-text-secondary">Core Database</span>
              <Badge variant="success">Online</Badge>
            </div>
            <div className="flex justify-between items-center pb-2 border-b border-surface-hover">
              <span className="text-sm text-text-secondary">Auth Service</span>
              <Badge variant="success">Online</Badge>
            </div>
            <div className="flex justify-between items-center pb-2 border-b border-surface-hover">
              <span className="text-sm text-text-secondary">Lab Integration</span>
              <Badge variant="warning">Sync Delayed</Badge>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-sm text-text-secondary">ICU Ventilators</span>
              <Badge variant="error">Critical Load</Badge>
            </div>
          </CardContent>
        </Card>

      </div>
    </div>
  );
}
