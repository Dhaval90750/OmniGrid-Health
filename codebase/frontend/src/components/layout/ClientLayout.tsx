"use client";

import { usePathname, useRouter } from "next/navigation";
import { 
  LayoutDashboard, Users, Settings, LogOut, 
  Activity, ShieldAlert, HeartPulse, FileText, 
  Syringe, FlaskConical, Image, Pill, 
  Receipt, ClipboardList, Database, Truck, 
  UserCheck, Droplet, Mic, BarChart3
} from "lucide-react";
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

  // Render auth pages without the sidebar
  if (pathname === "/login" || pathname === "/register") {
    return <>{children}</>;
  }

  const isActive = (path: string) => pathname === path;
  const linkClass = (path: string) => 
    `flex items-center gap-3 px-3 py-1.5 rounded-md font-medium text-xs transition-colors ${
      isActive(path) 
        ? "bg-primary-light text-primary-dark font-semibold" 
        : "text-text-secondary hover:bg-surface hover:text-text-primary"
    }`;

  return (
    <>
      {/* Sidebar Navigation */}
      <aside className="w-[280px] bg-background border-r border-border hidden md:flex flex-col h-screen shrink-0">
        <div className="h-16 flex items-center px-6 border-b border-border shrink-0">
          <div className="flex items-center gap-2 text-primary font-bold text-lg">
            <img src="/logo.png" alt="OmniGrid Logo" className="w-8 h-8 object-contain rounded-md" />
            OmniGrid Health
          </div>
        </div>
        
        <nav className="flex-1 p-4 space-y-4 overflow-y-auto">
          {/* Main & Clinical */}
          <div>
            <div className="px-3 mb-1 text-[10px] font-bold text-text-tertiary uppercase tracking-wider">Clinical</div>
            <div className="space-y-0.5">
              <Link href="/" className={linkClass("/")}>
                <LayoutDashboard size={14} /> Dashboard
              </Link>
              <Link href="/patients" className={linkClass("/patients")}>
                <Users size={14} /> Patients
              </Link>
              <Link href="/doctor/dashboard" className={linkClass("/doctor/dashboard")}>
                <UserCheck size={14} /> Doctor Dashboard
              </Link>
              <Link href="/scribe" className={linkClass("/scribe")}>
                <Mic size={14} /> AI Scribe
              </Link>
              <Link href="/icu" className={linkClass("/icu")}>
                <HeartPulse size={14} /> ICU
              </Link>
              <Link href="/ot" className={linkClass("/ot")}>
                <Activity size={14} /> Operating Theater (OT)
              </Link>
              <Link href="/nursing" className={linkClass("/nursing")}>
                <Syringe size={14} /> Nursing Workflow
              </Link>
            </div>
          </div>

          {/* Departments */}
          <div>
            <div className="px-3 mb-1 text-[10px] font-bold text-text-tertiary uppercase tracking-wider">Departments</div>
            <div className="space-y-0.5">
              <Link href="/lab" className={linkClass("/lab")}>
                <FlaskConical size={14} /> Laboratory (LIS)
              </Link>
              <Link href="/radiology" className={linkClass("/radiology")}>
                <Image size={14} /> Radiology (RIS)
              </Link>
              <Link href="/pharmacy" className={linkClass("/pharmacy")}>
                <Pill size={14} /> Pharmacy
              </Link>
              <Link href="/auxiliary" className={linkClass("/auxiliary")}>
                <Droplet size={14} /> Blood Bank
              </Link>
            </div>
          </div>

          {/* Administrative */}
          <div>
            <div className="px-3 mb-1 text-[10px] font-bold text-text-tertiary uppercase tracking-wider">Administrative</div>
            <div className="space-y-0.5">
              <Link href="/admissions" className={linkClass("/admissions")}>
                <ShieldAlert size={14} /> Admissions / ADT
              </Link>
              <Link href="/discharge" className={linkClass("/discharge")}>
                <FileText size={14} /> Discharge
              </Link>
              <Link href="/billing" className={linkClass("/billing")}>
                <Receipt size={14} /> Billing & Revenue
              </Link>
              <Link href="/inventory" className={linkClass("/inventory")}>
                <Database size={14} /> Inventory / Pharmacy Stock
              </Link>
              <Link href="/operations" className={linkClass("/operations")}>
                <Truck size={14} /> Operations / Housekeeping
              </Link>
              <Link href="/staff" className={linkClass("/staff")}>
                <ClipboardList size={14} /> Staff / Roster
              </Link>
              <Link href="/analytics" className={linkClass("/analytics")}>
                <BarChart3 size={14} /> Reports & Analytics
              </Link>
            </div>
          </div>
        </nav>
        
        <div className="p-4 border-t border-border shrink-0">
          <div 
            onClick={handleLogout}
            className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-critical-bg hover:text-error rounded-md font-medium text-sm transition-colors cursor-pointer"
          >
            <LogOut size={16} /> Logout
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
