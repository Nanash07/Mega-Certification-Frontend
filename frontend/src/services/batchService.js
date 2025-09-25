import api from "./api";

const BASE = "/batches";

// ================== BATCH CRUD ==================

// 🔹 Paging + Filter + Search (tabel list)
export async function fetchBatches(params) {
  try {
    const query = { ...params };

    // 👉 Convert sortField & sortDirection ke format Spring: sort=field,direction
    if (params?.sortField) {
      query.sort = `${params.sortField},${params.sortDirection || "asc"}`;
      delete query.sortField;
      delete query.sortDirection;
    }

    const { data } = await api.get(`${BASE}/paged`, { params: query });
    return data || { content: [], totalPages: 0, totalElements: 0 };
  } catch (err) {
    console.error("❌ fetchBatches error:", err);
    return { content: [], totalPages: 0, totalElements: 0 };
  }
}

// 🔹 Create batch
export async function createBatch(payload) {
  try {
    const { data } = await api.post(BASE, payload);
    return data;
  } catch (err) {
    console.error("❌ createBatch error:", err);
    throw err;
  }
}

// 🔹 Update batch
export async function updateBatch(id, payload) {
  try {
    const { data } = await api.put(`${BASE}/${id}`, payload);
    return data;
  } catch (err) {
    console.error("❌ updateBatch error:", err);
    throw err;
  }
}

// 🔹 Delete batch (soft delete)
export async function deleteBatch(id) {
  try {
    await api.delete(`${BASE}/${id}`);
    return true;
  } catch (err) {
    console.error("❌ deleteBatch error:", err);
    throw err;
  }
}

// 🔹 Search batch (async select di dropdown)
export async function searchBatches({ search, page = 0, size = 20 }) {
  try {
    const { data } = await api.get(`${BASE}/paged`, {
      params: { search, page, size },
    });
    return data || { content: [], totalPages: 0, totalElements: 0 };
  } catch (err) {
    console.error("❌ searchBatches error:", err);
    return { content: [], totalPages: 0, totalElements: 0 };
  }
}

// 🔹 Get batch detail by ID
export async function fetchBatchById(id) {
  try {
    const { data } = await api.get(`${BASE}/${id}`);
    return data || null;
  } catch (err) {
    console.error("❌ fetchBatchById error:", err);
    return null;
  }
}
