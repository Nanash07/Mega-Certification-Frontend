import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import Select from "react-select";
import AsyncSelect from "react-select/async";
import Pagination from "../../components/common/Pagination";
import { fetchEmployeeHistories } from "../../services/employeeHistoryService";
import { ArrowLeft } from "lucide-react";

export default function EmployeeHistoryPage() {
    const navigate = useNavigate();

    // State data
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(false);

    // Pagination
    const [page, setPage] = useState(1);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    // Filters
    const [filterEmployee, setFilterEmployee] = useState(null);
    const [filterAction, setFilterAction] = useState({ value: "all", label: "Semua Aksi" });
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");

    const [employeeOptions, setEmployeeOptions] = useState([]);

    // ===================== INIT EMPLOYEE LIST =====================
    const loadEmployeeList = useCallback(async () => {
        try {
            const data = await fetchEmployeeHistories({ page: 0, size: 500 });
            const uniqueEmps = new Map();
            data.content.forEach((h) => {
                if (h.employeeId && !uniqueEmps.has(h.employeeId)) {
                    uniqueEmps.set(h.employeeId, {
                        value: h.employeeId,
                        label: `${h.employeeNip} - ${h.employeeName}`,
                    });
                }
            });
            setEmployeeOptions(Array.from(uniqueEmps.values()));
        } catch (err) {
            console.error("❌ Gagal load list pegawai:", err);
        }
    }, []);

    // Filter function for react-select
    const loadEmployeeOptions = (inputValue) =>
        new Promise((resolve) => {
            if (!inputValue) resolve(employeeOptions.slice(0, 20));
            else {
                const filtered = employeeOptions.filter((e) =>
                    e.label.toLowerCase().includes(inputValue.toLowerCase())
                );
                resolve(filtered.slice(0, 20));
            }
        });

    // ===================== LOAD HISTORIES =====================
    const load = useCallback(async () => {
        setLoading(true);
        try {
            if (startDate && endDate && startDate > endDate) {
                toast.error("Tanggal awal tidak boleh melebihi tanggal akhir");
                setLoading(false);
                return;
            }

            const params = {
                page: page - 1,
                size: rowsPerPage,
                actionType: filterAction?.value || "all",
                employeeId: filterEmployee?.value || null,
                startDate,
                endDate,
            };

            const data = await fetchEmployeeHistories(params);
            setRows(data.content || []);
            setTotalPages(data.totalPages || 1);
            setTotalElements(data.totalElements || 0);
        } catch (err) {
            toast.error("Gagal memuat histori pegawai");
        } finally {
            setLoading(false);
        }
    }, [page, rowsPerPage, filterEmployee, filterAction, startDate, endDate]);

    // Initial load
    useEffect(() => {
        loadEmployeeList();
    }, [loadEmployeeList]);

    useEffect(() => {
        load();
    }, [load]);

    // Reset filter
    const resetFilter = () => {
        setFilterEmployee(null);
        setFilterAction({ value: "all", label: "Semua Aksi" });
        setStartDate("");
        setEndDate("");
        toast.success("Filter berhasil direset");
    };

    // Utils
    const formatDate = (val) => {
        if (!val) return "-";
        return new Date(val).toLocaleDateString("id-ID", {
            day: "2-digit",
            month: "short",
            year: "numeric",
        });
    };

    const startIdx = totalElements === 0 ? 0 : (page - 1) * rowsPerPage + 1;

    // ===================== UI =====================
    return (
        <div className="p-4 space-y-5">
            {/* Back button */}
            <div className="flex justify-start mb-3">
                <button className="btn btn-accent btn-sm flex items-center gap-2" onClick={() => navigate(-1)}>
                    <ArrowLeft size={16} /> Kembali
                </button>
            </div>

            {/* Filters */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-6 gap-3 text-xs items-end">
                {/* Pegawai */}
                <AsyncSelect
                    cacheOptions
                    defaultOptions={employeeOptions}
                    loadOptions={loadEmployeeOptions}
                    value={filterEmployee}
                    onChange={setFilterEmployee}
                    placeholder="Filter Pegawai"
                    isClearable
                />

                {/* Aksi */}
                <Select
                    options={[
                        { value: "all", label: "Semua Aksi" },
                        { value: "CREATED", label: "CREATED" },
                        { value: "UPDATED", label: "UPDATED" },
                        { value: "MUTASI", label: "MUTASI" },
                        { value: "RESIGN", label: "RESIGN" },
                        { value: "TERMINATED", label: "TERMINATED" },
                    ]}
                    value={filterAction}
                    onChange={setFilterAction}
                    placeholder="Filter Aksi"
                />

                {/* Start Date */}
                <input
                    type="date"
                    className="input input-bordered input-sm w-full"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                />

                {/* End Date */}
                <input
                    type="date"
                    className="input input-bordered input-sm w-full"
                    value={endDate}
                    min={startDate}
                    onChange={(e) => setEndDate(e.target.value)}
                />

                {/* Export */}
                <button className="btn btn-warning btn-sm w-full" onClick={() => toast("📥 Coming Soon: Export Excel")}>
                    Export Excel
                </button>

                {/* Clear Filter */}
                <button className="btn btn-accent btn-soft border-accent btn-sm w-full" onClick={resetFilter}>
                    Clear Filter
                </button>
            </div>

            {/* Table */}
            <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
                <table className="table table-zebra text-xs">
                    <thead className="bg-base-200">
                        <tr>
                            <th>No</th>
                            <th>Aksi</th>
                            <th>Tanggal Aksi</th>
                            <th>NIP</th>
                            <th>Nama Pegawai</th>
                            <th>Jabatan Lama</th>
                            <th>Jabatan Baru</th>
                            <th>Unit</th>
                            <th>Divisi</th>
                            <th>Regional</th>
                            <th>Tanggal Efektif</th>
                        </tr>
                    </thead>
                    <tbody>
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
                            rows.map((h, idx) => (
                                <tr key={h.id || idx}>
                                    <td>{startIdx + idx}</td>
                                    <td>
                                        <span
                                            className={`badge badge-sm text-white ${
                                                h.actionType === "CREATED"
                                                    ? "badge-success"
                                                    : h.actionType === "UPDATED"
                                                    ? "badge-info"
                                                    : h.actionType === "MUTASI"
                                                    ? "badge-warning"
                                                    : h.actionType === "RESIGN"
                                                    ? "badge-neutral"
                                                    : "badge-error"
                                            }`}
                                        >
                                            {h.actionType}
                                        </span>
                                    </td>
                                    <td>{formatDate(h.actionAt)}</td>
                                    <td>{h.employeeNip || "-"}</td>
                                    <td>{h.employeeName || "-"}</td>
                                    <td>{h.oldJobTitle || "-"}</td>
                                    <td>{h.newJobTitle || "-"}</td>
                                    <td>{h.newUnitName || "-"}</td>
                                    <td>{h.newDivisionName || "-"}</td>
                                    <td>{h.newRegionalName || "-"}</td>
                                    <td>{formatDate(h.effectiveDate)}</td>
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
        </div>
    );
}
