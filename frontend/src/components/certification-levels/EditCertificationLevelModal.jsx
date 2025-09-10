// src/components/certification-levels/EditCertificationLevelModal.jsx
import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { updateCertificationLevel } from "../../services/certificationLevelService";

export default function EditCertificationLevelModal({ open, initial, onClose, onSaved }) {
  const [form, setForm] = useState({ level: 0, name: "" });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (open && initial) {
      setForm({
        level: Number(initial.level ?? 0),
        name: initial.name ?? "",
      });
    }
  }, [open, initial]);

  function setField(key, val) {
    setForm((f) => ({ ...f, [key]: val }));
  }

  async function onSubmit(e) {
    e.preventDefault();
    if (!initial?.id) {
      toast.error("ID tidak ditemukan");
      return;
    }
    setSubmitting(true);
    try {
      await updateCertificationLevel(initial.id, form);
      toast.success("Jenjang sertifikasi diupdate");
      onSaved?.();
      onClose?.();
    } catch (err) {
      toast.error(err?.response?.data?.message || "Gagal mengupdate data");
    } finally {
      setSubmitting(false);
    }
  }

  if (!open) return null;

  return (
    <dialog open className="modal">
      <div className="modal-box">
        <h3 className="font-bold text-lg">Edit Jenjang Sertifikasi</h3>

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