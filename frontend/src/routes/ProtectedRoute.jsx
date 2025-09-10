// src/pages/routes/ProtectedRoute.jsx
import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

const isTokenExpired = (token) => {
  try {
    const { exp } = jwtDecode(token);
    if (!exp) return false;
    return exp < Math.floor(Date.now() / 1000);
  } catch {
    return true;
  }
};

const extractRoles = (payload) => {
  // dukung berbagai bentuk claim
  let raw =
    payload?.roles ??
    payload?.authorities ??
    payload?.role ??
    payload?.scope ??
    payload?.scopes;

  if (!raw) return [];
  if (Array.isArray(raw)) return raw.map((r) => String(r).replace(/^ROLE_/, "").toUpperCase());
  if (typeof raw === "string") {
    // bisa "ROLE_SUPERADMIN" atau "SUPERADMIN" atau "ROLE_A,ROLE_B"
    return raw
      .split(/[,\s]+/)
      .filter(Boolean)
      .map((r) => r.replace(/^ROLE_/, "").toUpperCase());
  }
  return [];
};

export default function ProtectedRoute({ children, roles }) {
  const token = localStorage.getItem("token");
  if (!token || isTokenExpired(token)) {
    localStorage.removeItem("token");
    return <Navigate to="/login" replace />;
  }

  // kalau butuh cek role tertentu
  if (roles?.length) {
    try {
      const payload = jwtDecode(token);
      const userRoles = extractRoles(payload);
      const allowed = userRoles.some((r) => roles.includes(r));
      if (!allowed) return <Navigate to="/dashboard" replace />;
    } catch {
      return <Navigate to="/login" replace />;
    }
  }

  return children;
}