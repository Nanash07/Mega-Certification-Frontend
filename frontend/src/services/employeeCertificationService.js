import api from "./api";

const BASE_URL = "/employee-certifications"; // ✅ harus pakai /api

// ================== FETCH DATA ==================
export async function fetchCertifications(params = {}) {
  try {
    const { data } = await api.get(BASE_URL, { params });
    return {
      content: data.content || [],
      totalPages: data.totalPages || 1,
      totalElements: data.totalElements || 0,
    };
  } catch (err) {
    console.error("fetchCertifications error:", err);
    return { content: [], totalPages: 0, totalElements: 0 };
  }
}

// ================== CRUD ==================
export async function createCertification(payload) {
  try {
    const { data } = await api.post(BASE_URL, payload);
    return data;
  } catch (err) {
    console.error("createCertification error:", err);
    throw err;
  }
}

export async function updateCertification(id, payload) {
  try {
    const { data } = await api.put(`${BASE_URL}/${id}`, payload);
    return data;
  } catch (err) {
    console.error("updateCertification error:", err);
    throw err;
  }
}

export async function deleteCertification(id) {
  try {
    await api.delete(`${BASE_URL}/${id}`);
    return true;
  } catch (err) {
    console.error("deleteCertification error:", err);
    throw err;
  }
}

// ================== UPLOAD ==================
export async function uploadCertificateFile(id, file) {
  const formData = new FormData();
  formData.append("file", file);
  try {
    const { data } = await api.post(`${BASE_URL}/${id}/upload`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return data;
  } catch (err) {
    console.error("uploadCertificateFile error:", err);
    throw err;
  }
}