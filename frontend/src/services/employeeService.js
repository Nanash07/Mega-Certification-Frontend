import api from "./api";

const BASE_URL = "/employees";

// ================== EMPLOYEE ==================

// 🔹 Ambil data pegawai dengan paging + filter
export async function fetchEmployees(params) {
  try {
    const { data } = await api.get(`${BASE_URL}/paged`, { params });
    return data || { content: [], totalPages: 0, totalElements: 0 };
  } catch (err) {
    console.error("❌ fetchEmployees error:", err);
    return { content: [], totalPages: 0, totalElements: 0 };
  }
}

// 🔹 Delete pegawai (soft delete)
export async function deleteEmployee(id) {
  try {
    await api.delete(`${BASE_URL}/${id}`);
    return true;
  } catch (err) {
    console.error("❌ deleteEmployee error:", err);
    throw err;
  }
}

// 🔹 Create pegawai
export async function createEmployee(payload) {
  try {
    const { data } = await api.post(BASE_URL, payload);
    return data;
  } catch (err) {
    console.error("❌ createEmployee error:", err);
    throw err;
  }
}

// 🔹 Update pegawai
export async function updateEmployee(id, payload) {
  try {
    const { data } = await api.put(`${BASE_URL}/${id}`, payload);
    return data;
  } catch (err) {
    console.error("❌ updateEmployee error:", err);
    throw err;
  }
}

// 🔹 Download template Excel
export async function downloadEmployeeTemplate() {
  try {
    const res = await api.get(`${BASE_URL}/template`, {
      responseType: "blob", // ✅ biar hasilnya file binary
    });
    return res.data;
  } catch (err) {
    console.error("❌ downloadEmployeeTemplate error:", err);
    throw err;
  }
}

// 🔹 Import pegawai via Excel
export async function importEmployeesExcel(formData) {
  try {
    const { data } = await api.post(`${BASE_URL}/import`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return data;
  } catch (err) {
    console.error("❌ importEmployeesExcel error:", err);
    throw err;
  }
}

// ================== MASTER DATA ==================

export async function fetchRegionals() {
  try {
    const { data } = await api.get("/regionals/all");
    return Array.isArray(data) ? data : [];
  } catch (err) {
    console.error("❌ fetchRegionals error:", err);
    return [];
  }
}

export async function fetchDivisions() {
  try {
    const { data } = await api.get("/divisions/all");
    return Array.isArray(data) ? data : [];
  } catch (err) {
    console.error("❌ fetchDivisions error:", err);
    return [];
  }
}

export async function fetchUnits() {
  try {
    const { data } = await api.get("/units/all");
    return Array.isArray(data) ? data : [];
  } catch (err) {
    console.error("❌ fetchUnits error:", err);
    return [];
  }
}

export async function fetchJobPositions() {
  try {
    const { data } = await api.get("/job-positions/all");
    return Array.isArray(data) ? data : [];
  } catch (err) {
    console.error("❌ fetchJobPositions error:", err);
    return [];
  }
}