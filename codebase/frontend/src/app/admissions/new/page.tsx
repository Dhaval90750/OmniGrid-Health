"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";

export default function NewAdmission() {
  const router = useRouter();

  const handleAdmit = (e: React.FormEvent) => {
    e.preventDefault();
    alert("Patient Admitted & Bed Allocated!");
    router.push("/admissions");
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="secondary" onClick={() => router.push("/admissions")}>← Back</Button>
        <h2 className="text-2xl font-semibold text-text-primary">Admit Patient</h2>
      </div>

      <form onSubmit={handleAdmit} className="space-y-6">
        
        {/* Patient Selection */}
        <Card>
          <CardHeader><CardTitle>1. Select Patient</CardTitle></CardHeader>
          <CardContent>
            <div className="flex gap-4 items-end">
              <div className="flex-1">
                <Input label="Search UHID or Mobile" placeholder="MED-2026..." />
              </div>
              <Button variant="secondary" type="button">Search</Button>
            </div>
            
            {/* Mock Patient Card showing selected state */}
            <div className="mt-4 p-4 border border-primary bg-primary-light rounded-lg flex justify-between items-center">
              <div>
                <div className="font-bold text-primary-dark">Rahul Sharma (34M)</div>
                <div className="text-sm text-primary">UHID: MED-2026-000001 • Mobile: +91 9876543210</div>
              </div>
              <Badge variant="success">Selected</Badge>
            </div>
          </CardContent>
        </Card>

        {/* Admission Details */}
        <Card>
          <CardHeader><CardTitle>2. Admission Details</CardTitle></CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="flex flex-col gap-1 w-full md:col-span-2">
              <label className="text-xs font-medium text-text-secondary">Admission Reason / Diagnosis *</label>
              <textarea 
                className="bg-background border border-border rounded-[6px] h-20 p-3 text-sm text-text-primary outline-none focus:border-primary resize-y"
                required
              />
            </div>
            
            <div className="flex flex-col gap-1 w-full">
              <label className="text-xs font-medium text-text-secondary">Admitting Doctor *</label>
              <select className="bg-background border border-border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none focus:border-primary" required>
                <option value="">Select...</option>
                <option value="dr_smith">Dr. Smith (Cardiology)</option>
                <option value="dr_jones">Dr. Jones (General Medicine)</option>
              </select>
            </div>

            <Input label="Admission Date *" type="datetime-local" defaultValue={new Date().toISOString().slice(0, 16)} required />
          </CardContent>
        </Card>

        {/* Bed Allocation */}
        <Card>
          <CardHeader><CardTitle>3. Bed Allocation</CardTitle></CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="flex flex-col gap-1 w-full">
              <label className="text-xs font-medium text-text-secondary">Select Ward *</label>
              <select className="bg-background border border-border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none focus:border-primary" required>
                <option value="">Select...</option>
                <option value="general_male">General Male Ward (10 Available)</option>
                <option value="icu">ICU (2 Available)</option>
              </select>
            </div>
            
            <div className="flex flex-col gap-1 w-full">
              <label className="text-xs font-medium text-text-secondary">Select Bed *</label>
              <select className="bg-background border border-border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none focus:border-primary" required>
                <option value="">Select...</option>
                <option value="GM-101">GM-101</option>
                <option value="GM-102">GM-102</option>
                <option value="GM-103">GM-103</option>
              </select>
            </div>
          </CardContent>
          <CardFooter className="justify-end gap-4 border-t border-border mt-4 pt-4">
            <Button type="button" variant="secondary" onClick={() => router.push("/admissions")}>Cancel</Button>
            <Button type="submit" variant="primary">Confirm Admission</Button>
          </CardFooter>
        </Card>

      </form>
    </div>
  );
}
