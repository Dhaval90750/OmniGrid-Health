"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Button } from "@/components/ui/Button";
import { useAuthStore } from "@/store/useAuthStore";
import { api } from "@/lib/api";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  
  const router = useRouter();
  const login = useAuthStore((state) => state.login);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      // MVP Testing Bypass: Accept admin/admin since the DB is empty
      if (username === "admin" && password === "admin") {
        const dummyToken = "mock_jwt_token_for_testing";
        login({ id: "1", username: "admin", roles: ["ROLE_ADMIN"] }, dummyToken);
        router.push("/");
        return;
      }

      // Real backend call
      const response = await api.post("/auth/login", { username, password });
      const { token, id, username: uname, roles } = response.data;
      
      login({ id, username: uname, roles }, token);
      router.push("/");
    } catch (err: any) {
      setError(err.response?.data?.message || "Invalid username or password.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-surface p-4">
      <Card className="w-full max-w-md shadow-lg border-none">
        <CardHeader className="text-center pb-2">
          <div className="flex justify-center mb-4">
            <img src="/logo.png" alt="OmniGrid Logo" className="w-12 h-12 object-contain" />
          </div>
          <CardTitle className="text-2xl font-bold text-text-primary">OmniGrid Health</CardTitle>
          <p className="text-sm text-text-secondary mt-1">Sign in to your account</p>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleLogin} className="space-y-4">
            {error && (
              <div className="bg-critical-bg text-error text-sm p-3 rounded-md border border-[#FAD2CF]">
                {error}
              </div>
            )}
            
            <Input 
              label="Username" 
              placeholder="Enter your username" 
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
            
            <Input 
              label="Password" 
              type="password" 
              placeholder="Enter your password" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            
            <Button 
              type="submit" 
              variant="primary" 
              className="w-full mt-6"
              disabled={isLoading}
            >
              {isLoading ? "Signing in..." : "Sign In"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
