import { useState } from "react";
import { Toaster } from "react-hot-toast";
import Sidebar from "./Sidebar";
import Navbar from "./Navbar";

export default function MainLayout({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="h-screen w-full flex bg-slate-50">
      {/* sidebar kiri */}
      <Sidebar open={sidebarOpen} setOpen={setSidebarOpen} />

      {/* area kanan */}
      <div className="flex-1 min-w-0 flex flex-col">
        <Navbar onMenuClick={() => setSidebarOpen(v => !v)} />
        <main className="flex-1 overflow-auto p-6">{children}</main>
      </div>

      {/* toaster: cukup sekali di layout */}
      <Toaster position="top-right" toastOptions={{ duration: 3000 }} />
    </div>
  );
}
