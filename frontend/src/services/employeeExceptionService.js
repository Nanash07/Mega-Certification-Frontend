import api from "./api";

const BASE_URL = "/exceptions";

// ================== FETCH DATA ==================

// 🔹 Ambil exception dengan paging + filter
export async function fetchExceptions(params = {}) {
  try {
    const { data } = await api.get(BASE_URL, { params });
    return data || { content: [], totalPages: 0, totalElements: 0 };
  } catch (err) {
    console.error("fetchExceptions error:", err);
    return { content: [], totalPages: 0, totalElements: 0 };
  }
}

// ================== CRUD ==================

// 🔹 Create exception (pakai body JSON)
export async function createException(payload) {
  try {
    const { data } = await api.post(BASE_URL, payload);
    return data;
  } catch (err) {
    console.error("createException error:", err);
    throw err;
  }
}

// 🔹 Update notes exception
export async function updateException(id, notes) {
  try {
    const { data } = await api.put(`${BASE_URL}/${id}/notes`, null, {
      params: { notes },
    });
    return data;
  } catch (err) {
    console.error("updateException error:", err);
    throw err;
  }
}

// 🔹 Toggle aktif/nonaktif exception
export async function toggleException(id) {
  try {
    const { data } = await api.put(`${BASE_URL}/${id}/toggle`);
    return data;
  } catch (err) {
    console.error("toggleException error:", err);
    throw err;
  }
}

// 🔹 Soft delete exception
export async function deleteException(id) {
  try {
    await api.delete(`${BASE_URL}/${id}`);
    return true;
  } catch (err) {
    console.error("deleteException error:", err);
    throw err;
  }
}

// ================== IMPORT ==================

const IMPORT_BASE = `${BASE_URL}/import`;

// 🔹 Dry Run Import
export async function dryRunImportExceptions(file) {
  const formData = new FormData();
  formData.append("file", file);
  try {
    const { data } = await api.post(`${IMPORT_BASE}/dry-run`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return data;
  } catch (err) {
    console.error("dryRunImportExceptions error:", err);
    throw err;
  }
}

// 🔹 Confirm Import
export async function confirmImportExceptions(file) {
  const formData = new FormData();
  formData.append("file", file);
  try {
    const { data } = await api.post(`${IMPORT_BASE}/confirm`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return data;
  } catch (err) {
    console.error("confirmImportExceptions error:", err);
    throw err;
  }
}

// 🔹 Download Template
export async function downloadExceptionTemplate() {
  try {
    const res = await api.get(`${IMPORT_BASE}/template`, {
      responseType: "blob",
    });
    return res.data;
  } catch (err) {
    console.error("downloadExceptionTemplate error:", err);
    throw err;
  }
}