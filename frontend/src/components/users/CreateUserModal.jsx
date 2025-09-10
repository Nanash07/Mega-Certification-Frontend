import { useState } from "react";
import toast from "react-hot-toast";
import { createUser } from "../../services/userService";

export default function CreateUserModal({ open, onClose, roles, onSaved }) {
  const [form, setForm] = useState({
    username: "",
    email: "",
    password: "",
    roleId: "",
    isActive: true,
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : (name === "roleId" ? Number(value) : value),
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await createUser(form);
      toast.success("User berhasil dibuat");
      onSaved();
    } catch (err) {
      toast.error(err?.response?.data?.message ?? "Gagal membuat user");
    } finally {
      setLoading(false);
    }
  };

  if (!open) return null;

  return (
    <dialog open className="modal">
      <div className="modal-box">
        <h3 className="font-bold text-lg">Tambah User</h3>
        <form onSubmit={handleSubmit} className="space-y-3">
          <input
            className="input input-bordered w-full"
            name="username"
            value={form.username}
            onChange={handleChange}
            placeholder="Username"
            required
          />
          <input
            className="input input-bordered w-full"
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            placeholder="Email"
            required
          />
          <input
            className="input input-bordered w-full"
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            placeholder="Password"
            required
          />
          <select
            className="select select-bordered w-full"
            name="roleId"
            value={form.roleId}
            onChange={handleChange}
            required
          >
            <option value="">Pilih Role</option>
            {roles.map((r) => (
              <option key={r.id} value={r.id}>{r.name}</option>
            ))}
          </select>
          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              name="isActive"
              checked={form.isActive}
              onChange={handleChange}
              className="checkbox"
            />
            Aktif
          </label>

          <div className="modal-action">
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? "Menyimpan..." : "Simpan"}
            </button>
            <button type="button" className="btn" onClick={onClose}>
              Batal
            </button>
          </div>
        </form>
      </div>
    </dialog>
  );
}