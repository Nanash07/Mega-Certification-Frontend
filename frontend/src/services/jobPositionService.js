import api from "./api";

const BASE_URL = "/job-positions";

// ✅ Ambil job positions (paged, buat tabel)
export async function fetchJobPositions(params) {
  try {
    const { data } = await api.get(BASE_URL, { params });
    return data; // { content, totalPages, totalElements }
  } catch (err) {
    console.error("❌ fetchJobPositions error:", err);
    return { content: [], totalPages: 0, totalElements: 0 };
  }
}

// ✅ Ambil semua job positions (array, buat dropdown filter/modal)
export async function fetchAllJobPositions() {
  try {
    const { data } = await api.get(`${BASE_URL}/all`);
    return Array.isArray(data) ? data : [];
  } catch (err) {
    console.error("❌ fetchAllJobPositions error:", err);
    return [];
  }
}

// ✅ Ambil detail job position by id
export async function fetchJobPositionById(id) {
  try {
    const { data } = await api.get(`${BASE_URL}/${id}`);
    return data;
  } catch (err) {
    console.error("❌ fetchJobPositionById error:", err);
    return null;
  }
}

// ✅ Create job position baru
export async function createJobPosition(payload) {
  try {
    const { data } = await api.post(BASE_URL, payload); // payload harus { name: "Manager" }
    return data;
  } catch (err) {
    console.error("❌ createJobPosition error:", err);
    throw err;
  }
}

// ✅ Toggle aktif/nonaktif job position
export async function toggleJobPosition(id) {
  try {
    const { data } = await api.put(`${BASE_URL}/${id}/toggle`);
    return data;
  } catch (err) {
    console.error("❌ toggleJobPosition error:", err);
    throw err;
  }
}
