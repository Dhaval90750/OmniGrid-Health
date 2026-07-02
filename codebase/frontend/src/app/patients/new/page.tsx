"use client";

import { useState, useRef, useCallback } from "react";
import { useRouter } from "next/navigation";
import Webcam from "react-webcam";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { api } from "@/lib/api";

import { validateAadhaar } from "@/lib/aadhaarValidation";

export default function NewPatientRegistration() {
  const router = useRouter();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [validationError, setValidationError] = useState("");
  const [showDuplicateModal, setShowDuplicateModal] = useState(false);
  const [duplicatePatient, setDuplicatePatient] = useState<any>(null);
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
    emergencyContactPhone: "",
    middleName: "",
    occupation: "",
    secondaryMobile: "",
    abhaId: "",
    passportNumber: "",
    religion: "",
    referredBy: "",
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
    setValidationError("");
    
    // Aadhaar Validation
    if (formData.nationalId && formData.nationalId.length === 12) {
      if (!validateAadhaar(formData.nationalId)) {
        setValidationError("Invalid Aadhaar number according to Verhoeff checksum algorithm.");
        return;
      }
    }

    setIsSubmitting(true);
    try {
      const response = await api.post("/patients", formData);
      // Redirect to the newly created patient profile
      router.push(`/patients/${response.data.id}`);
    } catch (error: any) {
      if (error.response?.status === 409) {
        setDuplicatePatient(error.response.data.duplicatePatient);
        setShowDuplicateModal(true);
      } else {
        console.error("Failed to register patient", error);
        setValidationError("Registration failed. Please check the inputs.");
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const forceCreatePatient = async () => {
    setIsSubmitting(true);
    try {
      const payload = { ...formData, bypassDuplicateCheck: true };
      const response = await api.post("/patients", payload);
      router.push(`/patients/${response.data.id}`);
    } catch (error) {
      console.error("Failed to force register patient", error);
      setValidationError("Registration failed. Please check the inputs.");
    } finally {
      setIsSubmitting(false);
      setShowDuplicateModal(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="secondary" onClick={() => router.push("/patients")}>← Back</Button>
        <h2 className="text-2xl font-semibold text-text-primary">New Patient Registration</h2>
      </div>

      {validationError && (
        <div className="p-4 rounded-[8px] bg-error-light text-error-dark border border-error-border text-sm font-medium">
          {validationError}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="space-y-6">
          {/* Demographics */}
          <Card>
            <CardHeader><CardTitle>Demographics</CardTitle></CardHeader>
            <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input label="First Name *" name="firstName" required value={formData.firstName} onChange={handleChange} />
              <Input label="Middle Name" name="middleName" value={formData.middleName} onChange={handleChange} />
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
              <Input label="Passport Number" name="passportNumber" value={formData.passportNumber} onChange={handleChange} />
              <Input label="ABHA ID" name="abhaId" placeholder="14-digit ABHA ID" value={formData.abhaId} onChange={handleChange} />
              <Input label="Religion" name="religion" value={formData.religion} onChange={handleChange} />
              <Input label="Occupation" name="occupation" value={formData.occupation} onChange={handleChange} />
              <Input label="Referred By" name="referredBy" value={formData.referredBy} onChange={handleChange} />
            </CardContent>
          </Card>

          {/* Contact Information */}
          <Card>
            <CardHeader><CardTitle>Contact Information</CardTitle></CardHeader>
            <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input label="Mobile Number *" type="tel" name="mobileNumber" required value={formData.mobileNumber} onChange={handleChange} />
              <Input label="Secondary Mobile" name="secondaryMobile" value={formData.secondaryMobile} onChange={handleChange} />
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
            <div className="flex justify-end pt-6 gap-3 border-t border-border mt-6">
              <Button variant="secondary" onClick={() => router.push("/patients")} type="button">Cancel</Button>
              <Button variant="primary" type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Registering..." : "Register Patient"}
              </Button>
            </div>
          </Card>
        </div>
      </form>

      {/* Duplicate Review Modal */}
      {showDuplicateModal && duplicatePatient && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="bg-background rounded-lg shadow-xl w-full max-w-3xl overflow-hidden flex flex-col max-h-[90vh]">
            <div className="p-4 border-b border-border flex justify-between items-center bg-warning-light/20">
              <h2 className="text-lg font-bold text-text-primary">Possible Duplicate Found</h2>
              <button onClick={() => setShowDuplicateModal(false)} className="text-text-secondary hover:text-text-primary">&times;</button>
            </div>
            
            <div className="p-6 overflow-y-auto grid grid-cols-2 gap-6">
              <div>
                <h3 className="font-semibold text-text-primary border-b border-border pb-2 mb-4">You Entered:</h3>
                <div className="space-y-2 text-sm text-text-secondary">
                  <p><span className="font-medium text-text-primary">Name:</span> {formData.firstName} {formData.lastName}</p>
                  <p><span className="font-medium text-text-primary">DOB:</span> {formData.dateOfBirth}</p>
                  <p><span className="font-medium text-text-primary">Mobile:</span> {formData.mobileNumber}</p>
                </div>
              </div>
              <div>
                <h3 className="font-semibold text-text-primary border-b border-border pb-2 mb-4">Existing Patient:</h3>
                <div className="space-y-2 text-sm text-text-secondary">
                  <p><span className="font-medium text-text-primary">UHID:</span> <span className="font-mono bg-surface px-1 py-0.5 rounded text-xs">{duplicatePatient.uhid}</span></p>
                  <p><span className="font-medium text-text-primary">Name:</span> {duplicatePatient.firstName} {duplicatePatient.lastName}</p>
                  <p><span className="font-medium text-text-primary">DOB:</span> {duplicatePatient.dateOfBirth}</p>
                  <p><span className="font-medium text-text-primary">Mobile:</span> {duplicatePatient.mobileNumber}</p>
                </div>
              </div>
            </div>

            <div className="p-4 border-t border-border bg-surface flex justify-end gap-3">
              <Button variant="secondary" onClick={forceCreatePatient} disabled={isSubmitting}>
                {isSubmitting ? "Wait..." : "Create New Anyway"}
              </Button>
              <Button variant="primary" onClick={() => router.push(`/patients/${duplicatePatient.id}`)}>
                Use Existing Profile
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
