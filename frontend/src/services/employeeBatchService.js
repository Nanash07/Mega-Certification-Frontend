import api from "./api";

const BASE = "/employee-batches";

// 🔹 Get peserta by batch
export async function fetchEmployeeBatches(batchId) {
  try {
    const { data } = await api.get(`${BASE}/batch/${batchId}`);
    return Array.isArray(data) ? data : [];
  } catch (err) {
    console.error("❌ fetchEmployeeBatches error:", err);
    return [];
  }
}

// 🔹 Tambah peserta
export async function addEmployeeToBatch(batchId, employeeId) {
  try {
    const { data } = await api.post(`${BASE}/batch/${batchId}/employee/${employeeId}`);
    return data;
  } catch (err) {
    console.error("❌ addEmployeeToBatch error:", err);
    throw err;
  }
}

// 🔹 Update status peserta
export async function updateEmployeeBatchStatus(id, status, score, notes) {
  try {
    const { data } = await api.put(`${BASE}/${id}/status`, null, {
      params: { status, score, notes },
    });
    return data;
  } catch (err) {
    console.error("❌ updateEmployeeBatchStatus error:", err);
    throw err;
  }
}

// 🔹 Delete peserta
export async function deleteEmployeeFromBatch(id) {
  try {
    await api.delete(`${BASE}/${id}`);
    return true;
  } catch (err) {
    console.error("❌ deleteEmployeeFromBatch error:", err);
    throw err;
  }
}

// 🔹 Get eligible employees untuk batch
export async function fetchEligibleEmployees(batchId) {
  try {
    const { data } = await api.get(`${BASE}/batch/${batchId}/eligible`);
    return Array.isArray(data) ? data : [];
  } catch (err) {
    console.error("❌ fetchEligibleEmployees error:", err);
    return [];
  }
}
