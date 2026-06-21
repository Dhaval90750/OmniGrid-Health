/* eslint-disable @next/next/no-img-element */
import type { Metadata } from "next";
import { Inter } from "next/font/google";
import { LayoutDashboard, Users, Calendar, Settings, LogOut, Stethoscope, Bed, Activity, Scissors, HeartPulse, Pill, FlaskConical, Microscope, Box, BarChart, CreditCard, UserPlus, PenTool } from "lucide-react";
import Link from "next/link";
import "./globals.css";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "OmniGrid Health",
  description: "Enterprise Hospital Information System",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={`${inter.className} antialiased bg-surface text-text-primary min-h-screen flex`}>
        
        {/* Sidebar Navigation */}
        <aside className="w-[260px] bg-background border-r border-border hidden md:flex flex-col">
          <div className="h-16 flex items-center px-6 border-b border-border">
            <div className="flex items-center gap-2 text-primary font-bold text-lg">
              <img src="/logo.png" alt="OmniGrid Logo" className="w-8 h-8 object-contain rounded-md" />
              OmniGrid Health
            </div>
          </div>
          
          <nav className="flex-1 p-4 space-y-1 overflow-y-auto">
            <Link href="/" className="flex items-center gap-3 px-3 py-2 hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <LayoutDashboard size={18} /> Dashboard
            </Link>
            <Link href="/doctor/dashboard" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <Stethoscope size={18} /> Doctor Dashboard
            </Link>
            <Link href="/patients" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <Users size={18} /> Patients
            </Link>
            <Link href="/admissions" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <Bed size={18} /> Admissions
            </Link>
            <Link href="/nursing" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <HeartPulse size={18} /> Nursing & Vitals
            </Link>
            <Link href="/icu" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <Activity size={18} /> ICU
            </Link>
            <Link href="/ot" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <Scissors size={18} /> Operation Theatre
            </Link>
            <Link href="/scribe" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <PenTool size={18} /> AI Scribe
            </Link>
            <Link href="/pharmacy" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <Pill size={18} /> Pharmacy
            </Link>
            <Link href="/lab" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <FlaskConical size={18} /> Laboratory
            </Link>
            <Link href="/radiology" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <Microscope size={18} /> Radiology
            </Link>
            <Link href="/billing" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <CreditCard size={18} /> Billing
            </Link>
            <Link href="/inventory" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <Box size={18} /> Inventory
            </Link>
            <Link href="/staff" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <UserPlus size={18} /> Staff
            </Link>
            <Link href="/analytics" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <BarChart size={18} /> Analytics
            </Link>
            <Link href="/operations" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-surface hover:text-text-primary rounded-md font-medium text-sm transition-colors">
              <Settings size={18} /> Operations
            </Link>
          </nav>
          
          <div className="p-4 border-t border-border">
            <Link href="/login" className="flex items-center gap-3 px-3 py-2 text-text-secondary hover:bg-critical-bg hover:text-error rounded-md font-medium text-sm transition-colors cursor-pointer">
              <LogOut size={18} /> Logout
            </Link>
          </div>
        </aside>

        {/* Main Content Area */}
        <div className="flex-1 flex flex-col min-w-0">
          
          {/* Top Header Bar */}
          <header className="h-16 bg-background border-b border-border shadow-[0_1px_2px_rgba(0,0,0,0.03)] flex items-center justify-between px-8">
            <h1 className="text-lg font-semibold text-text-primary">Overview</h1>
            
            <div className="flex items-center gap-4">
              <div className="w-8 h-8 rounded-full bg-primary-light text-primary flex items-center justify-center font-bold text-sm">
                DR
              </div>
            </div>
          </header>

          {/* Page Content */}
          <main className="flex-1 overflow-y-auto p-8">
            {children}
          </main>
          
        </div>
      </body>
    </html>
  );
}
