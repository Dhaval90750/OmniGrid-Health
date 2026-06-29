import type { Metadata } from "next";
import { Inter } from "next/font/google";
import ClientLayout from "@/components/layout/ClientLayout";
import "./globals.css";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "MedcoreHIS",
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
        <ClientLayout>{children}</ClientLayout>
      </body>
    </html>
  );
}
