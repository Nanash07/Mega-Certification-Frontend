import { useEffect, useState } from "react";
import AsyncSelect from "react-select/async";
import Select from "react-select";
import toast from "react-hot-toast";
import { createException } from "../../services/employeeExceptionService";
import { fetchCertificationRules } from "../../services/certificationRuleService";
import { fetchEmployees } from "../../services/employeeService"; // pake search API (paged)

export default function CreateExceptionModal({ open, onClose, onSaved }) {
  const [rules, setRules] = useState([]);
  const [form, setForm] = useState({
    employeeId: null,
    employeeLabel: "", // simpan label biar tampil di select
    certificationRuleId: null,
    notes: "",
  });

  useEffect(() => {
    if (open) {
      fetchCertificationRules()
        .then(setRules)
        .catch(() => toast.error("Gagal load aturan"));

      setForm({ employeeId: null, employeeLabel: "", certificationRuleId: null, notes: "" });
    }
  }, [open]);

  async function onSubmit() {
    if (!form.employeeId || !form.certificationRuleId) {
      toast.error("Pilih pegawai & sertifikasi dulu");
      return;
    }
    try {
      await createException({
        employeeId: form.employeeId,
        certificationRuleId: form.certificationRuleId,
        notes: form.notes,
      });
      toast.success("Exception ditambahkan");
      onSaved?.(); 
      onClose();
    } catch (err) {
      toast.error(err.response?.data?.message || "Gagal menambah exception");
    }
  }

  // 🔹 Async load employees by search
  async function loadEmployeeOptions(inputValue) {
    if (!inputValue || inputValue.length < 2) return [];
    try {
      const res = await fetchEmployees({ search: inputValue, page: 0, size: 20 });
      return res.content.map((e) => ({
        value: e.id,
        label: `${e.nip} - ${e.name} (${e.jobName || "-"})`,
      }));
    } catch {
      return [];
    }
  }

  const ruleOptions = (rules || []).map((r) => ({
    value: r.id,
    label: `${r.certificationCode} - ${r.certificationLevelName || "-"} - ${r.subFieldCode || "-"}`,
  }));

  return (
    <dialog className={`modal ${open ? "modal-open" : ""}`}>
      <div className="modal-box max-w-2xl">
        <h3 className="font-bold text-lg mb-4">Tambah Exception</h3>

        {/* Employee (Async) */}
        <div className="mb-3">
          <label className="label">Pegawai</label>
          <AsyncSelect
            cacheOptions
            defaultOptions={[]}
            loadOptions={loadEmployeeOptions}
            value={
              form.employeeId
                ? { value: form.employeeId, label: form.employeeLabel }
                : null
            }
            onChange={(opt) =>
              setForm({ ...form, employeeId: opt?.value, employeeLabel: opt?.label })
            }
            placeholder="Cari pegawai..."
            menuPortalTarget={document.body}
            menuPosition="fixed"
            styles={{ menuPortal: (base) => ({ ...base, zIndex: 9999 }) }}
          />
        </div>

        {/* Certification Rule */}
        <div className="mb-3">
          <label className="label">Sertifikasi</label>
          <Select
            options={ruleOptions}
            value={ruleOptions.find((opt) => opt.value === form.certificationRuleId) || null}
            onChange={(opt) => setForm({ ...form, certificationRuleId: opt?.value })}
            placeholder="Pilih aturan sertifikasi"
            menuPortalTarget={document.body}
            menuPosition="fixed"
            styles={{ menuPortal: (base) => ({ ...base, zIndex: 9999 }) }}
          />
        </div>

        {/* Notes */}
        <div className="mb-3">
          <label className="label">Catatan</label>
          <input
            type="text"
            className="input input-bordered w-full"
            value={form.notes}
            onChange={(e) => setForm({ ...form, notes: e.target.value })}
            placeholder="Catatan tambahan (opsional)"
          />
        </div>

        {/* Actions */}
        <div className="modal-action">
          <button className="btn" onClick={onClose}>
            Batal
          </button>
          <button className="btn btn-primary" onClick={onSubmit}>
            Simpan
          </button>
        </div>
      </div>

      <form method="dialog" className="modal-backdrop">
        <button onClick={onClose}>close</button>
      </form>
    </dialog>
  );
}