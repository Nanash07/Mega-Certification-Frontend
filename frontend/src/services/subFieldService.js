import api from "./api"; // axios instance

export async function fetchSubFields() {
  const res = await api.get("/subfields");
  return res.data;
}

export async function createSubField(data) {
  const res = await api.post("/subfields", data);
  return res.data;
}

export async function updateSubField(id, data) {
  const res = await api.put(`/subfields/${id}`, data);
  return res.data;
}

export async function deleteSubField(id) {
  await api.delete(`/subfields/${id}`);
}