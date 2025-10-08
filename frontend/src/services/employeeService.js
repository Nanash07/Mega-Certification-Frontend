import api from "./api";

const EMPLOYEE_BASE = "/employees";
const IMPORT_BASE = "/employees/import";

// =============================================================
// 🧩 EMPLOYEE CRUD
// =============================================================

// 🔹 Ambil data pegawai (paging + filter + sorting)
export async function fetchEmployees(params = {}) {
    try {
        const query = { ...params };

        // 👉 Convert sortField & sortDirection ke format Spring: sort=field,direction
        if (params?.sortField) {
            query.sort = `${params.sortField},${params.sortDirection || "asc"}`;
            delete query.sortField;
            delete query.sortDirection;
        }

        const { data } = await api.get(`${EMPLOYEE_BASE}/paged`, { params: query });
        return data || { content: [], totalPages: 0, totalElements: 0 };
    } catch (err) {
        console.error("fetchEmployees error:", err);
        return { content: [], totalPages: 0, totalElements: 0 };
    }
}

// 🔹 Ambil semua pegawai (non-paging) — hati-hati kalau datanya ribuan
export async function fetchEmployeesAll() {
    try {
        const { data } = await api.get(`${EMPLOYEE_BASE}/all`);
        return Array.isArray(data) ? data : [];
    } catch (err) {
        console.error("fetchEmployeesAll error:", err);
        return [];
    }
}

// 🔹 Search pegawai (paged)
export async function searchEmployees({ search = "", page = 0, size = 20 }) {
    try {
        const { data } = await api.get(`${EMPLOYEE_BASE}/paged`, {
            params: { search, page, size },
        });
        return data || { content: [], totalPages: 0, totalElements: 0 };
    } catch (err) {
        console.error("searchEmployees error:", err);
        return { content: [], totalPages: 0, totalElements: 0 };
    }
}

// 🔹 Ambil detail pegawai
export async function getEmployeeDetail(id) {
    try {
        const { data } = await api.get(`${EMPLOYEE_BASE}/${id}`);
        return data;
    } catch (err) {
        console.error("getEmployeeDetail error:", err);
        throw err;
    }
}

// 🔹 Hapus pegawai (soft delete)
export async function deleteEmployee(id) {
    try {
        await api.delete(`${EMPLOYEE_BASE}/${id}`);
        return true;
    } catch (err) {
        console.error("deleteEmployee error:", err);
        throw err;
    }
}

// 🔹 Tambah pegawai baru
export async function createEmployee(payload) {
    try {
        const { data } = await api.post(EMPLOYEE_BASE, payload);
        return data;
    } catch (err) {
        console.error("createEmployee error:", err);
        throw err;
    }
}

// 🔹 Update data pegawai
export async function updateEmployee(id, payload) {
    try {
        const { data } = await api.put(`${EMPLOYEE_BASE}/${id}`, payload);
        return data;
    } catch (err) {
        console.error("updateEmployee error:", err);
        throw err;
    }
}

// =============================================================
// 📦 EMPLOYEE IMPORT (Excel Upload)
// =============================================================

// 🔹 Download template Excel pegawai
export async function downloadEmployeeTemplate() {
    try {
        const res = await api.get(`${IMPORT_BASE}/template`, {
            responseType: "blob",
        });
        return res.data;
    } catch (err) {
        console.error("downloadEmployeeTemplate error:", err);
        throw err;
    }
}

// 🔹 Dry run import pegawai (cek dulu tanpa commit DB)
export async function importEmployeesDryRun(formData) {
    try {
        const { data } = await api.post(`${IMPORT_BASE}/dry-run`, formData, {
            headers: { "Content-Type": "multipart/form-data" },
        });
        return data;
    } catch (err) {
        console.error("importEmployeesDryRun error:", err);
        throw err;
    }
}

// 🔹 Confirm import pegawai (commit ke DB)
export async function importEmployeesConfirm(formData) {
    try {
        const { data } = await api.post(`${IMPORT_BASE}/confirm`, formData, {
            headers: { "Content-Type": "multipart/form-data" },
        });
        return data;
    } catch (err) {
        console.error("importEmployeesConfirm error:", err);
        throw err;
    }
}

// 🔹 Ambil semua logs import (admin)
export async function fetchEmployeeImportLogs() {
    try {
        const { data } = await api.get(`${IMPORT_BASE}/logs`);
        return Array.isArray(data) ? data : [];
    } catch (err) {
        console.error("fetchEmployeeImportLogs error:", err);
        return [];
    }
}

// 🔹 Ambil logs import berdasarkan user ID
export async function fetchEmployeeImportLogsByUser(userId) {
    try {
        const { data } = await api.get(`${IMPORT_BASE}/logs/${userId}`);
        return Array.isArray(data) ? data : [];
    } catch (err) {
        console.error("fetchEmployeeImportLogsByUser error:", err);
        return [];
    }
}

// =============================================================
// 🗂️ MASTER DATA (Dropdown Support)
// =============================================================

// 🔹 Ambil semua regional
export async function fetchRegionals() {
    try {
        const { data } = await api.get("/regionals/all");
        return Array.isArray(data) ? data : [];
    } catch (err) {
        console.error("fetchRegionals error:", err);
        return [];
    }
}

// 🔹 Ambil semua division
export async function fetchDivisions() {
    try {
        const { data } = await api.get("/divisions/all");
        return Array.isArray(data) ? data : [];
    } catch (err) {
        console.error("fetchDivisions error:", err);
        return [];
    }
}

// 🔹 Ambil semua unit
export async function fetchUnits() {
    try {
        const { data } = await api.get("/units/all");
        return Array.isArray(data) ? data : [];
    } catch (err) {
        console.error("fetchUnits error:", err);
        return [];
    }
}

// 🔹 Ambil semua job position
export async function fetchJobPositions() {
    try {
        const { data } = await api.get("/job-positions/all");
        return Array.isArray(data) ? data : [];
    } catch (err) {
        console.error("fetchJobPositions error:", err);
        return [];
    }
}
