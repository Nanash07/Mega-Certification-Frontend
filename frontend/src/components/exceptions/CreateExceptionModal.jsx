import { useEffect, useState } from "react";
import Select from "react-select";
import toast from "react-hot-toast";
import { createException } from "../../services/employeeCertificationExceptionService";
import { fetchEmployees } from "../../services/employeeService";
import { fetchCertificationRules } from "../../services/certificationRuleService";

export default function CreateExceptionModal({ open, onClose, onSaved }) {
  const [employees, setEmployees] = useState([]);
  const [rules, setRules] = useState([]);
  const [form, setForm] = useState({ employeeId: null, certificationRuleId: null, reason: "" });

  useEffect(() => {
    if (open) {
      fetchEmployees().then(setEmployees).catch(() => toast.error("Gagal load pegawai"));
      fetchCertificationRules().then(setRules).catch(() => toast.error("Gagal load aturan"));
      setForm({ employeeId: null, certificationRuleId: null, reason: "" });
    }
  }, [open]);

  async function onSubmit() {
    if (!form.employeeId || !form.certificationRuleId) {
      toast.error("Pilih pegawai & sertifikasi dulu");
      return;
    }
    try {
      await createException(form);
      toast.success("Data ditambahkan");
      onSaved();
    } catch (err) {
      toast.error(err.response?.data?.message || "Gagal menambah data");
    }
  }

  const employeeOptions = employees.map((e) => ({
    value: e.id,
    label: `${e.nip} - ${e.name} (${e.jobPositionTitle})`,
  }));

  const ruleOptions = rules.map((r) => ({
    value: r.id,
    label: `${r.certificationCode} - ${r.levelName || "-"} - ${r.subFieldCode || "-"}`,
  }));

  return (
    <dialog className={`modal ${open ? "modal-open" : ""}`}>
      <div className="modal-box max-w-2xl">
        <h3 className="font-bold text-lg mb-4">Tambah Exception</h3>

        <div className="mb-3">
          <label className="label">Pegawai</label>
          <Select
            options={employeeOptions}
            value={employeeOptions.find((opt) => opt.value === form.employeeId) || null}
            onChange={(opt) => setForm({ ...form, employeeId: opt?.value })}
            placeholder="Pilih pegawai"
            menuPortalTarget={document.body}
            menuPosition="fixed"
            styles={{ menuPortal: (base) => ({ ...base, zIndex: 9999 }) }}
          />
        </div>

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

        <div className="mb-3">
          <label className="label">Reason</label>
          <input
            type="text"
            className="input input-bordered w-full"
            value={form.reason}
            onChange={(e) => setForm({ ...form, reason: e.target.value })}
          />
        </div>

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