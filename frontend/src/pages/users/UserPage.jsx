import { useEffect, useState, useCallback } from "react";
import toast from "react-hot-toast";
import Select from "react-select";
import AsyncSelect from "react-select/async";
import Pagination from "../../components/common/Pagination";
import { fetchUsers, deleteUser, toggleUser, fetchActiveUsers } from "../../services/userService";
import { fetchRoles } from "../../services/roleService";
import CreateUserModal from "../../components/users/CreateUserModal";
import EditUserModal from "../../components/users/EditUserModal";

export default function UserPage() {
    // ===================== STATE =====================
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(false);

    const [page, setPage] = useState(1);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    const [roles, setRoles] = useState([]);
    const [filterUser, setFilterUser] = useState(null);
    const [filterRole, setFilterRole] = useState([]);
    const [filterStatus, setFilterStatus] = useState([]);

    const [showCreateModal, setShowCreateModal] = useState(false);
    const [editData, setEditData] = useState(null);
    const [confirm, setConfirm] = useState({ open: false, id: null });

    // ===================== LOAD ROLES =====================
    useEffect(() => {
        fetchRoles()
            .then(setRoles)
            .catch(() => toast.error("Gagal memuat role"));
    }, []);

    // ===================== LOAD USERS =====================
    const loadUsers = useCallback(async () => {
        setLoading(true);
        try {
            const params = {
                page: page - 1,
                size: rowsPerPage,
                roleId: filterRole?.value || undefined,
                isActive: filterStatus?.value === "all" ? undefined : filterStatus?.value ?? undefined,
                q: filterUser?.label || undefined,
            };

            const res = await fetchUsers(params);
            setRows(res.content || []);
            setTotalPages(res.totalPages || 1);
            setTotalElements(res.totalElements || 0);
        } catch {
            toast.error("Gagal memuat data user");
        } finally {
            setLoading(false);
        }
    }, [page, rowsPerPage, filterUser, filterRole, filterStatus]);

    useEffect(() => {
        loadUsers();
    }, [loadUsers]);

    // ===================== ASYNC SEARCH USER =====================
    const loadUserOptions = async (inputValue) => {
        try {
            const users = await fetchActiveUsers(inputValue);
            return users.slice(0, 20).map((u) => ({
                value: u.id,
                label: `${u.username}${u.employeeName ? ` - ${u.employeeName}` : ""}`,
            }));
        } catch {
            return [];
        }
    };

    // ===================== HANDLERS =====================
    async function onDelete(id) {
        try {
            await deleteUser(id);
            toast.success("User dihapus");
            loadUsers();
        } catch {
            toast.error("Gagal menghapus user");
        }
    }

    async function onToggle(id) {
        try {
            const updated = await toggleUser(id);
            toast.success(`User ${updated.isActive ? "diaktifkan" : "dinonaktifkan"}`);
            loadUsers();
        } catch {
            toast.error("Gagal mengubah status user");
        }
    }

    const resetFilter = () => {
        setFilterUser(null);
        setFilterRole([]);
        setFilterStatus([]);
        setPage(1);
        toast.success("Filter direset");
    };

    const startIdx = totalElements === 0 ? 0 : (page - 1) * rowsPerPage + 1;

    // ===================== UI =====================
    return (
        <div>
            {/* Toolbar */}
            <div className="mb-4 space-y-3">
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3 text-xs">
                    <AsyncSelect
                        cacheOptions
                        defaultOptions
                        loadOptions={loadUserOptions}
                        value={filterUser}
                        onChange={setFilterUser}
                        placeholder="Cari User"
                        isClearable
                    />

                    <Select
                        options={roles.map((r) => ({ value: r.id, label: r.name }))}
                        value={filterRole}
                        onChange={setFilterRole}
                        placeholder="Filter Role"
                        isClearable
                    />

                    <Select
                        options={[
                            { value: "all", label: "Semua Status" },
                            { value: true, label: "Aktif" },
                            { value: false, label: "Nonaktif" },
                        ]}
                        value={filterStatus}
                        onChange={setFilterStatus}
                        placeholder="Filter Status"
                        isClearable
                    />

                    <button className="btn btn-sm btn-primary w-full" onClick={() => setShowCreateModal(true)}>
                        + Tambah User
                    </button>

                    <button className="btn btn-sm btn-accent btn-soft border-accent w-full" onClick={resetFilter}>
                        Reset Filter
                    </button>
                </div>
            </div>

            {/* Table */}
            <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
                <table className="table table-zebra">
                    <thead className="bg-base-200 text-xs">
                        <tr>
                            <th>No</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Nama Pegawai</th>
                            <th>NIP</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Updated At</th>
                            <th>Aksi</th>
                        </tr>
                    </thead>
                    <tbody className="text-xs">
                        {loading ? (
                            <tr>
                                <td colSpan={9} className="text-center py-10">
                                    <span className="loading loading-dots loading-md" />
                                </td>
                            </tr>
                        ) : rows.length === 0 ? (
                            <tr>
                                <td colSpan={9} className="text-center text-gray-400 py-10">
                                    Tidak ada data
                                </td>
                            </tr>
                        ) : (
                            rows.map((u, idx) => (
                                <tr key={u.id}>
                                    <td>{startIdx + idx}</td>
                                    <td>{u.username}</td>
                                    <td>{u.email || "-"}</td>
                                    <td>{u.employeeName || "-"}</td>
                                    <td>{u.employeeNip || "-"}</td>
                                    <td>{u.roleName || "-"}</td>
                                    <td>
                                        {u.isActive ? (
                                            <span className="badge badge-success border-success badge-sm text-slate-50">
                                                ACTIVE
                                            </span>
                                        ) : (
                                            <span className="badge badge-secondary border-secondary badge-sm text-slate-50">
                                                NONACTIVE
                                            </span>
                                        )}
                                    </td>
                                    <td>
                                        {u.updatedAt
                                            ? new Date(u.updatedAt).toLocaleDateString("id-ID", {
                                                  day: "2-digit",
                                                  month: "short",
                                                  year: "numeric",
                                                  hour: "2-digit",
                                                  minute: "2-digit",
                                              })
                                            : "-"}
                                    </td>
                                    <td className="space-x-1 whitespace-nowrap">
                                        <button
                                            className={`btn btn-xs ${
                                                u.isActive
                                                    ? "btn-secondary border-secondary btn-soft"
                                                    : "btn-success border-success btn-soft"
                                            }`}
                                            onClick={() => onToggle(u.id)}
                                        >
                                            {u.isActive ? "Nonaktifkan" : "Aktifkan"}
                                        </button>
                                        <button
                                            className="btn btn-xs border-warning btn-soft btn-warning"
                                            onClick={() => setEditData(u)}
                                        >
                                            Edit
                                        </button>
                                        <button
                                            className="btn btn-xs border-error btn-soft btn-error"
                                            onClick={() => setConfirm({ open: true, id: u.id })}
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

            {/* Pagination */}
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
            {showCreateModal && (
                <CreateUserModal
                    open={showCreateModal}
                    onClose={() => setShowCreateModal(false)}
                    onSaved={loadUsers}
                    roles={roles}
                />
            )}
            {editData && (
                <EditUserModal
                    open={!!editData}
                    onClose={() => setEditData(null)}
                    onSaved={loadUsers}
                    roles={roles}
                    initial={editData}
                />
            )}

            {/* Modal Delete */}
            {confirm.open && (
                <dialog className="modal" open={confirm.open}>
                    <div className="modal-box">
                        <h3 className="font-bold text-lg">Hapus User?</h3>
                        <p className="py-2">User ini akan dihapus dari sistem.</p>
                        <div className="modal-action">
                            <button className="btn" onClick={() => setConfirm({ open: false, id: null })}>
                                Batal
                            </button>
                            <button
                                className="btn btn-error"
                                onClick={async () => {
                                    await onDelete(confirm.id);
                                    setConfirm({ open: false, id: null });
                                }}
                            >
                                Hapus
                            </button>
                        </div>
                    </div>
                    <form method="dialog" className="modal-backdrop">
                        <button onClick={() => setConfirm({ open: false, id: null })}>close</button>
                    </form>
                </dialog>
            )}
        </div>
    );
}
