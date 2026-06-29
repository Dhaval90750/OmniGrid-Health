"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { api } from "@/lib/api";

export default function AdminSettings() {
  const [settings, setSettings] = useState({
    "stripe.api.key": "",
    "twilio.account.sid": "",
    "twilio.auth.token": "",
    "twilio.phone.number": "",
    "abdm.sandbox.url": "",
    "llm.api.key": "",
    "llm.api.url": "",
    "llm.model": ""
  });
  
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    fetchSettings();
  }, []);

  const fetchSettings = async () => {
    try {
      // For local demo, we might use a mock response if the backend isn't up
      const data = await api.get("/admin/settings").catch(() => ({}));
      
      setSettings(prev => ({
        ...prev,
        ...data
      }));
    } catch (err) {
      console.error("Failed to load settings", err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSettings({ ...settings, [e.target.name]: e.target.value });
  };

  const handleSave = async () => {
    setIsSaving(true);
    setMessage("");
    try {
      await api.post("/admin/settings", settings);
      setMessage("Settings saved successfully! Changes are applied immediately without server restart.");
    } catch (err) {
      setMessage("Failed to save settings. Check backend connection.");
    } finally {
      setIsSaving(false);
    }
  };

  if (isLoading) {
    return <div className="p-8 text-center">Loading system configuration...</div>;
  }

  return (
    <div className="max-w-4xl mx-auto py-8 space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-text-primary">System Configuration</h1>
        <p className="text-text-secondary mt-2">Manage API keys and integration endpoints. Changes take effect in real-time.</p>
      </div>
      
      {message && (
        <div className={`p-4 rounded-md font-medium ${message.includes("success") ? "bg-success/20 text-success" : "bg-danger/20 text-danger"}`}>
          {message}
        </div>
      )}

      <Card>
        <CardHeader>
          <CardTitle>Payment Gateway (Stripe)</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Stripe Secret API Key</label>
            <input 
              type="password" 
              name="stripe.api.key"
              value={settings["stripe.api.key"]}
              onChange={handleChange}
              className="w-full p-2 border rounded-md"
              placeholder="sk_test_..."
            />
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>SMS Notifications (Twilio)</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Account SID</label>
            <input 
              type="text" 
              name="twilio.account.sid"
              value={settings["twilio.account.sid"]}
              onChange={handleChange}
              className="w-full p-2 border rounded-md"
              placeholder="AC..."
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Auth Token</label>
            <input 
              type="password" 
              name="twilio.auth.token"
              value={settings["twilio.auth.token"]}
              onChange={handleChange}
              className="w-full p-2 border rounded-md"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">From Phone Number</label>
            <input 
              type="text" 
              name="twilio.phone.number"
              value={settings["twilio.phone.number"]}
              onChange={handleChange}
              className="w-full p-2 border rounded-md"
              placeholder="+1234567890"
            />
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Government Integrations (ABDM Sandbox)</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">ABDM Gateway URL</label>
            <input 
              type="text" 
              name="abdm.sandbox.url"
              value={settings["abdm.sandbox.url"]}
              onChange={handleChange}
              className="w-full p-2 border rounded-md"
              placeholder="https://dev.abdm.gov.in/gateway/v0.5"
            />
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>AI & Clinical NLP Engine (LLM)</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">API Key</label>
            <input 
              type="password" 
              name="llm.api.key"
              value={settings["llm.api.key"]}
              onChange={handleChange}
              className="w-full p-2 border rounded-md"
              placeholder="sk-..."
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">API URL (Chat Completions)</label>
            <input 
              type="text" 
              name="llm.api.url"
              value={settings["llm.api.url"]}
              onChange={handleChange}
              className="w-full p-2 border rounded-md"
              placeholder="https://api.openai.com/v1/chat/completions"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Model Name</label>
            <input 
              type="text" 
              name="llm.model"
              value={settings["llm.model"]}
              onChange={handleChange}
              className="w-full p-2 border rounded-md"
              placeholder="gpt-4o"
            />
          </div>
        </CardContent>
      </Card>

      <div className="flex justify-end pt-4">
        <Button variant="primary" size="lg" onClick={handleSave} disabled={isSaving}>
          {isSaving ? "Saving..." : "Save Configuration"}
        </Button>
      </div>

    </div>
  );
}
