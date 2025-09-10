// src/utils/logout.js
export function handleLogout() {
  localStorage.removeItem("token");
  localStorage.removeItem("role"); // kalau ada
  // Tambah bersihin state/apa aja yang lo perlu
  window.location.href = "/login";
}