import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import Select from "react-select";
import Pagination from "../../components/common/Pagination";
import { fetchJobCertMappingHistories } from "../../services/jobCertificationMappingHistoryService";
import { ArrowLeft } from "lucide-react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

export default function JobCertificationMappingHistoryPage() {
    const navigate = useNavigate();

    // State
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    // Filters
    const [filterAction, setFilterAction] = useState({ value: "all", label: "Semua Aksi" });
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);

    // Fetch data
    async function load() {
        setLoading(true);
        try {
            const params = {
                page: page - 1,
                size: rowsPerPage,
                actionType: filterAction?.value || "all",
                start: startDate ? startDate.toISOString() : null,
                end: endDate ? endDate.toISOString() : null,
            };
            const data = await fetchJobCertMappingHistories(params);
            setRows(data.content || []);
            setTotalPages(data.totalPages || 1);
            setTotalElements(data.totalElements || 0);
        } catch (err) {
            toast.error("❌ Gagal memuat histori mapping jabatan");
        } finally {
            setLoading(false);
        }
    }

    // Auto reload when filters/pagination change
    useEffect(() => {
        load();
    }, [page, rowsPerPage, filterAction, startDate, endDate]);

    // Reset page when filter changes
    useEffect(() => {
        setPage(1);
    }, [filterAction, startDate, endDate]);

    // Reset filter
    const resetFilter = () => {
        setFilterAction({ value: "all", label: "Semua Aksi" });
        setStartDate(null);
        setEndDate(null);
        toast.success("✅ Filter direset");
    };

    const formatDate = (val, withTime = true) => {
        if (!val) return "-";
        return new Date(val).toLocaleString("id-ID", {
            day: "2-digit",
            month: "short",
            year: "numeric",
            ...(withTime && { hour: "2-digit", minute: "2-digit" }),
        });
    };

    const startIdx = totalElements === 0 ? 0 : (page - 1) * rowsPerPage + 1;

    return (
        <div className="p-4 space-y-5">
            {/* Back button */}
            <div className="flex justify-start mb-3">
                <button className="btn btn-accent btn-sm flex items-center gap-2" onClick={() => navigate(-1)}>
                    <ArrowLeft size={16} /> Kembali
                </button>
            </div>

            {/* Filters */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-6 gap-3 text-xs items-center">
                <div className="col-span-2">
                    <Select
                        options={[
                            { value: "all", label: "Semua Aksi" },
                            { value: "CREATED", label: "CREATED" },
                            { value: "UPDATED", label: "UPDATED" },
                            { value: "TOGGLED", label: "TOGGLED" },
                            { value: "DELETED", label: "DELETED" },
                        ]}
                        value={filterAction}
                        onChange={setFilterAction}
                        placeholder="Filter Aksi"
                        isClearable
                    />
                </div>

                {/* Date Range */}
                <div className="col-span-2 grid grid-cols-2 gap-2">
                    <DatePicker
                        selected={startDate}
                        onChange={(date) => setStartDate(date)}
                        selectsStart
                        startDate={startDate}
                        endDate={endDate}
                        className="input input-bordered input-sm w-full"
                        placeholderText="Dari Tanggal"
                        dateFormat="dd MMM yyyy"
                    />
                    <DatePicker
                        selected={endDate}
                        onChange={(date) => setEndDate(date)}
                        selectsEnd
                        startDate={startDate}
                        endDate={endDate}
                        minDate={startDate}
                        className="input input-bordered input-sm w-full"
                        placeholderText="Sampai Tanggal"
                        dateFormat="dd MMM yyyy"
                    />
                </div>

                <div className="col-span-1">
                    <button
                        className="btn btn-warning btn-sm w-full"
                        onClick={() => toast("📥 Coming Soon: Export Excel")}
                    >
                        Export Excel
                    </button>
                </div>

                <div className="col-span-1">
                    <button className="btn btn-accent btn-soft border-accent btn-sm w-full" onClick={resetFilter}>
                        Clear Filter
                    </button>
                </div>
            </div>

            {/* Table */}
            <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
                <table className="table table-zebra text-xs">
                    <thead className="bg-base-200">
                        <tr>
                            <th>No</th>
                            <th>Aksi</th>
                            <th>Tanggal Aksi</th>
                            <th>Job</th>
                            <th>Cert Code</th>
                            <th>Level</th>
                            <th>Sub Field</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {loading ? (
                            <tr>
                                <td colSpan={8} className="text-center py-10">
                                    <span className="loading loading-dots loading-md" />
                                </td>
                            </tr>
                        ) : rows.length === 0 ? (
                            <tr>
                                <td colSpan={8} className="text-center text-gray-400 py-10">
                                    Tidak ada data
                                </td>
                            </tr>
                        ) : (
                            rows.map((r, idx) => (
                                <tr key={r.id || idx}>
                                    <td>{startIdx + idx}</td>
                                    <td>
                                        <span
                                            className={`badge badge-sm text-white ${
                                                r.actionType === "CREATED"
                                                    ? "badge-success"
                                                    : r.actionType === "UPDATED"
                                                    ? "badge-info"
                                                    : r.actionType === "TOGGLED"
                                                    ? "badge-warning"
                                                    : "badge-error"
                                            }`}
                                        >
                                            {r.actionType}
                                        </span>
                                    </td>
                                    <td>{formatDate(r.actionAt, true)}</td>
                                    <td>{r.jobName || "-"}</td>
                                    <td>{r.certificationCode || "-"}</td>
                                    <td>{r.certificationLevel || "-"}</td>
                                    <td>{r.subFieldCode || "-"}</td>
                                    <td>
                                        <span
                                            className={`badge badge-sm text-white ${
                                                r.isActive ? "badge-success" : "badge-error"
                                            }`}
                                        >
                                            {r.isActive ? "ACTIVE" : "INACTIVE"}
                                        </span>
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
        </div>
    );
}
