"use client";

/* eslint-disable @next/next/no-img-element */
import { usePathname, useRouter } from "next/navigation";
import { LayoutDashboard, Users, Calendar, Settings, LogOut } from "lucide-react";
import { useAuthStore } from "@/store/useAuthStore";
import Link from "next/link";

export default function ClientLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const logout = useAuthStore((state) => state.logout);
  const user = useAuthStore((state) => state.user);

  const handleLogout = () => {
    logout();
    router.push("/login");
  };

  // If we are on the login page, render only the children without the sidebar
  if (pathname === "/login") {
    return <>{children}</>;
  }

  return (
    <>
      {/* Sidebar Navigation */}
      <aside className="w-[260px] bg-background border-r border-border hidden md:flex flex-col">
        <div className="h-16 flex items-center px-6 border-b border-border">
          <div className="flex items-center gap-2 text-primary font-bold text-lg">
            <img src="/logo.png" alt="OmniGrid Logo" className="w-8 h-8 object-contain rounded-md" />
            OmniGrid Health
          </div>
        </div>
        
        <nav className="flex-1 p-4 space-y-1 overflow-y-auto">
          <Link href="/" className="flex items-center gap-3 px-3 py-2 bg-primary-light text-primary-dark rounded-md font-medium text-sm">
            <LayoutDashboard size={18} /> Dashboard
          </Link>
          <Link href="/patients" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
            <Users size={18} /> Patients
          </Link>
          <Link href="/appointments" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
            <Calendar size={18} /> Appointments
          </Link>
          <Link href="/settings" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
            <Settings size={18} /> Settings
          </Link>
        </nav>
        
        <div className="p-4 border-t border-border">
          <div 
            onClick={handleLogout}
            className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-critical-bg hover:text-error rounded-md font-medium text-sm transition-colors cursor-pointer"
          >
            <LogOut size={18} /> Logout
          </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0">
        
        {/* Top Header Bar */}
        <header className="h-16 bg-background border-b border-border shadow-[0_1px_2px_rgba(0,0,0,0.03)] flex items-center justify-between px-8">
          <h1 className="text-lg font-semibold text-text-primary">Overview</h1>
          
          <div className="flex items-center gap-4">
            <div className="w-8 h-8 rounded-full bg-primary-light text-primary flex items-center justify-center font-bold text-sm uppercase">
              {user ? user.username.substring(0, 2) : "OG"}
            </div>
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 overflow-y-auto p-8">
          {children}
        </main>
        
      </div>
    </>
  );
}
