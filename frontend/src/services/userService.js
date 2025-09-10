import api from "./api";

// ambil list user
export async function fetchUsers(params = {}) {
  const { data } = await api.get("/users", { params });
  return data;
}

// ambil detail user
export async function fetchUserDetail(id) {
  const { data } = await api.get(`/users/${id}`);
  return data;
}

// create user
export async function createUser(payload) {
  const { data } = await api.post("/users", payload);
  return data;
}

// update user
export async function updateUser(id, payload) {
  const { data } = await api.put(`/users/${id}`, payload);
  return data;
}

// delete user (soft delete di BE)
export async function deleteUser(id) {
  await api.delete(`/users/${id}`);
}