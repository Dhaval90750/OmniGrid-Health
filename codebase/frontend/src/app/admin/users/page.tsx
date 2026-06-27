"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function UserManagement() {
  const [users, setUsers] = useState<any[]>([]);
  const [roles, setRoles] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [showRoleModal, setShowRoleModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState<any>(null);
  const [selectedRoles, setSelectedRoles] = useState<string[]>([]);

  useEffect(() => {
    fetchUsers();
    fetchRoles();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const res = await api.get("/users");
      setUsers(res.data);
    } catch (e) {
      console.error(e);
      // Fallback
      setUsers([
        { id: "1", username: "admin", firstName: "System", lastName: "Admin", email: "admin@medcore.com", active: true, roles: ["ROLE_ADMIN"] },
        { id: "2", username: "dr.smith", firstName: "John", lastName: "Smith", email: "smith@medcore.com", active: true, roles: ["ROLE_DOCTOR"] }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const fetchRoles = async () => {
    try {
      const res = await api.get("/roles/matrix");
      setRoles(res.data);
    } catch (e) {
      console.error(e);
      setRoles([
        { name: "ROLE_ADMIN" }, { name: "ROLE_DOCTOR" }, { name: "ROLE_NURSE" }, { name: "ROLE_RECEPTIONIST" }
      ]);
    }
  };

  const handleToggleStatus = async (user: any) => {
    try {
      await api.put(`/users/${user.id}/status`, { active: !user.active });
      setUsers(users.map(u => u.id === user.id ? { ...u, active: !user.active } : u));
    } catch (e) {
      console.error(e);
      alert("Failed to update status");
    }
  };

  const openRoleModal = (user: any) => {
    setSelectedUser(user);
    setSelectedRoles(user.roles || []);
    setShowRoleModal(true);
  };

  const handleRoleToggle = (roleName: string) => {
    if (selectedRoles.includes(roleName)) {
      setSelectedRoles(selectedRoles.filter(r => r !== roleName));
    } else {
      setSelectedRoles([...selectedRoles, roleName]);
    }
  };

  const saveRoles = async () => {
    if (!selectedUser) return;
    try {
      await api.put(`/users/${selectedUser.id}/roles`, { roleNames: selectedRoles });
      setUsers(users.map(u => u.id === selectedUser.id ? { ...u, roles: selectedRoles } : u));
      setShowRoleModal(false);
    } catch (e) {
      console.error(e);
      alert("Failed to update roles");
    }
  };

  const filteredUsers = users.filter(u => 
    u.username.toLowerCase().includes(searchTerm.toLowerCase()) || 
    (u.firstName + " " + u.lastName).toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">System Users</h2>
          <p className="text-text-secondary text-sm">Manage staff accounts, assign roles, and control access.</p>
        </div>
        <div className="flex gap-3">
          <Input 
            placeholder="Search users..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-64"
          />
        </div>
      </div>

      <Card>
        <CardContent className="p-0">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface border-b border-border">
              <tr>
                <th className="p-4 font-medium text-text-secondary">User</th>
                <th className="p-4 font-medium text-text-secondary">Username</th>
                <th className="p-4 font-medium text-text-secondary">Roles</th>
                <th className="p-4 font-medium text-text-secondary">Status</th>
                <th className="p-4 font-medium text-text-secondary text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {loading ? (
                <tr><td colSpan={5} className="p-8 text-center text-text-secondary">Loading users...</td></tr>
              ) : filteredUsers.map((user) => (
                <tr key={user.id} className="hover:bg-surface-hover">
                  <td className="p-4">
                    <div className="font-semibold text-text-primary">{user.firstName} {user.lastName}</div>
                    <div className="text-xs text-text-secondary">{user.email}</div>
                  </td>
                  <td className="p-4 font-mono">{user.username}</td>
                  <td className="p-4">
                    <div className="flex flex-wrap gap-1">
                      {user.roles && user.roles.map((r: string) => (
                        <Badge key={r} variant="secondary" className="text-[10px]">
                          {r.replace('ROLE_', '')}
                        </Badge>
                      ))}
                    </div>
                  </td>
                  <td className="p-4">
                    {user.active 
                      ? <Badge variant="success">Active</Badge> 
                      : <Badge variant="default" className="bg-gray-200 text-gray-700">Inactive</Badge>
                    }
                  </td>
                  <td className="p-4 text-right">
                    <Button variant="secondary" size="sm" className="mr-2" onClick={() => openRoleModal(user)}>
                      Manage Roles
                    </Button>
                    <Button 
                      variant={user.active ? "outline" : "default"} 
                      size="sm"
                      onClick={() => handleToggleStatus(user)}
                      className={user.active ? "text-error border-error/30 hover:bg-error/10" : ""}
                    >
                      {user.active ? 'Suspend' : 'Activate'}
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>

      {showRoleModal && selectedUser && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-[450px]">
            <CardHeader>
              <CardTitle>Assign Roles to {selectedUser.firstName}</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-3 mt-4">
                {roles.map(role => (
                  <label key={role.name} className="flex items-center gap-3 p-3 rounded border border-border hover:bg-surface cursor-pointer">
                    <input 
                      type="checkbox" 
                      checked={selectedRoles.includes(role.name)}
                      onChange={() => handleRoleToggle(role.name)}
                      className="w-4 h-4 rounded text-primary"
                    />
                    <div>
                      <div className="font-medium">{role.name.replace('ROLE_', '')}</div>
                      <div className="text-xs text-text-secondary text-wrap">{role.description || "System Role"}</div>
                    </div>
                  </label>
                ))}
              </div>
              <div className="flex justify-end gap-3 mt-6">
                <Button variant="secondary" onClick={() => setShowRoleModal(false)}>Cancel</Button>
                <Button onClick={saveRoles}>Save Assignments</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
