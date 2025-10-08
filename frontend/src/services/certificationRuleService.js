import api from "./api";

const BASE_URL = "/certification-rules";

// ================= CRUD Certification Rules =================

// 🔹 Ambil semua Certification Rule (non-paging)
export async function fetchCertificationRules() {
    try {
        const { data } = await api.get(BASE_URL);
        return Array.isArray(data) ? data : [];
    } catch (err) {
        console.error("❌ fetchCertificationRules error:", err);
        return [];
    }
}

// 🔹 Ambil Certification Rule dengan paging + filter + search
export async function fetchCertificationRulesPaged(params) {
    try {
        const { data } = await api.get(`${BASE_URL}/paged`, { params });
        return data || { content: [], totalPages: 0, totalElements: 0 };
    } catch (err) {
        console.error("❌ fetchCertificationRulesPaged error:", err);
        return { content: [], totalPages: 0, totalElements: 0 };
    }
}

// 🔹 Ambil detail rule by ID
export async function fetchCertificationRuleById(id) {
    try {
        const { data } = await api.get(`${BASE_URL}/${id}`);
        return data || null;
    } catch (err) {
        console.error("❌ fetchCertificationRuleById error:", err);
        return null;
    }
}

// 🔹 Create rule baru
export async function createCertificationRule(payload) {
    try {
        const { data } = await api.post(BASE_URL, payload);
        return data;
    } catch (err) {
        console.error("❌ createCertificationRule error:", err);
        throw err;
    }
}

// 🔹 Update rule
export async function updateCertificationRule(id, payload) {
    try {
        const { data } = await api.put(`${BASE_URL}/${id}`, payload);
        return data;
    } catch (err) {
        console.error("❌ updateCertificationRule error:", err);
        throw err;
    }
}

// 🔹 Soft delete rule
export async function deleteCertificationRule(id) {
    try {
        await api.delete(`${BASE_URL}/${id}`);
        return true;
    } catch (err) {
        console.error("❌ deleteCertificationRule error:", err);
        throw err;
    }
}

// 🔹 Toggle aktif/nonaktif
export async function toggleCertificationRule(id) {
    try {
        const { data } = await api.put(`${BASE_URL}/${id}/toggle`);
        return data;
    } catch (err) {
        console.error("❌ toggleCertificationRule error:", err);
        throw err;
    }
}

// ================= DROPDOWNS =================

// 🔹 Certifications
export async function fetchCertifications() {
    try {
        const { data } = await api.get("/certifications");
        return Array.isArray(data) ? data : [];
    } catch (err) {
        console.error("❌ fetchCertifications error:", err);
        return [];
    }
}

// 🔹 Certification Levels
export async function fetchCertificationLevels() {
    try {
        const { data } = await api.get("/certification-levels");
        return Array.isArray(data) ? data : [];
    } catch (err) {
        console.error("❌ fetchCertificationLevels error:", err);
        return [];
    }
}

// 🔹 Sub Fields
export async function fetchSubFields() {
    try {
        const { data } = await api.get("/sub-fields");
        return Array.isArray(data) ? data : [];
    } catch (err) {
        console.error("❌ fetchSubFields error:", err);
        return [];
    }
}
