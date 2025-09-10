import { useEffect, useMemo, useState } from "react";
import { fetchRolesPage, deleteRole } from "../../services/roleService";
import RoleFormModal from "../../components/UserFormModal";
import ConfirmDialog from "../../components/ConfirmDialog";

export default function RoleManagementPage() {
  const [q, setQ] = useState("");
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [rows, setRows] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  const [openForm, setOpenForm] = useState(false);
  const [edit, setEdit] = useState(undefined);
  const [confirm, setConfirm] = useState({ open: false, id: undefined, name: "" });

  const params = useMemo(() => ({ q: q || undefined, page, size }), [q, page, size]);

  async function load() {
    setLoading(true);
    try {
      const data = await fetchRolesPage(params);
      setRows(data.content || []);
      setTotalPages(Math.max(data.totalPages || 1, 1));
    } catch (e) {
      toast.error(e?.response?.data?.message ?? "Gagal memuat data");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, [params]);

  function onSaved() {
    setOpenForm(false);
    setEdit(undefined);
    setPage(0);
    load();
  }

  async function onDelete(id) {
    try {
      await deleteRole(id);
      toast.success("Role dihapus");
      load();
    } catch (e) {
      toast.error(e?.response?.data?.message ?? "Gagal menghapus role");
    }
  }

  return (
    <div className="p-4 space-y-4"> 

      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Manajemen Role</h1>
        <button className="btn btn-primary" onClick={() => setOpenForm(true)}>+ Tambah Role</button>
      </div>

      {/* Search & Rows per page */}
      <div className="flex flex-wrap gap-3 items-end">
        <label className="form-control w-72">
          <div className="label"><span className="label-text">Search (nama role)</span></div>
          <input
            className="input input-bordered"
            placeholder="Cari role..."
            value={q}
            onChange={(e) => { setPage(0); setQ(e.target.value); }}
          />
        </label>

        <label className="form-control w-40">
          <div className="label"><span className="label-text">Rows per page</span></div>
          <select
            className="select select-bordered"
            value={size}
            onChange={(e) => { setPage(0); setSize(Number(e.target.value)); }}
          >
            {[10, 20, 50].map(n => <option key={n} value={n}>{n}</option>)}
          </select>
        </label>
      </div>

      {/* Table */}
      <div className="overflow-x-auto">
        <table className="table">
          <thead>
            <tr>
              <th>#</th>
              <th>Nama Role</th>
              <th className="text-right">Aksi</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={3}><span className="loading loading-dots loading-md"></span></td></tr>
            ) : rows.length === 0 ? (
              <tr><td colSpan={3} className="text-center text-base-500">Tidak ada data</td></tr>
            ) : rows.map((r, idx) => (
              <tr key={r.id}>
                <td>{page * size + idx + 1}</td>
                <td className="font-medium">{r.name}</td>
                <td className="text-right space-x-2">
                  <button className="btn btn-sm btn-outline" onClick={() => { setEdit(r); setOpenForm(true); }}>
                    Edit
                  </button>
                  <button
                    className="btn btn-sm btn-outline btn-error"
                    onClick={() => setConfirm({ open: true, id: r.id, name: r.name })}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <div className="flex items-center justify-end gap-2">
        <button className="btn btn-sm" disabled={page === 0} onClick={() => setPage(p => p - 1)}>«</button>
        <span className="px-2">Page {page + 1} / {totalPages}</span>
        <button className="btn btn-sm" disabled={page + 1 >= totalPages} onClick={() => setPage(p => p + 1)}>»</button>
      </div>

      <RoleFormModal
        open={openForm}
        onClose={() => { setOpenForm(false); setEdit(undefined); }}
        initial={edit}
        onSaved={onSaved}
      />

      <ConfirmDialog
        open={confirm.open}
        title="Hapus Role?"
        message={`Yakin mau hapus role "${confirm.name}"?`}
        confirmText="Hapus"
        onConfirm={() => confirm.id && onDelete(confirm.id)}
        onClose={() => setConfirm({ open: false })}
      />
    </div>
  );
}
