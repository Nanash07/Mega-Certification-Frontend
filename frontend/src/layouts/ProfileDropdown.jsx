import { useNavigate } from "react-router-dom";
import { useState, useRef, useEffect } from "react";
import { User, Settings, Info, LogOut, ChevronDown } from "lucide-react";
import { handleLogout } from "../utils/logout";

export default function ProfileDropdown() {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);
  const navigate = useNavigate();

  // Ambil nama & email dari localStorage (hasil login)
  const username = localStorage.getItem("username") || "User";
  const email = localStorage.getItem("email") || "-";

  useEffect(() => {
    function handleClickOutside(event) {
      if (ref.current && !ref.current.contains(event.target)) {
        setOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className="relative" ref={ref}>
      {/* Avatar + Username */}
      <button
        className="flex items-center gap-2"
        onClick={() => setOpen((v) => !v)}
      >
        <img
          src="https://randomuser.me/api/portraits/men/75.jpg"
          alt="Avatar"
          className="w-10 h-10 rounded-full border-2 border-gray-200 object-cover"
        />
        {/* Username → hidden di mobile */}
        <span className="hidden sm:inline max-w-[120px] truncate font-medium text-gray-700">
          {username}
        </span>
        <ChevronDown
          size={18}
          className={`transition-transform text-gray-500 ${open ? "rotate-180" : ""}`}
        />
      </button>

      {/* Dropdown */}
      {open && (
        <div className="absolute right-0 mt-3 w-80 max-w-[92vw] bg-white rounded-2xl shadow-xl border border-gray-100 z-50 overflow-hidden">
          {/* Header */}
          <div className="px-6 pt-6 pb-4">
            <div className="font-bold text-lg text-gray-800 truncate">
              {username}
            </div>
            <div className="text-gray-400 text-sm truncate">{email}</div>
          </div>

          {/* Menu */}
          <div className="divide-y divide-gray-100">
            <div className="py-2 px-2 space-y-1">
              <button
                onClick={() => {
                  setOpen(false);
                  navigate("/profile");
                }}
                className="flex items-center gap-3 w-full text-left px-4 py-2 rounded-lg hover:bg-gray-50 text-gray-700"
              >
                <User size={18} /> Edit profile
              </button>
              <button
                className="flex items-center gap-3 w-full text-left px-4 py-2 rounded-lg hover:bg-gray-50 text-gray-700"
              >
                <Settings size={18} /> Account settings
              </button>
              <button
                className="flex items-center gap-3 w-full text-left px-4 py-2 rounded-lg hover:bg-gray-50 text-gray-700"
              >
                <Info size={18} /> Support
              </button>
            </div>
            <div className="py-2">
              <button
                onClick={() => {
                  setOpen(false);
                  handleLogout();
                }}
                className="flex items-center gap-3 w-full text-left px-4 py-2 rounded-lg text-red-500 hover:bg-gray-50"
              >
                <LogOut size={18} /> Sign out
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}