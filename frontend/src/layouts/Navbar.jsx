import { Menu, Bell } from "lucide-react";
import { useLocation } from "react-router-dom";
import ProfileDropdown from "./ProfileDropdown";
import { MENU } from "./Sidebar"; // langsung ambil dari Sidebar.jsx

const getMenuTitle = (pathname) => {
  for (const item of MENU) {
    if (item.subMenu) {
      const sub = item.subMenu.find((s) => pathname.startsWith(s.href));
      if (sub) return sub.label; // kalau submenu ketemu
    } else if (pathname.startsWith(item.href)) {
      return item.label; // kalau parent menu biasa
    }
  }
  return "Dashboard"; // default
};

const Navbar = ({ onMenuClick }) => {
  const location = useLocation();
  const title = getMenuTitle(location.pathname);

  return (
    <header className="flex items-center h-20 px-4 lg:px-8 bg-white border-b border-gray-200">
      {/* Sidebar toggle */}
      <button
        className="btn btn-ghost btn-square border border-gray-200 lg:hidden"
        onClick={onMenuClick}
      >
        <Menu size={24} className="text-gray-400" />
      </button>

      {/* Title kiri */}
      <h1 className="ml-4 font-semibold text-lg">{title}</h1>

      {/* Right */}
      <div className="flex items-center gap-4 ml-auto">
        <button className="btn btn-ghost btn-circle border border-gray-200 relative">
          <Bell size={22} className="text-gray-400" />
          <span className="absolute top-2 right-2 w-2 h-2 bg-orange-400 rounded-full ring-2 ring-white"></span>
        </button>
        <ProfileDropdown />
      </div>
    </header>
  );
};

export default Navbar;