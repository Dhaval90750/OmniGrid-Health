"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";

export default function NewPatientRegistration() {
  const router = useRouter();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    dateOfBirth: "",
    gender: "",
    mobileNumber: "",
    bloodGroup: "",
    email: "",
    addressLine1: "",
    city: "",
    state: "",
    emergencyContactName: "",
    emergencyContactPhone: ""
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const response = await api.post("/patients", formData);
      // Redirect to the newly created patient profile
      router.push(`/patients/${response.data.id}`);
    } catch (error) {
      console.error("Failed to register patient", error);
      alert("Registration failed. Please check the inputs.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="secondary" onClick={() => router.push("/patients")}>← Back</Button>
        <h2 className="text-2xl font-semibold text-text-primary">New Patient Registration</h2>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="space-y-6">
          {/* Demographics */}
          <Card>
            <CardHeader><CardTitle>Demographics</CardTitle></CardHeader>
            <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input label="First Name *" name="firstName" required onChange={handleChange} />
              <Input label="Last Name *" name="lastName" required onChange={handleChange} />
              <Input label="Date of Birth *" type="date" name="dateOfBirth" required onChange={handleChange} />
              <div className="flex flex-col gap-1 w-full">
                <label className="text-xs font-medium text-text-secondary">Gender *</label>
                <select 
                  name="gender" 
                  required 
                  onChange={handleChange}
                  className="bg-background border border-border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none focus:border-primary"
                >
                  <option value="">Select...</option>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Other">Other</option>
                </select>
              </div>
              <Input label="Blood Group" name="bloodGroup" placeholder="e.g. O+" onChange={handleChange} />
            </CardContent>
          </Card>

          {/* Contact Information */}
          <Card>
            <CardHeader><CardTitle>Contact Information</CardTitle></CardHeader>
            <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input label="Mobile Number *" type="tel" name="mobileNumber" required onChange={handleChange} />
              <Input label="Email Address" type="email" name="email" onChange={handleChange} />
              <div className="md:col-span-2">
                <Input label="Address Line 1" name="addressLine1" onChange={handleChange} />
              </div>
              <Input label="City" name="city" onChange={handleChange} />
              <Input label="State" name="state" onChange={handleChange} />
            </CardContent>
          </Card>

          {/* Emergency Contact */}
          <Card>
            <CardHeader><CardTitle>Emergency Contact</CardTitle></CardHeader>
            <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input label="Contact Name" name="emergencyContactName" onChange={handleChange} />
              <Input label="Contact Phone" name="emergencyContactPhone" type="tel" onChange={handleChange} />
            </CardContent>
            <CardFooter className="justify-end gap-4">
              <Button type="button" variant="secondary" onClick={() => router.push("/patients")}>Cancel</Button>
              <Button type="submit" variant="primary" disabled={isSubmitting}>
                {isSubmitting ? "Registering..." : "Complete Registration"}
              </Button>
            </CardFooter>
          </Card>
        </div>
      </form>
    </div>
  );
}
