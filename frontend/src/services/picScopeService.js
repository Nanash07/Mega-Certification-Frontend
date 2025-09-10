import api from "./api";

// Ambil semua PIC + scope
export async function fetchPicScopes() {
  const { data } = await api.get("/pic-scope");
  return data;
}

// Ambil scope berdasarkan userId
export async function fetchPicScopesByUser(userId) {
  const { data } = await api.get(`/pic-scope/${userId}`);
  return data;
}

// Assign ulang scope untuk PIC tertentu
export async function assignPicScope(userId, certificationIds) {
  const { data } = await api.put(`/pic-scope/${userId}`, {
    certificationIds,
  });
  return data;
}