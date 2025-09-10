// src/services/certificationLevelService.js
import api from "./api";

function serialize(body) {
  return {
    certificationId: Number(body.certificationId ?? 0),
    level: Number(body.level ?? 0),
    name: (body.name || "").trim(),
  };
}

export async function fetchCertificationLevels() {
  const { data } = await api.get("/certification-levels");
  return Array.isArray(data) ? data : data?.content || [];
}

export async function fetchCertificationLevel(id) {
  const { data } = await api.get(`/certification-levels/${id}`);
  return data;
}

export async function createCertificationLevel(body) {
  const payload = serialize(body);
  const { data } = await api.post("/certification-levels", payload);
  return data;
}

export async function updateCertificationLevel(id, body) {
  const payload = serialize(body);
  const { data } = await api.put(`/certification-levels/${id}`, payload);
  return data;
}

export async function deleteCertificationLevel(id) {
  await api.delete(`/certification-levels/${id}`);
}