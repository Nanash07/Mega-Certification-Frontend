// src/services/roleService.js
import api from "./api";

/**
 * fetchRoles()
 * Backend bisa balikin array atau {content:[]}
 */
export async function fetchRoles() {
  const { data } = await api.get("/roles");
  if (Array.isArray(data)) return data;
  if (data?.content && Array.isArray(data.content)) return data.content;
  return [];
}

// (opsional) CRUD role kalau dibutuhin nanti
export async function createRole(payload) {
  const { data } = await api.post("/roles", payload);
  return data;
}
export async function updateRole(id, payload) {
  const { data } = await api.put(`/roles/${id}`, payload);
  return data;
}
export async function deleteRole(id) {
  await api.delete(`/roles/${id}`);
}
