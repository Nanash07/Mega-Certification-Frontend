import api from "./api";

const BASE_URL = "/job-certification-mappings/import";

export async function dryRunJobCertImport(file) {
  const formData = new FormData();
  formData.append("file", file);
  const { data } = await api.post(`${BASE_URL}/dry-run`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return data;
}

export async function confirmJobCertImport(file) {
  const formData = new FormData();
  formData.append("file", file);
  const { data } = await api.post(`${BASE_URL}/confirm`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return data;
}

export async function downloadJobCertTemplate() {
  const res = await api.get(`${BASE_URL}/template`, { responseType: "blob" });
  const blob = new Blob([res.data], {
    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  });
  const link = document.createElement("a");
  link.href = window.URL.createObjectURL(blob);
  link.download = "job_certification_mapping_template.xlsx";
  link.click();
}
