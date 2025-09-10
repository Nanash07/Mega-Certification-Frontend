// src/components/certification-levels/CreateCertificationLevelModal.jsx
import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { createCertificationLevel } from "../../services/certificationLevelService";

const emptyForm = { level: 4, name: "" };

export default function CreateCertificationLevelModal({ open, onClose, onSaved }) {
  const [form, setForm] = useState(emptyForm);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open) return;
    setForm(emptyForm);
  }, [open]);

  function setField(key, val) {
    setForm((f) => ({ ...f, [key]: val }));
  }

  async function onSubmit(e) {
    e.preventDefault();
    setSubmitting(true);
    try {
      await createCertificationLevel(form);
      toast.success("Jenjang sertifikasi dibuat");
      onSaved?.();
      onClose?.();
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.message || "Gagal membuat data");
    } finally {
      setSubmitting(false);
    }
  }

  if (!open) return null;

  return (
    <dialog open className="modal">
      <div className="modal-box">
        <h3 className="font-bold text-lg">Tambah Jenjang Sertifikasi</h3>

        <form className="mt-4 space-y-3" onSubmit={onSubmit}>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label pb-1">Level</label>
              <input
                type="number"
                className="input input-bordered w-full"
                value={form.level}
                onChange={(e) => setField("level", e.target.value)}
                min={1}
                max={10}
                required
              />
            </div>

            <div>
              <label className="label pb-1">Nama Jenjang</label>
              <input
                className="input input-bordered w-full"
                value={form.name}
                onChange={(e) => setField("name", e.target.value)}
                placeholder="Jenjang 4"
                required
              />
            </div>
          </div>

          <div className="modal-action">
            <button
              type="button"
              className="btn"
              onClick={onClose}
              disabled={submitting}
            >
              Batal
            </button>
            <button
              className={`btn btn-primary ${submitting ? "loading" : ""}`}
              disabled={submitting}
            >
              Simpan
            </button>
          </div>
        </form>
      </div>
      <form method="dialog" className="modal-backdrop">
        <button onClick={onClose}>close</button>
      </form>
    </dialog>
  );
}