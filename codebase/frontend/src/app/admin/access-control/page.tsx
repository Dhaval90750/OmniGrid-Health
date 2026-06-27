"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

const MODULES = [
  "Patient Registration",
  "Admission/ADT",
  "Clinical Notes",
  "Prescriptions",
  "Lab Orders/Results",
  "Radiology",
  "Pharmacy",
  "Billing",
  "Inventory",
  "Operations",
  "Bed Management",
  "Dashboards",
  "Audit Logs",
  "System Config"
];

const ACCESS_LEVELS = [
  { value: "FULL_ACCESS", label: "✅ Full" },
  { value: "READ_ONLY", label: "👁️ Read" },
  { value: "DASHBOARD_ONLY", label: "📊 Dash" },
  { value: "NO_ACCESS", label: "❌ None" }
];

export default function AccessControlMatrix() {
  const [roles, setRoles] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [savingId, setSavingId] = useState<string | null>(null);

  useEffect(() => {
    fetchMatrix();
  }, []);

  const fetchMatrix = async () => {
    try {
      setLoading(true);
      const res = await api.get("/roles/matrix");
      setRoles(res.data);
    } catch (e) {
      console.error(e);
      // Fallback for dev if backend not ready
      setRoles([
        { id: "1", name: "ROLE_ADMIN", permissions: { "Patient Registration": "FULL_ACCESS", "System Config": "FULL_ACCESS" } },
        { id: "2", name: "ROLE_DOCTOR", permissions: { "Clinical Notes": "FULL_ACCESS", "System Config": "NO_ACCESS" } }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handlePermissionChange = (roleId: string, module: string, newLevel: string) => {
    setRoles(roles.map(r => {
      if (r.id === roleId) {
        return {
          ...r,
          permissions: {
            ...r.permissions,
            [module]: newLevel
          }
        };
      }
      return r;
    }));
  };

  const saveRolePermissions = async (roleId: string) => {
    const role = roles.find(r => r.id === roleId);
    if (!role) return;
    
    setSavingId(roleId);
    try {
      await api.put(`/roles/${roleId}/permissions`, { permissions: role.permissions });
      alert(`Permissions for ${role.name.replace('ROLE_', '')} updated successfully!`);
    } catch (e) {
      console.error(e);
      alert("Failed to update permissions.");
    } finally {
      setSavingId(null);
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-text-secondary">Loading matrix...</div>;
  }

  return (
    <div className="max-w-full mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Access Control & Role Matrix</h2>
          <p className="text-text-secondary text-sm">Configure module-level permissions for all staff roles.</p>
        </div>
        <Button variant="outline" onClick={fetchMatrix}>Refresh Data</Button>
      </div>

      <div className="overflow-x-auto rounded-lg border border-border bg-surface shadow-sm">
        <table className="w-full text-left text-sm min-w-max">
          <thead className="bg-surface-hover border-b border-border">
            <tr>
              <th className="p-4 font-semibold text-text-primary sticky left-0 z-10 bg-surface-hover border-r border-border">System Modules</th>
              {roles.map(role => (
                <th key={role.id} className="p-4 font-semibold text-center border-r border-border min-w-[140px]">
                  <div className="mb-2 text-primary">{role.name.replace('ROLE_', '')}</div>
                  <Button 
                    size="sm" 
                    variant="secondary" 
                    onClick={() => saveRolePermissions(role.id)}
                    disabled={savingId === role.id}
                    className="w-full text-xs h-8"
                  >
                    {savingId === role.id ? 'Saving...' : 'Save Role'}
                  </Button>
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-border">
            {MODULES.map(module => (
              <tr key={module} className="hover:bg-surface-hover transition-colors">
                <td className="p-4 font-medium text-text-secondary sticky left-0 z-10 bg-surface border-r border-border">
                  {module}
                </td>
                {roles.map(role => {
                  const currentLevel = role.permissions?.[module] || "NO_ACCESS";
                  return (
                    <td key={`${role.id}-${module}`} className="p-2 text-center border-r border-border">
                      <select
                        value={currentLevel}
                        onChange={(e) => handlePermissionChange(role.id, module, e.target.value)}
                        className={`w-full text-xs font-medium rounded p-1.5 outline-none border ${
                          currentLevel === 'FULL_ACCESS' ? 'bg-success/10 text-success border-success/30' :
                          currentLevel === 'READ_ONLY' ? 'bg-warning/10 text-warning border-warning/30' :
                          currentLevel === 'DASHBOARD_ONLY' ? 'bg-info/10 text-info border-info/30' :
                          'bg-surface-hover text-text-muted border-transparent'
                        }`}
                      >
                        {ACCESS_LEVELS.map(level => (
                          <option key={level.value} value={level.value}>{level.label}</option>
                        ))}
                      </select>
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
