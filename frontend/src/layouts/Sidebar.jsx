import { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import {
  LayoutDashboard,
  User,
  Users,
  ListChecks,
  ChevronDown,
  ChevronUp,
  BadgeCheck,
  Bell,
  FileText,
  Layers,
  ClipboardList,
  Settings,
} from "lucide-react";

// ================== MENU MASTER ==================
export const MENU = [
  // 1. Dashboard & Monitoring
  {
    label: "Dashboard",
    icon: <LayoutDashboard size={18} />,
    href: "/dashboard",
    key: "dashboard",
  },
  /*{
    label: "Reminder / Notifikasi",
    icon: <Bell size={18} />,
    href: "/reminder",
    key: "reminder",
  },
  {
    label: "Reports",
    icon: <FileText size={18} />,
    href: "/reports",
    key: "reports",
  },*/

  // 2. Data Pegawai & Organization
  {
    label: "Employee",
    icon: <User size={18} />,
    key: "employee",
    subMenu: [
      { label: "Data Pegawai", href: "/employee/data" },
      { label: "Eligibility", href: "/employee/eligibility" },
      { label: "Eligibility Manual", href: "/employee/exception" },
      /*{ label: "Tracking Sertifikasi", href: "/employee/certification" },*/
    ],
  },
  {
    label: "Organization",
    icon: <Users size={18} />,
    key: "organization",
    subMenu: [
      { label: "Regional", href: "/organization/regional" },
      { label: "Division", href: "/organization/division" },
      { label: "Unit", href: "/organization/unit" },
      { label: "Job Position", href: "/organization/job-position" },
    ],
  },

  // 3. Master Sertifikasi
  {
    label: "Sertifikasi",
    icon: <BadgeCheck size={18} />,
    key: "sertifikasi",
    subMenu: [
      { label: "Aturan Sertifikasi", href: "/sertifikasi/aturan-sertifikat" },
      { label: "Jenis", href: "/sertifikasi/jenis" },
      { label: "Jenjang", href: "/sertifikasi/jenjang" },
      { label: "Sub Bidang", href: "/sertifikasi/sub-bidang" },
      { label: "Lembaga", href: "/sertifikasi/lembaga" },
    ],
  },

  // 4. Mapping & Scope
  {
    label: "Mapping",
    icon: <ListChecks size={18} />,
    key: "mapping",
    subMenu: [
      { label: "Mapping Jabatan Sertifikasi", href: "/mapping/job-certification" },
      { label: "PIC Scope", href: "/mapping/pic-certification-scope" },
    ],
  },

  // 5. Batch Sertifikasi
  /*{
    label: "Batch Sertifikasi",
    icon: <ClipboardList size={18} />,
    key: "batch",
    subMenu: [
      { label: "Daftar Batch", href: "/batch" },
      { label: "Registrasi Pegawai", href: "/batch/registrasi" },
    ],
  },*/

  // 6. User & Role Management
  {
    label: "Manajemen User",
    icon: <Settings size={18} />,
    href: "/user",
    key: "user",
  },
];

// ======== ROLE-BASED FILTERING ========
const filterMenuByRole = (menu, roleRaw) => {
  const role = (roleRaw || "").toUpperCase();
  if (role === "SUPERADMIN") return menu;

  if (role === "PIC") {
    return menu
      .filter((item) => item.key !== "user") // hide Manajemen User untuk PIC
      .map((item) => {
        if (item.key === "sertifikasi") {
          return {
            ...item,
            subMenu: item.subMenu.filter(
              (sub) => sub.label !== "Jenjang" && sub.label !== "Jenis"
            ),
          };
        }
        return item;
      });
  }

  // Default (PEGAWAI / unknown): cuma menu personal
  return menu.filter(
    (item) =>
      item.key === "dashboard" ||
      item.key === "employee" ||
      item.key === "reminder"
  );
};

export default function Sidebar({ open, setOpen }) {
  const location = useLocation();
  const [openMenu, setOpenMenu] = useState("");

  // Ambil role dari storage (fleksibel)
  const role =
    (JSON.parse(localStorage.getItem("user") || "{}").role ||
      localStorage.getItem("role") ||
      "").toString().toUpperCase();

  const visibleMenu = filterMenuByRole(MENU, role);

  // Auto expand parent submenu kalau route ada di salah satu anaknya
  useEffect(() => {
    const parent = visibleMenu.find(
      (m) => m.subMenu && m.subMenu.some((s) => location.pathname.startsWith(s.href))
    );
    if (parent) setOpenMenu(parent.key);
  }, [location.pathname, visibleMenu]);

  const handleMenuClick = (key) => setOpenMenu((prev) => (prev === key ? "" : key));
  const handleLinkClick = () => {
    if (window.innerWidth < 1024) setOpen(false);
  };

  // Active states
  const isActive = (href) =>
    location.pathname === href || location.pathname.startsWith(href + "/");
  const isParentActive = (submenu) => submenu.some((sub) => isActive(sub.href));
  const isMenuActive = (item) =>
    item.subMenu ? openMenu === item.key || isParentActive(item.subMenu) : isActive(item.href);

  return (
    <>
      {open && <div className="fixed inset-0 z-30 lg:hidden" onClick={() => setOpen(false)} />}
      <aside
        className={`
          fixed z-40 top-0 left-0 h-full w-56 bg-white shadow-sm
          flex flex-col transition-transform duration-300
          ${open ? "translate-x-0" : "-translate-x-full"}
          border-r border-gray-200
          lg:translate-x-0 lg:static
        `}
      >
        <div className="flex items-center h-20 px-6 border-b border-gray-200">
          <Link to="/dashboard" className="font-bold text-2xl" onClick={handleLinkClick}>
            Mega Certification
          </Link>
        </div>

        <nav className="flex-1 overflow-y-auto pt-6 px-2 space-y-1">
          {visibleMenu.map((item) =>
            item.subMenu ? (
              <div key={item.key} className="mb-2">
                <button
                  onClick={() => handleMenuClick(item.key)}
                  className={`btn w-full justify-start gap-3 mb-2 text-xs ${
                    isMenuActive(item) ? "btn-primary" : "btn-ghost"
                  }`}
                >
                  {item.icon}
                  <span className="flex-1 text-left text-xs">{item.label}</span>
                  {openMenu === item.key || isParentActive(item.subMenu) ? (
                    <ChevronUp size={16} />
                  ) : (
                    <ChevronDown size={16} />
                  )}
                </button>

                {(openMenu === item.key || isParentActive(item.subMenu)) && (
                  <div className="ml-4 space-y-1">
                    {item.subMenu.map((sub, idx) => (
                      <Link
                        key={idx}
                        to={sub.href}
                        className={`btn w-full justify-start text-xs ${
                          isActive(sub.href) ? "btn-primary" : "btn-ghost"
                        }`}
                        onClick={handleLinkClick}
                      >
                        {sub.label}
                      </Link>
                    ))}
                  </div>
                )}
              </div>
            ) : (
              <div key={item.key} className="mb-2">
                <Link
                  to={item.href}
                  className={`btn w-full justify-start gap-3 text-xs ${
                    isMenuActive(item) ? "btn-primary" : "btn-ghost"
                  }`}
                  onClick={handleLinkClick}
                >
                  {item.icon}
                  {item.label}
                </Link>
              </div>
            )
          )}
        </nav>
      </aside>
    </>
  );
}