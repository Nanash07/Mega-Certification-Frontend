import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import Select from "react-select";
import Pagination from "../../components/common/Pagination";
import {
  fetchManualEligibilities,
  createManualEligibility,
  toggleManualEligibility,
  deleteManualEligibility,
} from "../../services/employeeEligibilityManualService";
import { fetchAllJobPositions } from "../../services/jobPositionService";
import { fetchCertifications } from "../../services/certificationService";
import { fetchCertificationLevels } from "../../services/certificationLevelService";
import { fetchSubFields } from "../../services/subFieldService";

export default function EmployeeEligibilityManualPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);

  // 🔹 Pagination
  const [page, setPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // 🔹 Filters
  const [jobOptions, setJobOptions] = useState([]);
  const [certOptions, setCertOptions] = useState([]);
  const [levelOptions, setLevelOptions] = useState([]);
  const [subOptions, setSubOptions] = useState([]);

  const [filterJob, setFilterJob] = useState([]);
  const [filterCert, setFilterCert] = useState([]);
  const [filterLevel, setFilterLevel] = useState([]);
  const [filterSub, setFilterSub] = useState([]);
  const [filterStatus, setFilterStatus] = useState([]);
  const [search, setSearch] = useState("");

  async function load() {
    setLoading(true);
    try {
      const params = {
        page: page - 1,
        size: rowsPerPage,
        jobIds: filterJob.map((f) => f.value),
        certCodes: filterCert.map((f) => f.value),
        levels: filterLevel.map((f) => f.value),
        subCodes: filterSub.map((f) => f.value),
        statuses: filterStatus.map((f) => f.value),
        search: search || null,
      };
      const res = await fetchManualEligibilities(params);
      setRows(res.content || []);
      setTotalPages(res.totalPages || 1);
      setTotalElements(res.totalElements || 0);
    } catch {
      toast.error("❌ Gagal memuat eligibility manual");
    } finally {
      setLoading(false);
    }
  }

  async function onAddManual() {
    const employeeId = prompt("Masukkan Employee ID:");
    const ruleId = prompt("Masukkan Certification Rule ID:");
    if (!employeeId || !ruleId) return;
    try {
      await createManualEligibility(employeeId, ruleId);
      toast.success("✅ Eligibility manual ditambahkan");
      load();
    } catch {
      toast.error("❌ Gagal menambah eligibility manual");
    }
  }

  useEffect(() => {
    load();
  }, [page, rowsPerPage, filterJob, filterCert, filterLevel, filterSub, filterStatus, search]);

  useEffect(() => {
    (async () => {
      try {
        const [jobs, certs, levels, subs] = await Promise.all([
          fetchAllJobPositions(),
          fetchCertifications(),
          fetchCertificationLevels(),
          fetchSubFields(),
        ]);
        setJobOptions(jobs.map((j) => ({ value: j.id, label: j.name })));
        setCertOptions(certs.map((c) => ({ value: c.code, label: `${c.code} - ${c.name}` })));
        setLevelOptions(levels.map((l) => ({ value: l.level, label: `${l.level} - ${l.name}` })));
        setSubOptions(subs.map((s) => ({ value: s.code, label: `${s.code} - ${s.name}` })));
      } catch (err) {
        console.error("❌ loadFilters error:", err);
        toast.error("❌ Gagal memuat filter");
      }
    })();
  }, []);

  const startIdx = totalElements === 0 ? 0 : (page - 1) * rowsPerPage + 1;

  return (
    <div>
      {/* Toolbar */}
      <div className="mb-4 space-y-3">
        <div className="flex justify-between gap-3">
          <input
            type="text"
            className="input input-sm input-bordered w-full"
            placeholder="🔍 Cari NIP, nama, atau jabatan..."
            value={search}
            onChange={(e) => {
              setPage(1);
              setSearch(e.target.value);
            }}
          />
          <button className="btn btn-primary btn-sm" onClick={onAddManual}>
            + Tambah Manual
          </button>
        </div>
      </div>

      {/* Table */}
      <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
        <table className="table table-zebra">
          <thead className="bg-base-200 text-xs">
            <tr>
              <th>No</th>
              <th>NIP</th>
              <th>Nama Pegawai</th>
              <th>Jabatan</th>
              <th>Kode Sertifikasi</th>
              <th>Level</th>
              <th>Sub Bidang</th>
              <th>Status</th>
              <th>Due Date</th>
              <th>Aksi</th>
            </tr>
          </thead>
          <tbody className="text-xs">
            {loading ? (
              <tr>
                <td colSpan={10} className="text-center py-10">
                  <span className="loading loading-dots loading-md" />
                </td>
              </tr>
            ) : rows.length === 0 ? (
              <tr>
                <td colSpan={10} className="text-center text-gray-400 py-10">
                  Tidak ada data
                </td>
              </tr>
            ) : (
              rows.map((r, idx) => (
                <tr key={r.id}>
                  <td>{startIdx + idx}</td>
                  <td>{r.nip}</td>
                  <td>{r.employeeName}</td>
                  <td>{r.jobPositionTitle}</td>
                  <td>{r.certificationCode}</td>
                  <td>{r.certificationLevelName || "-"}</td>
                  <td>{r.subFieldCode || "-"}</td>
                  <td>{r.status}</td>
                  <td>{r.dueDate || "-"}</td>
                  <td className="flex gap-2">
                    <button
                      className="btn btn-xs btn-warning"
                      onClick={async () => {
                        await toggleManualEligibility(r.id);
                        toast.success("✅ Status eligibility diubah");
                        load();
                      }}
                    >
                      Toggle
                    </button>
                    <button
                      className="btn btn-xs btn-error"
                      onClick={async () => {
                        if (confirm("Hapus eligibility manual ini?")) {
                          await deleteManualEligibility(r.id);
                          toast.success("✅ Dihapus");
                          load();
                        }
                      }}
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
    </div>
  );
}