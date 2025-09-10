import api from "./api";

const BASE_URL = "/employee-eligibility/manual";

// 🔹 Ambil data manual (paging + filter)
export async function fetchManualEligibilities(params) {
  try {
    const { data } = await api.get(`${BASE_URL}/paged`, { params });
    return data || { content: [], totalPages: 0, totalElements: 0 };
  } catch (err) {
    console.error("❌ fetchManualEligibilities error:", err);
    return { content: [], totalPages: 0, totalElements: 0 };
  }
}

// 🔹 Tambah eligibility manual
export async function createManualEligibility(employeeId, certificationRuleId) {
  try {
    const { data } = await api.post(
      `${BASE_URL}?employeeId=${employeeId}`,
      { id: certificationRuleId }
    );
    return data;
  } catch (err) {
    console.error("❌ createManualEligibility error:", err);
    throw err;
  }
}

// 🔹 Toggle aktif/nonaktif
export async function toggleManualEligibility(id) {
  try {
    const { data } = await api.put(`${BASE_URL}/${id}/toggle`);
    return data;
  } catch (err) {
    console.error("❌ toggleManualEligibility error:", err);
    throw err;
  }
}

// 🔹 Soft delete
export async function deleteManualEligibility(id) {
  try {
    await api.delete(`${BASE_URL}/${id}`);
    return true;
  } catch (err) {
    console.error("❌ deleteManualEligibility error:", err);
    throw err;
  }
}