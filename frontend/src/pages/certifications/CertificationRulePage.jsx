import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import Select from "react-select";
import Pagination from "../../components/common/Pagination";
import {
    fetchCertificationRulesPaged,
    deleteCertificationRule,
    toggleCertificationRule,
} from "../../services/certificationRuleService";
import { fetchCertifications } from "../../services/certificationService";
import { fetchCertificationLevels } from "../../services/certificationLevelService";
import { fetchSubFields } from "../../services/subFieldService";
import CreateCertificationRuleModal from "../../components/certification-rules/CreateCertificationRuleModal";
import EditCertificationRuleModal from "../../components/certification-rules/EditCertificationRuleModal";

export default function CertificationRulePage() {
    const navigate = useNavigate();
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(false);
    const [openCreate, setOpenCreate] = useState(false);
    const [editItem, setEditItem] = useState(null);
    const [confirm, setConfirm] = useState({ open: false, id: undefined });

    // Pagination
    const [page, setPage] = useState(1);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    // Filters
    const [certOptions, setCertOptions] = useState([]);
    const [levelOptions, setLevelOptions] = useState([]);
    const [subOptions, setSubOptions] = useState([]);

    const [filterCert, setFilterCert] = useState([]);
    const [filterLevel, setFilterLevel] = useState([]);
    const [filterSub, setFilterSub] = useState([]);
    const [filterStatus, setFilterStatus] = useState({
        value: "all",
        label: "Semua",
    });

    // Load data
    async function load() {
        setLoading(true);
        try {
            const params = {
                page: page - 1,
                size: rowsPerPage,
                status: filterStatus?.value || "all",
            };

            if (filterCert.length > 0) params.certIds = filterCert.map((f) => f.value);
            if (filterLevel.length > 0) params.levelIds = filterLevel.map((f) => f.value);
            if (filterSub.length > 0) params.subIds = filterSub.map((f) => f.value);

            const res = await fetchCertificationRulesPaged(params);
            setRows(res.content || []);
            setTotalPages(res.totalPages || 1);
            setTotalElements(res.totalElements || 0);
        } catch (err) {
            toast.error("Gagal memuat aturan sertifikasi");
            console.error("fetchCertificationRulesPaged error:", err);
        } finally {
            setLoading(false);
        }
    }

    async function loadFilters() {
        try {
            const [certs, levels, subs] = await Promise.all([
                fetchCertifications(),
                fetchCertificationLevels(),
                fetchSubFields(),
            ]);

            setCertOptions(certs.map((c) => ({ value: c.id, label: c.code })));
            setLevelOptions(levels.map((l) => ({ value: l.id, label: `${l.level}` })));
            setSubOptions(subs.map((s) => ({ value: s.id, label: s.code })));
        } catch {
            toast.error("Gagal memuat data filter");
        }
    }

    useEffect(() => {
        load();
    }, [page, rowsPerPage, filterCert, filterLevel, filterSub, filterStatus]);

    useEffect(() => {
        loadFilters();
    }, []);

    async function onDelete(id) {
        try {
            await deleteCertificationRule(id);
            toast.success("Aturan sertifikasi dihapus");
            load();
        } catch {
            toast.error("Gagal menghapus aturan sertifikasi");
        }
    }

    async function onToggle(id) {
        try {
            await toggleCertificationRule(id);
            toast.success("Status berhasil diperbarui");
            load();
        } catch {
            toast.error("Gagal update status");
        }
    }

    const startIdx = totalElements === 0 ? 0 : (page - 1) * rowsPerPage + 1;

    return (
        <div>
            {/* Toolbar Filter + Actions */}
            <div className="mb-4 grid grid-cols-1 md:grid-cols-6 gap-3 items-end text-xs">
                {/* Filter Sertifikasi */}
                <div className="col-span-1">
                    <Select
                        options={certOptions}
                        value={filterCert}
                        onChange={setFilterCert}
                        isClearable
                        isMulti
                        placeholder="Filter Sertifikasi"
                    />
                </div>

                {/* Filter Level */}
                <div className="col-span-1">
                    <Select
                        options={levelOptions}
                        value={filterLevel}
                        onChange={setFilterLevel}
                        isClearable
                        isMulti
                        placeholder="Filter Level"
                    />
                </div>

                {/* Filter Sub Bidang */}
                <div className="col-span-1">
                    <Select
                        options={subOptions}
                        value={filterSub}
                        onChange={setFilterSub}
                        isClearable
                        isMulti
                        placeholder="Filter Sub Bidang"
                    />
                </div>

                {/* Filter Status */}
                <div className="col-span-1">
                    <Select
                        options={[
                            { value: "all", label: "Semua" },
                            { value: "active", label: "Aktif" },
                            { value: "inactive", label: "Nonaktif" },
                        ]}
                        value={filterStatus}
                        onChange={setFilterStatus}
                        placeholder="Status"
                    />
                </div>

                {/* Tombol Tambah Aturan */}
                <div className="col-span-1 flex justify-end">
                    <button className="btn btn-primary btn-sm w-full" onClick={() => setOpenCreate(true)}>
                        + Tambah Aturan
                    </button>
                </div>

                {/* Tombol Histori Global */}
                <div className="col-span-1 flex justify-end">
                    <button
                        className="btn btn-accent btn-sm w-full"
                        onClick={() => navigate("/sertifikasi/aturan-sertifikat/histories")}
                    >
                        Histori
                    </button>
                </div>
            </div>

            {/* Table */}
            <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
                <table className="table table-zebra">
                    <thead className="bg-base-200 text-xs">
                        <tr>
                            <th>No</th>
                            <th>Sertifikasi</th>
                            <th>Jenjang</th>
                            <th>Sub Bidang</th>
                            <th>Masa Berlaku</th>
                            <th>Reminder</th>
                            <th>Refreshment</th>
                            <th>Wajib Setelah Masuk</th>
                            <th>Status</th>
                            <th>Updated At</th>
                            <th>Aksi</th>
                        </tr>
                    </thead>
                    <tbody className="text-xs">
                        {loading ? (
                            <tr>
                                <td colSpan={11} className="text-center py-10">
                                    <span className="loading loading-dots loading-md" />
                                </td>
                            </tr>
                        ) : rows.length === 0 ? (
                            <tr>
                                <td colSpan={11} className="text-center text-gray-400 py-10">
                                    Tidak ada data
                                </td>
                            </tr>
                        ) : (
                            rows.map((r, idx) => (
                                <tr key={r.id}>
                                    <td>{startIdx + idx}</td>
                                    <td>{r.certificationCode}</td>
                                    <td>{r.certificationLevelLevel || "-"}</td>
                                    <td>{r.subFieldCode || "-"}</td>
                                    <td>{r.validityMonths} bulan</td>
                                    <td>{r.reminderMonths} bulan</td>
                                    <td>{r.refreshmentTypeName || "-"}</td>
                                    <td>{r.wajibSetelahMasuk != null ? `${r.wajibSetelahMasuk} bulan` : "-"}</td>
                                    <td>
                                        {r.isActive ? (
                                            <span className="badge badge-success badge-sm text-white">ACTIVE</span>
                                        ) : (
                                            <span className="badge badge-warning badge-sm text-white">NONACTIVE</span>
                                        )}
                                    </td>
                                    <td>
                                        {r.updatedAt
                                            ? new Date(r.updatedAt).toLocaleDateString("id-ID", {
                                                  day: "2-digit",
                                                  month: "short",
                                                  year: "numeric",
                                              })
                                            : "-"}
                                    </td>
                                    <td className="space-x-1 space-y-1">
                                        {/* 🔹 Tombol Histori per baris dihapus */}
                                        <button
                                            className={`btn btn-xs ${
                                                r.isActive
                                                    ? "btn-warning btn-soft border-warning"
                                                    : "btn-success btn-soft border-success"
                                            }`}
                                            onClick={() => onToggle(r.id)}
                                        >
                                            {r.isActive ? "Nonaktifkan" : "Aktifkan"}
                                        </button>
                                        <button
                                            className="btn btn-xs btn-soft btn-warning border-warning"
                                            onClick={() => setEditItem(r)}
                                        >
                                            Edit
                                        </button>
                                        <button
                                            className="btn btn-xs btn-soft btn-error border-error"
                                            onClick={() => setConfirm({ open: true, id: r.id })}
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
            <CreateCertificationRuleModal
                open={openCreate}
                onClose={() => setOpenCreate(false)}
                onSaved={() => {
                    setOpenCreate(false);
                    load();
                }}
            />
            <EditCertificationRuleModal
                open={!!editItem}
                initial={editItem}
                onClose={() => setEditItem(null)}
                onSaved={() => {
                    setEditItem(null);
                    load();
                }}
            />

            {/* Confirm Delete */}
            <dialog className="modal" open={confirm.open}>
                <div className="modal-box">
                    <h3 className="font-bold text-lg">Hapus Aturan Sertifikasi?</h3>
                    <p className="py-2">Data ini akan dinonaktifkan dari sistem.</p>
                    <div className="modal-action">
                        <button className="btn" onClick={() => setConfirm({ open: false })}>
                            Batal
                        </button>
                        <button
                            className="btn btn-error"
                            onClick={() => {
                                onDelete(confirm.id);
                                setConfirm({ open: false });
                            }}
                        >
                            Hapus
                        </button>
                    </div>
                </div>
                <form method="dialog" className="modal-backdrop">
                    <button>close</button>
                </form>
            </dialog>
        </div>
    );
}
