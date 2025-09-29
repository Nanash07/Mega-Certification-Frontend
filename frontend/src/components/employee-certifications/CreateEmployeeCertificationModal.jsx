import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import Select from "react-select";
import AsyncSelect from "react-select/async";
import { createCertification } from "../../services/employeeCertificationService";
import { searchEmployees } from "../../services/employeeService";
import { fetchCertificationRules } from "../../services/certificationRuleService";
import { fetchInstitutions } from "../../services/institutionService";

export default function CreateCertificationModal({ open, onClose, onSaved }) {
  const [rules, setRules] = useState([]);
  const [institutions, setInstitutions] = useState([]);

  const [form, setForm] = useState(defaultForm());

  function defaultForm() {
    return {
      employeeId: null,
      employeeLabel: "",
      certificationRuleId: null,
      institutionId: null,
      certNumber: "",
      certDate: "",
      processType: "SERTIFIKASI",
    };
  }

  // Reset form tiap kali modal dibuka
  useEffect(() => {
    if (open) {
      setForm(defaultForm());
      Promise.all([fetchCertificationRules(), fetchInstitutions()])
        .then(([rules, insts]) => {
          setRules(
            rules.map((r) => ({
              value: r.id,
              label: `${r.certificationCode} - ${r.certificationLevelName || ""} - ${r.subFieldCode || ""}`,
            }))
          );
          setInstitutions(insts.map((i) => ({ value: i.id, label: i.name })));
        })
        .catch(() => toast.error("Gagal memuat data dropdown"));
    }
  }, [open]);

  // Async load employees
  const loadEmployees = async (inputValue) => {
    try {
      const res = await searchEmployees({ search: inputValue, page: 0, size: 20 });
      return res.content.map((e) => ({
        value: e.id,
        label: `${e.nip} - ${e.name}`,
      }));
    } catch {
      return [];
    }
  };

  async function handleSave() {
    try {
      // Bersihin payload sebelum kirim
      const { employeeLabel, ...payload } = form;
      Object.keys(payload).forEach((k) => {
        if (payload[k] === null || payload[k] === "") delete payload[k];
      });

      await createCertification(payload);
      toast.success("Sertifikasi pegawai berhasil ditambahkan");
      onSaved?.();
      onClose();
    } catch (err) {
      toast.error("Gagal menambahkan sertifikasi");
      console.error(err);
    }
  }

  if (!open) return null;

  return (
    <dialog className="modal" open={open}>
      <div className="modal-box max-w-3xl">
        <h3 className="font-bold text-lg mb-4">Tambah Sertifikasi Pegawai</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
          {/* Pegawai */}
          <div>
            <label className="block mb-1">Pegawai</label>
            <AsyncSelect
              cacheOptions
              defaultOptions
              loadOptions={loadEmployees}
              value={
                form.employeeId
                  ? { value: form.employeeId, label: form.employeeLabel }
                  : null
              }
              onChange={(opt) =>
                setForm({
                  ...form,
                  employeeId: opt?.value || null,
                  employeeLabel: opt?.label || "",
                })
              }
              placeholder="Cari pegawai..."
              isClearable
            />
          </div>

          {/* Aturan Sertifikasi */}
          <div>
            <label className="block mb-1">Aturan Sertifikasi</label>
            <Select
              options={rules}
              value={rules.find((r) => r.value === form.certificationRuleId) || null}
              onChange={(opt) =>
                setForm({ ...form, certificationRuleId: opt?.value || null })
              }
              placeholder="Pilih Aturan Sertifikasi"
              isClearable
            />
          </div>

          {/* Lembaga */}
          <div>
            <label className="block mb-1">Lembaga</label>
            <Select
              options={institutions}
              value={institutions.find((i) => i.value === form.institutionId) || null}
              onChange={(opt) =>
                setForm({ ...form, institutionId: opt?.value || null })
              }
              placeholder="Pilih Lembaga"
              isClearable
            />
          </div>

          {/* Nomor Sertifikat */}
          <div>
            <label className="block mb-1">Nomor Sertifikat</label>
            <input
              type="text"
              value={form.certNumber}
              onChange={(e) => setForm({ ...form, certNumber: e.target.value })}
              className="input input-bordered w-full"
              placeholder="Contoh: CERT-2025-001"
            />
          </div>

          {/* Tanggal Sertifikat */}
          <div>
            <label className="block mb-1">Tanggal Sertifikat</label>
            <input
              type="date"
              value={form.certDate}
              onChange={(e) => setForm({ ...form, certDate: e.target.value })}
              className="input input-bordered w-full"
            />
          </div>

          {/* Jenis Proses */}
          <div>
            <label className="block mb-1">Jenis Proses</label>
            <select
              value={form.processType}
              onChange={(e) => setForm({ ...form, processType: e.target.value })}
              className="select select-bordered w-full"
            >
              <option value="SERTIFIKASI">Sertifikasi</option>
              <option value="REFRESHMENT">Refreshment</option>
              <option value="TRAINING">Training</option>
            </select>
          </div>
        </div>

        <div className="modal-action">
          <button className="btn" onClick={onClose}>
            Batal
          </button>
          <button className="btn btn-primary" onClick={handleSave}>
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
