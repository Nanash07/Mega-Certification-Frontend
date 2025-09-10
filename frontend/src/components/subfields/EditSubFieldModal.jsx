// src/components/subfields/EditSubFieldModal.jsx
import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { updateSubField } from "../../services/subFieldService";
import { fetchCertifications } from "../../services/certificationService";

export default function EditSubFieldModal({ open, initial, onClose, onSaved }) {
  const [form, setForm] = useState({ name: "", code: "", certificationId: "" });
  const [submitting, setSubmitting] = useState(false);
  const [certs, setCerts] = useState([]);

  useEffect(() => {
    if (!open) return;
    (async () => {
      try {
        const list = await fetchCertifications();
        setCerts(list);
      } catch {
        toast.error("Gagal memuat sertifikasi");
      }
    })();
  }, [open]);

  useEffect(() => {
    if (open && initial) {
      setForm({
        name: initial.name ?? "",
        code: (initial.code ?? "").toUpperCase(),
        certificationId: initial.certificationId ?? "",
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
      await updateSubField(initial.id, form);
      toast.success("Sub bidang diupdate");
      onSaved?.();
      onClose?.();
    } catch (err) {
      toast.error(err?.response?.data?.message || "Gagal mengupdate sub bidang");
    } finally {
      setSubmitting(false);
    }
  }

  if (!open) return null;

  return (
    <dialog open className="modal">
      <div className="modal-box">
        <h3 className="font-bold text-lg">Edit Sub Bidang</h3>
        <form className="mt-4 space-y-3" onSubmit={onSubmit}>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label pb-1">Nama</label>
              <input
                className="input input-bordered w-full"
                value={form.name}
                onChange={(e) => setField("name", e.target.value)}
                required
              />
            </div>
            <div>
              <label className="label pb-1">Kode</label>
              <input
                className="input input-bordered w-full uppercase"
                value={form.code}
                onChange={(e) => setField("code", e.target.value.toUpperCase())}
                required
              />
            </div>
          </div>
          <div>
            <label className="label pb-1">Kode Sertifikat</label>
            <select
              className="select select-bordered w-full"
              value={form.certificationId}
              onChange={(e) => setField("certificationId", e.target.value)}
              required
            >
              <option value="">-- Pilih Kode Sertifikat --</option>
              {certs.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.code}
                </option>
              ))}
            </select>
          </div>
          <div className="modal-action">
            <button type="button" className="btn" onClick={onClose} disabled={submitting}>
              Batal
            </button>
            <button className={`btn btn-primary ${submitting ? "loading" : ""}`} disabled={submitting}>
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