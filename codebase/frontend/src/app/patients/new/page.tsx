"use client";

import { useState, useRef, useCallback } from "react";
import { useRouter } from "next/navigation";
import Webcam from "react-webcam";
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
    addressLine2: "",
    city: "",
    state: "",
    country: "India",
    zipCode: "",
    maritalStatus: "",
    nationality: "Indian",
    primaryLanguage: "",
    nationalId: "",
    emergencyContactName: "",
    emergencyContactRelation: "",
    emergencyContactName: "",
    emergencyContactRelation: "",
    emergencyContactPhone: "",
    photoBase64: ""
  });
  
  const webcamRef = useRef<Webcam>(null);

  const capturePhoto = useCallback(() => {
    const imageSrc = webcamRef.current?.getScreenshot();
    if (imageSrc) {
      setFormData(prev => ({ ...prev, photoBase64: imageSrc }));
    }
  }, [webcamRef]);

  const clearPhoto = () => {
    setFormData(prev => ({ ...prev, photoBase64: "" }));
  };

  const INDIAN_STATES = [
    "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", 
    "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", 
    "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", 
    "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", 
    "Uttar Pradesh", "Uttarakhand", "West Bengal", "Andaman and Nicobar Islands", 
    "Chandigarh", "Dadra and Nagar Haveli and Daman and Diu", "Delhi", "Jammu and Kashmir", 
    "Ladakh", "Lakshadweep", "Puducherry"
  ];

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
              <Input label="First Name *" name="firstName" required value={formData.firstName} onChange={handleChange} />
              <Input label="Last Name *" name="lastName" required value={formData.lastName} onChange={handleChange} />
              <Input label="Date of Birth *" type="date" name="dateOfBirth" required value={formData.dateOfBirth} onChange={handleChange} />
              
              <div className="flex flex-col gap-1 w-full">
                <label className="text-xs font-medium text-text-secondary">Gender *</label>
                <select 
                  name="gender" 
                  required 
                  value={formData.gender}
                  onChange={handleChange}
                  className="bg-background border border-border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none focus:border-primary"
                >
                  <option value="">Select...</option>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Transgender">Transgender</option>
                  <option value="Other">Other</option>
                </select>
              </div>

              <div className="flex flex-col gap-1 w-full">
                <label className="text-xs font-medium text-text-secondary">Blood Group</label>
                <select 
                  name="bloodGroup" 
                  value={formData.bloodGroup}
                  onChange={handleChange}
                  className="bg-background border border-border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none focus:border-primary"
                >
                  <option value="">Select...</option>
                  <option value="A+">A+</option>
                  <option value="A-">A-</option>
                  <option value="B+">B+</option>
                  <option value="B-">B-</option>
                  <option value="AB+">AB+</option>
                  <option value="AB-">AB-</option>
                  <option value="O+">O+</option>
                  <option value="O-">O-</option>
                </select>
              </div>

              <div className="flex flex-col gap-1 w-full">
                <label className="text-xs font-medium text-text-secondary">Marital Status</label>
                <select 
                  name="maritalStatus" 
                  value={formData.maritalStatus}
                  onChange={handleChange}
                  className="bg-background border border-border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none focus:border-primary"
                >
                  <option value="">Select...</option>
                  <option value="Single">Single</option>
                  <option value="Married">Married</option>
                  <option value="Divorced">Divorced</option>
                  <option value="Widowed">Widowed</option>
                </select>
              </div>

              <Input label="Nationality" name="nationality" value={formData.nationality} onChange={handleChange} />
              <Input label="Primary Language" name="primaryLanguage" value={formData.primaryLanguage} onChange={handleChange} />
              <Input label="Aadhaar Number (National ID)" name="nationalId" placeholder="12-digit Aadhaar number" value={formData.nationalId} onChange={handleChange} />
            </CardContent>
          </Card>

          {/* Contact Information */}
          <Card>
            <CardHeader><CardTitle>Contact Information</CardTitle></CardHeader>
            <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input label="Mobile Number *" type="tel" name="mobileNumber" required value={formData.mobileNumber} onChange={handleChange} />
              <Input label="Email Address" type="email" name="email" value={formData.email} onChange={handleChange} />
              <div className="md:col-span-2">
                <Input label="Address Line 1" name="addressLine1" value={formData.addressLine1} onChange={handleChange} />
              </div>
              <div className="md:col-span-2">
                <Input label="Address Line 2" name="addressLine2" value={formData.addressLine2} onChange={handleChange} />
              </div>
              <Input label="City" name="city" value={formData.city} onChange={handleChange} />
              
              <div className="flex flex-col gap-1 w-full">
                <label className="text-xs font-medium text-text-secondary">State</label>
                <select 
                  name="state" 
                  value={formData.state}
                  onChange={handleChange}
                  className="bg-background border border-border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none focus:border-primary"
                >
                  <option value="">Select State...</option>
                  {INDIAN_STATES.map((state) => (
                    <option key={state} value={state}>{state}</option>
                  ))}
                </select>
              </div>

              <div className="flex flex-col gap-1 w-full">
                <label className="text-xs font-medium text-text-secondary">Country</label>
                <select 
                  name="country" 
                  value={formData.country}
                  onChange={handleChange}
                  className="bg-background border border-border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none focus:border-primary"
                >
                  <option value="India">India</option>
                  <option value="USA">USA</option>
                  <option value="UK">UK</option>
                  <option value="Other">Other</option>
                </select>
              </div>

              <Input label="PIN Code" name="zipCode" placeholder="6-digit PIN code" value={formData.zipCode} onChange={handleChange} />
            </CardContent>
          </Card>

          {/* Emergency Contact */}
          <Card>
            <CardHeader><CardTitle>Emergency Contact</CardTitle></CardHeader>
            <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input label="Contact Name" name="emergencyContactName" value={formData.emergencyContactName} onChange={handleChange} />
              
              <div className="flex flex-col gap-1 w-full">
                <label className="text-xs font-medium text-text-secondary">Relationship</label>
                <select 
                  name="emergencyContactRelation" 
                  value={formData.emergencyContactRelation}
                  onChange={handleChange}
                  className="bg-background border border-border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none focus:border-primary"
                >
                  <option value="">Select...</option>
                  <option value="Father">Father</option>
                  <option value="Mother">Mother</option>
                  <option value="Spouse">Spouse</option>
                  <option value="Sibling">Sibling</option>
                  <option value="Child">Child</option>
                  <option value="Guardian">Guardian</option>
                  <option value="Friend">Friend</option>
                  <option value="Other">Other</option>
                </select>
              </div>

              <Input label="Contact Phone" name="emergencyContactPhone" type="tel" value={formData.emergencyContactPhone} onChange={handleChange} />
            </CardContent>
          </Card>

          {/* Photo Capture */}
          <Card>
            <CardHeader><CardTitle>Patient Photo</CardTitle></CardHeader>
            <CardContent className="flex flex-col items-center gap-4">
              {formData.photoBase64 ? (
                <div className="flex flex-col items-center gap-4">
                  <img src={formData.photoBase64} alt="Patient" className="w-64 h-48 object-cover rounded-md border border-border" />
                  <Button type="button" variant="secondary" onClick={clearPhoto}>Retake Photo</Button>
                </div>
              ) : (
                <div className="flex flex-col items-center gap-4 w-full">
                  <div className="w-full max-w-sm rounded-md overflow-hidden border border-border">
                    <Webcam
                      audio={false}
                      ref={webcamRef}
                      screenshotFormat="image/jpeg"
                      videoConstraints={{ facingMode: "user" }}
                      className="w-full"
                    />
                  </div>
                  <Button type="button" variant="primary" onClick={capturePhoto}>Capture Photo</Button>
                </div>
              )}
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
