import api from "./api";

function serialize(body) {
  return {
    employeeId: Number(body.employeeId ?? 0),
    certificationRuleId: Number(body.certificationRuleId ?? 0),
    reason: (body.reason || "").trim(),
  };
}

export async function fetchExceptions() {
  const { data } = await api.get("/employee-certification-exceptions");
  return Array.isArray(data) ? data : [];
}

export async function createException(body) {
  const payload = serialize(body);
  const { data } = await api.post("/employee-certification-exceptions", payload);
  return data;
}

export async function updateException(id, body) {
  const payload = serialize(body);
  const { data } = await api.put(`/employee-certification-exceptions/${id}`, payload);
  return data;
}

export async function deleteException(id) {
  await api.delete(`/employee-certification-exceptions/${id}`);
}