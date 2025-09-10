// src/pages/dashboard/Dashboard.jsx

import SuperadminDashboard from "./SuperadminDashboard";
import PICDashboard from "./PICDashboard";
import PegawaiDashboard from "./PegawaiDashboard";

export default function Dashboard() {
  // Ambil role dari localStorage atau context (tergantung cara lo simpan)
  const role = localStorage.getItem("role");

  if (role === "SUPERADMIN") return <SuperadminDashboard />;
  if (role === "PIC") return <PICDashboard />;
  // Default fallback pegawai
  return <PegawaiDashboard />;
}
