/* eslint-disable @next/next/no-img-element */
"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Button } from "@/components/ui/Button";
import { api } from "@/lib/api";

export default function RegisterPage() {
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    firstName: "",
    lastName: "",
    password: "",
    role: "DOCTOR"
  });
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  
  const router = useRouter();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setIsLoading(true);

    try {
      const response = await api.post("/auth/signup", formData);
      setSuccess(response.data.message || "Registration successful! Redirecting to login...");
      setTimeout(() => {
        router.push("/login");
      }, 2000);
    } catch (err: any) {
      setError(err.response?.data?.message || "Registration failed. Please check inputs.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-surface p-4">
      <Card className="w-full max-w-lg shadow-lg border-none">
        <CardHeader className="text-center pb-2">
          <div className="flex justify-center mb-4">
            <img src="/logo.png" alt="OmniGrid Logo" className="w-12 h-12 object-contain" />
          </div>
          <CardTitle className="text-2xl font-bold text-text-primary">OmniGrid Health</CardTitle>
          <p className="text-sm text-text-secondary mt-1">Create a new user/staff account</p>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleRegister} className="space-y-4">
            {error && (
              <div className="bg-critical-bg text-error text-sm p-3 rounded-md border border-[#FAD2CF]">
                {error}
              </div>
            )}
            {success && (
              <div className="bg-success/10 text-success text-sm p-3 rounded-md border border-success/20">
                {success}
              </div>
            )}
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input 
                label="First Name *" 
                name="firstName"
                placeholder="First name" 
                value={formData.firstName}
                onChange={handleChange}
                required
              />
              <Input 
                label="Last Name *" 
                name="lastName"
                placeholder="Last name" 
                value={formData.lastName}
                onChange={handleChange}
                required
              />
            </div>

            <Input 
              label="Username *" 
              name="username"
              placeholder="Choose a username" 
              value={formData.username}
              onChange={handleChange}
              required
            />

            <Input 
              label="Email Address *" 
              name="email"
              type="email"
              placeholder="user@omnigrid.health" 
              value={formData.email}
              onChange={handleChange}
              required
            />

            <Input 
              label="Password *" 
              name="password"
              type="password" 
              placeholder="Enter secure password" 
              value={formData.password}
              onChange={handleChange}
              required
            />

            <div className="flex flex-col gap-1 w-full">
              <label className="text-xs font-medium text-text-secondary">Assigned Role *</label>
              <select 
                name="role" 
                value={formData.role}
                onChange={handleChange}
                required
                className="bg-background border border-border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none focus:border-primary w-full"
              >
                <option value="DOCTOR">Doctor</option>
                <option value="NURSE">Nurse</option>
                <option value="RECEPTIONIST">Receptionist</option>
                <option value="PHARMACIST">Pharmacist</option>
                <option value="LAB_TECHNICIAN">Lab Technician</option>
                <option value="ADMIN">Administrator</option>
              </select>
            </div>
            
            <Button 
              type="submit" 
              variant="primary" 
              className="w-full mt-6"
              disabled={isLoading}
            >
              {isLoading ? "Creating Account..." : "Register Account"}
            </Button>

            <div className="text-center mt-4">
              <button 
                type="button" 
                onClick={() => router.push("/login")}
                className="text-sm text-primary hover:underline"
              >
                Back to Login
              </button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
