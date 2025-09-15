import { useEffect, useMemo, useState } from "react";
import toast from "react-hot-toast";
import { fetchUsers, fetchUserDetail, deleteUser } from "../../services/userService";
import { fetchRoles } from "../../services/roleService";
import CreateUserModal from "../../components/users/CreateUserModal";
import EditUserModal from "../../components/users/EditUserModal";
import Pagination from "../../components/common/Pagination";

export default function UserPage() {
  // filters
  const [roles, setRoles] = useState([]);
  const [roleId, setRoleId] = useState("");
  const [search, setSearch] = useState("");

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
      .catch(() => toast.error("❌ Gagal memuat role", { id: "roles-load" }));
  }, []);

  // param API
  const apiParams = useMemo(
    () => ({
      search: search?.trim() || undefined,
      roleId: roleId ? Number(roleId) : undefined,
      page: page - 1,
      size: rowsPerPage,
    }),
    [search, roleId, page, rowsPerPage]
  );

  async function load() {
    setLoading(true);
    try {
      const data = await fetchUsers(apiParams);
      setRows(data.content || data);
      setTotalPages(Math.max(data.totalPages || 1, 1));
      setTotalElements(data.totalElements ?? (data.content?.length ?? data.length ?? 0));
    } catch (e) {
      toast.error(e?.response?.data?.message ?? "❌ Gagal memuat data", { id: "users-load" });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
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
      toast.success("✅ User dihapus", { id: "user-delete" });
      load();
    } catch (e) {
      toast.error(e?.response?.data?.message ?? "❌ Gagal menghapus user", { id: "user-delete" });
    }
  }

  async function openDetail(id) {
    try {
      const u = await fetchUserDetail(id);
      setDetail(u);
      document.getElementById("detail_modal").showModal();
    } catch {
      toast.error("❌ Gagal memuat detail", { id: "user-detail" });
    }
  }

  // reset filter
  function resetFilter() {
    setRoleId("");
    setSearch("");
    setPage(1);
    toast.success("Clear filter berhasil");
  }

  const startIdx = totalElements === 0 ? 0 : (page - 1) * rowsPerPage + 1;

  return (
    <div>
      {/* Toolbar */}
      <div className="mb-4 space-y-3">
        {/* Row 1: Search + Tambah User */}
        <div className="grid grid-cols-1 lg:grid-cols-6 gap-3">
          <div className="col-span-1 lg:col-span-4">
            <input
              type="text"
              className="input input-sm input-bordered w-full"
              placeholder="🔍 Cari username / email…"
              value={search}
              onChange={(e) => {
                setPage(1);
                setSearch(e.target.value);
              }}
            />
          </div>
          <div className="col-span-1 lg:col-span-2">
            <button
              className="btn btn-primary btn-sm w-full"
              onClick={() => setOpenCreate(true)}
            >
              + Tambah User
            </button>
          </div>
        </div>

        {/* Row 2: Filters */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-6 gap-3 text-xs">
          <select
            className="select select-sm select-bordered w-full"
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
          <div>
            <button
              className="btn btn-accent btn-soft border-accent btn-sm w-full"
              onClick={resetFilter}
            >
              Clear Filter
            </button>
          </div>
        </div>
      </div>

      {/* Table */}
      <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
        <table className="table table-zebra">
          <thead className="bg-base-200 text-xs">
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
          <tbody className="text-xs">
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
                  <td>{startIdx + idx}</td>
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
                  <td className="flex gap-2 justify-end">
                    <button
                      className="btn btn-xs btn-warning"
                      onClick={() => setEditUser(u)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-xs btn-error"
                      onClick={() =>
                        setConfirm({ open: true, id: u.id, name: u.username })
                      }
                    >
                      Hapus
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination (seragam) */}
      <Pagination
        page={page}
        totalPages={totalPages}
        totalElements={totalElements}
        rowsPerPage={rowsPerPage}
        onPageChange={setPage}
        onRowsPerPageChange={(val) => {
          setRowsPerPage(val);
          setPage(1);
        }}
      />

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