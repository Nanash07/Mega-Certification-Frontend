import { useEffect, useState } from "react";
import Select from "react-select";
import toast from "react-hot-toast";
import { updateException } from "../../services/employeeExceptionService";
import { fetchEmployees } from "../../services/employeeService";
import { fetchCertificationRules } from "../../services/certificationRuleService";

export default function EditExceptionModal({ open, onClose, onSaved, initial }) {
  const [employees, setEmployees] = useState([]);
  const [rules, setRules] = useState([]);
  const [form, setForm] = useState({ employeeId: null, certificationRuleId: null, notes: "" });

  useEffect(() => {
    if (open && initial) {
      fetchEmployees().then(setEmployees).catch(() => toast.error("❌ Gagal load pegawai"));
      fetchCertificationRules().then(setRules).catch(() => toast.error("❌ Gagal load aturan"));

      setForm({
        employeeId: initial.employeeId,
        certificationRuleId: initial.certificationRuleId,
        notes: initial.notes || "",
      });
    }
  }, [open, initial]);

  async function onSubmit() {
    if (!form.notes.trim()) {
      toast.error("⚠️ Catatan tidak boleh kosong");
      return;
    }
    try {
      await updateException(initial.id, { notes: form.notes });
      toast.success("✅ Exception diupdate");
      onSaved();
      onClose();
    } catch (err) {
      toast.error(err.response?.data?.message || "❌ Gagal mengupdate exception");
    }
  }

  const employeeOptions = employees.map((e) => ({
    value: e.id,
    label: `${e.nip} - ${e.name} (${e.jobPositionTitle})`,
  }));

  const ruleOptions = rules.map((r) => ({
    value: r.id,
    label: `${r.certificationCode} - ${r.certificationLevelName || "-"} - ${r.subFieldCode || "-"}`,
  }));

  return (
    <dialog className={`modal ${open ? "modal-open" : ""}`}>
      <div className="modal-box max-w-2xl">
        <h3 className="font-bold text-lg mb-4">Edit Exception</h3>

        {/* Employee (disabled saat edit) */}
        <div className="mb-3">
          <label className="label">Pegawai</label>
          <Select
            isDisabled
            options={employeeOptions}
            value={employeeOptions.find((opt) => opt.value === form.employeeId) || null}
          />
        </div>

        {/* Certification Rule (disabled saat edit) */}
        <div className="mb-3">
          <label className="label">Sertifikasi</label>
          <Select
            isDisabled
            options={ruleOptions}
            value={ruleOptions.find((opt) => opt.value === form.certificationRuleId) || null}
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
          />
        </div>

        <div className="modal-action">
          <button className="btn" onClick={onClose}>Batal</button>
          <button className="btn btn-primary" onClick={onSubmit}>Update</button>
        </div>
      </div>
      <form method="dialog" className="modal-backdrop">
        <button onClick={onClose}>close</button>
      </form>
    </dialog>
  );
}