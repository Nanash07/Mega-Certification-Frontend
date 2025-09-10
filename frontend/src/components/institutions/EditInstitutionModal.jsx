import { useState, useEffect } from "react";
import toast from "react-hot-toast";
import { updateInstitution } from "../../services/institutionService";

export default function EditInstitutionModal({ open, onClose, onSaved, initial }) {
  const [form, setForm] = useState({ name: "", type: "Internal", address: "", contactPerson: "" });

  useEffect(() => {
    if (open && initial) {
      setForm({
        name: initial.name,
        type: initial.type,
        address: initial.address,
        contactPerson: initial.contactPerson,
      });
    }
  }, [open, initial]);

  async function onSubmit() {
    if (!form.name || !form.type) {
      toast.error("Nama & Type wajib diisi");
      return;
    }
    try {
      await updateInstitution(initial.id, form);
      toast.success("Institution diupdate");
      onSaved();
    } catch {
      toast.error("Gagal mengupdate institution");
    }
  }

  return (
    <dialog className={`modal ${open ? "modal-open" : ""}`}>
      <div className="modal-box max-w-lg">
        <h3 className="font-bold text-lg mb-4">Edit Institution</h3>

        <div className="form-control mb-3">
          <label className="label">Nama</label>
          <input
            className="input input-bordered"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
          />
        </div>

        <div className="form-control mb-3">
          <label className="label">Type</label>
          <select
            className="select select-bordered"
            value={form.type}
            onChange={(e) => setForm({ ...form, type: e.target.value })}
          >
            <option value="Internal">Internal</option>
            <option value="External">External</option>
          </select>
        </div>

        <div className="form-control mb-3">
          <label className="label">Alamat</label>
          <input
            className="input input-bordered"
            value={form.address}
            onChange={(e) => setForm({ ...form, address: e.target.value })}
          />
        </div>

        <div className="form-control mb-3">
          <label className="label">Contact Person</label>
          <input
            className="input input-bordered"
            value={form.contactPerson}
            onChange={(e) => setForm({ ...form, contactPerson: e.target.value })}
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