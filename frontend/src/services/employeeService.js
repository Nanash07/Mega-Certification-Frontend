import api from "./api";
import qs from "qs";

// ================== EMPLOYEE ==================
export async function fetchEmployees(params) {
  const res = await api.get("/employees", {
    params,
    paramsSerializer: (p) =>
      qs.stringify(p, { arrayFormat: "repeat" }), 
      // ✅ array jadi ?regionalIds=1&regionalIds=2 (Spring Boot ngerti)
  });
  return res.data;
}

export async function deleteEmployee(id) {
  await api.delete(`/employees/${id}`);
}

export async function createEmployee(payload) {
  const res = await api.post("/employees", payload);
  return res.data;
}

export async function updateEmployee(id, payload) {
  const res = await api.put(`/employees/${id}`, payload);
  return res.data;
}

export async function downloadEmployeeTemplate() {
  const res = await api.get("/employees/template", {
    responseType: "blob", // ✅ biar hasilnya file binary
  });
  return res.data;
}

export async function importEmployeesExcel(formData) {
  const res = await api.post("/employees/import", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return res.data;
}

// ================== MASTER DATA ==================
// Semua master data udah flat → gak pakai filter param

export async function fetchRegionals() {
  const res = await api.get("/regionals");
  return Array.isArray(res.data) ? res.data : [];
}

export async function fetchDivisions() {
  const res = await api.get("/divisions");
  return Array.isArray(res.data) ? res.data : [];
}

export async function fetchUnits() {
  const res = await api.get("/units");
  return Array.isArray(res.data) ? res.data : [];
}

export async function fetchJobPositions() {
  const res = await api.get("/job-positions");
  return Array.isArray(res.data) ? res.data : [];
}