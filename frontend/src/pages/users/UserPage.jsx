// src/pages/users/UserPage.jsx
import { useEffect, useMemo, useState } from "react";
import toast from "react-hot-toast";
import { fetchUsers, fetchUserDetail, deleteUser } from "../../services/userService";
import { fetchRoles } from "../../services/roleService";
import CreateUserModal from "../../components/users/CreateUserModal";
import EditUserModal from "../../components/users/EditUserModal";

export default function UserPage() {
  // filters
  const [roles, setRoles] = useState([]);
  const [roleId, setRoleId] = useState("");
  const [q, setQ] = useState("");

  // paging
  const [page, setPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  // data
  const [rows, setRows] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);

  // modals
  const [openCreate, setOpenCreate] = useState(false);
  const [editUser, setEditUser] = useState(undefined);
  const [detail, setDetail] = useState(null);
  const [confirm, setConfirm] = useState({ open: false, id: undefined, name: "" });

  // load roles sekali
  useEffect(() => {
    fetchRoles()
      .then(setRoles)
      .catch(() => toast.error("Gagal memuat role", { id: "roles-load" }));
  }, []);

  // param API
  const apiParams = useMemo(
    () => ({
      q: q?.trim() || undefined,
      roleId: roleId ? Number(roleId) : undefined,
      page: page - 1,
      size: rowsPerPage,
    }),
    [q, roleId, page, rowsPerPage]
  );

  async function load() {
    setLoading(true);
    try {
      const data = await fetchUsers(apiParams);
      setRows(data.content || data); // fallback kalau BE balikin array
      setTotalPages(Math.max(data.totalPages || 1, 1));
      setTotalElements(data.totalElements ?? (data.content?.length ?? data.length ?? 0));
    } catch (e) {
      toast.error(e?.response?.data?.message ?? "Gagal memuat data", { id: "users-load" });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiParams]);

  function onSaved() {
    setOpenCreate(false);
    setEditUser(undefined);
    setPage(1);
    load();
  }

  async function onDelete(id) {
    try {
      await deleteUser(id);
      toast.success("User dihapus", { id: "user-delete" });
      load();
    } catch (e) {
      toast.error(e?.response?.data?.message ?? "Gagal menghapus user", { id: "user-delete" });
    }
  }

  async function openDetail(id) {
    try {
      const u = await fetchUserDetail(id);
      setDetail(u);
      document.getElementById("detail_modal").showModal();
    } catch {
      toast.error("Gagal memuat detail", { id: "user-detail" });
    }
  }

  const startIdx = totalElements === 0 ? 0 : (page - 1) * rowsPerPage + 1;
  const endIdx = Math.min(page * rowsPerPage, totalElements);
  const handleChangePage = (newPage) => {
    if (newPage < 1 || newPage > totalPages) return;
    setPage(newPage);
  };

  return (
    <div className="">
      {/* Filter bar */}
      <div className="mb-4 flex flex-col gap-3 md:flex-row md:items-end md:gap-6">
        <div>
          <label className="label pb-1 font-semibold">Role</label>
          <select
            className="select select-bordered w-full max-w-xs"
            value={roleId}
            onChange={(e) => {
              setPage(1);
              setRoleId(e.target.value);
            }}
          >
            <option value="">Semua Role</option>
            {roles.map((r) => (
              <option key={r.id} value={r.id}>
                {r.name}
              </option>
            ))}
          </select>
        </div>

        <div className="flex-1 min-w-[16rem]">
          <label className="label pb-1 font-semibold">Search</label>
          <input
            className="input input-bordered w-full"
            value={q}
            onChange={(e) => {
              setPage(1);
              setQ(e.target.value);
            }}
            placeholder="Cari username / email…"
          />
        </div>

        <div>
          <label className="label pb-1 font-semibold">Rows per page</label>
          <select
            className="select select-bordered"
            value={rowsPerPage}
            onChange={(e) => {
              setRowsPerPage(Number(e.target.value));
              setPage(1);
            }}
          >
            {[5, 10, 15, 20, 30, 50].map((n) => (
              <option key={n} value={n}>
                {n}
              </option>
            ))}
          </select>
        </div>

        <div className="md:ml-auto">
          <button
            className="btn btn-primary w-full md:w-auto"
            onClick={() => setOpenCreate(true)}
          >
            + Tambah User
          </button>
        </div>
      </div>

      {/* Table */}
      <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
        <table className="table">
          <thead className="bg-base-200">
            <tr>
              <th>No.</th>
              <th>Username</th>
              <th>Email</th>
              <th>Employee</th>
              <th>Role</th>
              <th>Status</th>
              <th>Aksi</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={7} className="py-10 text-center">
                  <span className="loading loading-dots loading-md" />
                </td>
              </tr>
            ) : rows.length === 0 ? (
              <tr>
                <td colSpan={7} className="text-center text-gray-400 py-10">
                  Tidak ada data
                </td>
              </tr>
            ) : (
              rows.map((u, idx) => (
                <tr key={u.id}>
                  <td>{(page - 1) * rowsPerPage + idx + 1}</td>
                  <td>
                    <button className="link" onClick={() => openDetail(u.id)}>
                      {u.username}
                    </button>
                  </td>
                  <td>{u.email || "-"}</td>
                  <td>
                    {u.employeeId ? (
                      <span className="badge badge-outline">Linked</span>
                    ) : (
                      <span className="badge badge-ghost">No Employee</span>
                    )}
                  </td>
                  <td>{u.roleName || "-"}</td>
                  <td>
                    {u.isActive ? (
                      <span className="badge badge-success">Aktif</span>
                    ) : (
                      <span className="badge">Nonaktif</span>
                    )}
                  </td>
                  <td className="text-right space-x-2">
                    <button
                      className="btn btn-sm btn-outline btn-warning"
                      onClick={() => setEditUser(u)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-sm btn-outline btn-error"
                      onClick={() =>
                        setConfirm({ open: true, id: u.id, name: u.username })
                      }
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-2 mt-4">
        <div>
          <span className="text-sm text-gray-500">
            Menampilkan {startIdx} - {endIdx} dari {totalElements} data
          </span>
        </div>
        <div className="flex gap-1">
          <button
            className="btn btn-sm btn-outline"
            disabled={page === 1}
            onClick={() => handleChangePage(page - 1)}
          >
            {"<"}
          </button>
          {Array.from({ length: totalPages }).map((_, i) => (
            <button
              key={i}
              className={`btn btn-sm ${
                page === i + 1 ? "btn-primary" : "btn-ghost"
              }`}
              onClick={() => handleChangePage(i + 1)}
            >
              {i + 1}
            </button>
          ))}
          <button
            className="btn btn-sm btn-outline"
            disabled={page === totalPages}
            onClick={() => handleChangePage(page + 1)}
          >
            {">"}
          </button>
        </div>
      </div>

      {/* Modals */}
      <CreateUserModal
        open={openCreate}
        onClose={() => setOpenCreate(false)}
        roles={roles}
        onSaved={onSaved}
      />
      <EditUserModal
        open={!!editUser}
        onClose={() => setEditUser(undefined)}
        roles={roles}
        initial={editUser}
        onSaved={onSaved}
      />

      {/* Confirm delete */}
      {confirm.open && (
        <dialog open className="modal">
          <div className="modal-box">
            <h3 className="font-bold text-lg">Hapus User?</h3>
            <p>Yakin mau hapus user "{confirm.name}"?</p>
            <div className="modal-action">
              <button
                className="btn btn-error"
                onClick={() => {
                  onDelete(confirm.id);
                  setConfirm({ open: false });
                }}
              >
                Hapus
              </button>
              <button className="btn" onClick={() => setConfirm({ open: false })}>
                Batal
              </button>
            </div>
          </div>
        </dialog>
      )}

      {/* Detail modal */}
      <dialog id="detail_modal" className="modal">
        <div className="modal-box">
          <h3 className="font-bold text-lg">Detail User</h3>
          {detail ? (
            <div className="mt-3 space-y-2">
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <div className="text-sm opacity-70">Username</div>
                  <div className="font-medium">{detail.username}</div>
                </div>
                <div>
                  <div className="text-sm opacity-70">Email</div>
                  <div className="font-medium">{detail.email || "-"}</div>
                </div>
                <div>
                  <div className="text-sm opacity-70">Status</div>
                  <div>{detail.isActive ? "Aktif" : "Nonaktif"}</div>
                </div>
                <div>
                  <div className="text-sm opacity-70">Role</div>
                  <div>{detail.roleName || "-"}</div>
                </div>
              </div>
              <div className="divider my-2"></div>
              <div>
                <div className="text-sm opacity-70 mb-1">Employee</div>
                {detail.employeeId ? (
                  <span className="badge badge-outline">Linked</span>
                ) : (
                  <span className="badge badge-ghost">Tidak terhubung</span>
                )}
              </div>
            </div>
          ) : (
            <span className="loading loading-dots loading-md"></span>
          )}
          <div className="modal-action">
            <form method="dialog">
              <button className="btn">Tutup</button>
            </form>
          </div>
        </div>
        <form method="dialog" className="modal-backdrop">
          <button>close</button>
        </form>
      </dialog>
    </div>
  );
}