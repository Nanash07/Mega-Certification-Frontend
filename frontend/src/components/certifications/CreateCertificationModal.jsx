// src/components/certifications/CreateCertificationModal.jsx
import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { createCertification } from "../../services/certificationService";

const emptyForm = {
  name: "",
  code: "",
};

export default function CreateCertificationModal({ open, onClose, onSaved }) {
  const [form, setForm] = useState(emptyForm);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (open) setForm(emptyForm);
  }, [open]);

  function setField(key, val) {
    setForm((f) => ({ ...f, [key]: val }));
  }

  async function onSubmit(e) {
    e.preventDefault();
    setSubmitting(true);
    try {
      await createCertification(form);
      toast.success("Jenis sertifikasi dibuat");
      onSaved?.();
      onClose?.();
    } catch (err) {
      toast.error(err?.response?.data?.message || "Gagal membuat data");
    } finally {
      setSubmitting(false);
    }
  }

  if (!open) return null;

  return (
    <dialog open className="modal">
      <div className="modal-box">
        <h3 className="font-bold text-lg">Tambah Jenis Sertifikasi</h3>

        <form className="mt-4 space-y-3" onSubmit={onSubmit}>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label pb-1">Nama</label>
              <input
                className="input input-bordered w-full"
                value={form.name}
                onChange={(e) => setField("name", e.target.value)}
                placeholder="Sertifikasi Manajemen Risiko"
                required
              />
            </div>
            <div>
              <label className="label pb-1">Kode</label>
              <input
                className="input input-bordered w-full uppercase"
                value={form.code}
                onChange={(e) => setField("code", e.target.value.toUpperCase())}
                placeholder="SMR"
                required
                maxLength={20}
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