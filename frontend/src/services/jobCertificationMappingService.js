import api from "./api";

const BASE_URL = "/job-certification-mappings";

// 🔹 Ambil data mapping dengan paging + filter
export async function fetchJobCertificationMappingsPaged(params) {
  try {
    const { data } = await api.get(`${BASE_URL}/paged`, { params });
    return data || { content: [], totalPages: 0, totalElements: 0 };
  } catch (err) {
    console.error("❌ fetchJobCertificationMappingsPaged error:", err);
    return { content: [], totalPages: 0, totalElements: 0 };
  }
}

// 🔹 Ambil detail mapping
export async function fetchJobCertificationMappingById(id) {
  try {
    const { data } = await api.get(`${BASE_URL}/${id}`);
    return data || null;
  } catch (err) {
    console.error("❌ fetchJobCertificationMappingById error:", err);
    return null;
  }
}

// 🔹 Create mapping
export async function createJobCertificationMapping(payload) {
  try {
    const { data } = await api.post(BASE_URL, payload);
    return data;
  } catch (err) {
    console.error("❌ createJobCertificationMapping error:", err);
    throw err;
  }
}

// 🔹 Update mapping
export async function updateJobCertificationMapping(id, payload) {
  try {
    const { data } = await api.put(`${BASE_URL}/${id}`, payload);
    return data;
  } catch (err) {
    console.error("❌ updateJobCertificationMapping error:", err);
    throw err;
  }
}

// 🔹 Toggle aktif/nonaktif
export async function toggleJobCertificationMapping(id) {
  try {
    const { data } = await api.put(`${BASE_URL}/${id}/toggle`);
    return data;
  } catch (err) {
    console.error("❌ toggleJobCertificationMapping error:", err);
    throw err;
  }
}

// 🔹 Soft delete
export async function deleteJobCertificationMapping(id) {
  try {
    await api.delete(`${BASE_URL}/${id}`);
    return true;
  } catch (err) {
    console.error("❌ deleteJobCertificationMapping error:", err);
    throw err;
  }
}