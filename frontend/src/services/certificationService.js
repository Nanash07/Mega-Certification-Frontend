// src/services/certificationService.js
import api from "./api"; // pastiin api.js export default axios instance

function serialize(body) {
  return {
    name: (body.name || "").trim(),
    code: (body.code || "").trim(),
    isWajib6bln: !!body.isWajib6bln,
    masaBerlaku: Number(body.masaBerlaku ?? 0),
    reminderMonth: Number(body.reminderMonth ?? 0)
  };
}

export async function fetchCertifications() {
  const { data } = await api.get("/certifications");
  return Array.isArray(data) ? data : data?.content || [];
}

export async function fetchCertification(id) {
  const { data } = await api.get(`/certifications/${id}`);
  return data;
}

export async function createCertification(body) {
  const payload = serialize(body);
  const { data } = await api.post("/certifications", payload);
  return data;
}

export async function updateCertification(id, body) {
  const payload = serialize(body);
  const { data } = await api.put(`/certifications/${id}`, payload);
  return data;
}

export async function deleteCertification(id) {
  await api.delete(`/certifications/${id}`);
}
