import api from "./api";

function serialize(body) {
  return {
    name: (body.name || "").trim(),
    type: (body.type || "").trim(),
    address: (body.address || "").trim(),
    contactPerson: (body.contactPerson || "").trim(),
  };
}

export async function fetchInstitutions() {
  const { data } = await api.get("/institutions");
  return Array.isArray(data) ? data : [];
}

export async function fetchInstitution(id) {
  const { data } = await api.get(`/institutions/${id}`);
  return data;
}

export async function createInstitution(body) {
  const payload = serialize(body);
  const { data } = await api.post("/institutions", payload);
  return data;
}

export async function updateInstitution(id, body) {
  const payload = serialize(body);
  const { data } = await api.put(`/institutions/${id}`, payload);
  return data;
}

export async function deleteInstitution(id) {
  await api.delete(`/institutions/${id}`);
}