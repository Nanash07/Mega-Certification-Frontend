import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import Select from "react-select";
import AsyncSelect from "react-select/async";
import Pagination from "../../components/common/Pagination";
import {
  fetchEmployeeEligibilityPaged,
  refreshEmployeeEligibility,
} from "../../services/employeeEligibilityService";
import { fetchAllJobPositions } from "../../services/jobPositionService";
import { fetchCertifications } from "../../services/certificationService";
import { fetchCertificationLevels } from "../../services/certificationLevelService";
import { fetchSubFields } from "../../services/subFieldService";
import { searchEmployees } from "../../services/employeeService";

export default function EmployeeEligibilityPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

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

  const [filterEmployee, setFilterEmployee] = useState(null);
  const [filterJob, setFilterJob] = useState([]);
  const [filterCert, setFilterCert] = useState([]);
  const [filterLevel, setFilterLevel] = useState([]);
  const [filterSub, setFilterSub] = useState([]);
  const [filterStatus, setFilterStatus] = useState([]);
  const [filterSource, setFilterSource] = useState([]);

  // 🔹 Load data
  async function load() {
    setLoading(true);
    try {
      const params = {
        page: page - 1,
        size: rowsPerPage,
        employeeIds: filterEmployee ? [filterEmployee.value] : [],
        jobIds: filterJob.map((f) => f.value),
        certCodes: filterCert.map((f) => f.value),
        levels: filterLevel.map((f) => f.value),
        subCodes: filterSub.map((f) => f.value),
        statuses: filterStatus.map((f) => f.value),
        sources: filterSource.map((f) => f.value),
      };

      const res = await fetchEmployeeEligibilityPaged(params);
      setRows(res.content || []);
      setTotalPages(res.totalPages || 1);
      setTotalElements(res.totalElements || 0);
    } catch {
      toast.error("Gagal memuat eligibility");
    } finally {
      setLoading(false);
    }
  }

  async function onRefresh() {
    setRefreshing(true);
    try {
      await refreshEmployeeEligibility();
      toast.success("Eligibility berhasil di-refresh");
      load();
    } catch {
      toast.error("Gagal refresh eligibility");
    } finally {
      setRefreshing(false);
    }
  }

  // 🔹 Load filter options
  async function loadFilters() {
    try {
      const [jobs, certs, levels, subs] = await Promise.all([
        fetchAllJobPositions(),
        fetchCertifications(),
        fetchCertificationLevels(),
        fetchSubFields(),
      ]);

      setJobOptions(jobs.map((j) => ({ value: j.id, label: j.name })));
      setCertOptions(certs.map((c) => ({ value: c.code, label: c.code })));
      setLevelOptions(levels.map((l) => ({ value: l.level, label: l.level })));
      setSubOptions(subs.map((s) => ({ value: s.code, label: s.code })));
    } catch (err) {
      console.error("loadFilters error:", err);
      toast.error("Gagal memuat filter");
    }
  }

  // 🔹 Async search employees (limit 20)
  const loadEmployees = async (inputValue) => {
    try {
      const res = await searchEmployees({
        search: inputValue,
        page: 0,
        size: 20,
      });
      return res.content.map((e) => ({
        value: e.id,
        label: `${e.nip} - ${e.name}`,
      }));
    } catch {
      return [];
    }
  };

  useEffect(() => {
    load();
  }, [
    page,
    rowsPerPage,
    filterEmployee,
    filterJob,
    filterCert,
    filterLevel,
    filterSub,
    filterStatus,
    filterSource,
  ]);

  useEffect(() => {
    loadFilters();
  }, []);

  const startIdx = totalElements === 0 ? 0 : (page - 1) * rowsPerPage + 1;

  return (
    <div>
      {/* Toolbar */}
      <div className="mb-4 space-y-3">
        <div className="grid grid-cols-1 lg:grid-cols-6 gap-3">
          <div className="col-span-2">
            <button
              className="btn btn-primary btn-sm w-full flex items-center justify-center gap-2"
              onClick={onRefresh}
              disabled={refreshing}
            >
              {refreshing && (
                <span className="loading loading-spinner loading-xs" />
              )}
              {refreshing ? "Refreshing..." : "Refresh Eligibility"}
            </button>
          </div>
          <div className="col-span-1">
            <button
              className="btn btn-sm btn-accent w-full"
              onClick={() => {
                setFilterEmployee(null);
                setFilterJob([]);
                setFilterCert([]);
                setFilterLevel([]);
                setFilterSub([]);
                setFilterStatus([]);
                setFilterSource([]);
                setPage(1);
                toast.success("Clear filter berhasil");
              }}
            >
              Clear Filter
            </button>
          </div>
        </div>

        {/* Filters */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-7 gap-3 text-xs">
          <AsyncSelect
            cacheOptions
            defaultOptions
            loadOptions={loadEmployees}
            value={filterEmployee}
            onChange={setFilterEmployee}
            placeholder="Filter Pegawai"
            isClearable
          />
          <Select
            isMulti
            options={jobOptions}
            value={filterJob}
            onChange={setFilterJob}
            placeholder="Filter Jabatan"
          />
          <Select
            isMulti
            options={certOptions}
            value={filterCert}
            onChange={setFilterCert}
            placeholder="Filter Sertifikasi"
          />
          <Select
            isMulti
            options={levelOptions}
            value={filterLevel}
            onChange={setFilterLevel}
            placeholder="Filter Level"
          />
          <Select
            isMulti
            options={subOptions}
            value={filterSub}
            onChange={setFilterSub}
            placeholder="Filter Sub Bidang"
          />
          <Select
            isMulti
            options={[
              { value: "BELUM_SERTIFIKASI", label: "Belum Sertifikasi" },
              { value: "AKTIF", label: "Aktif" },
              { value: "DUE", label: "Mau Habis" },
              { value: "EXPIRED", label: "Expired" },
            ]}
            value={filterStatus}
            onChange={setFilterStatus}
            placeholder="Filter Status"
          />
          <Select
            isMulti
            options={[
              { value: "BY_JOB", label: "By Job" },
              { value: "BY_NAME", label: "By Name" },
            ]}
            value={filterSource}
            onChange={setFilterSource}
            placeholder="Filter Sumber"
          />
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
              <th>SK Efektif</th>
              <th>Status</th>
              <th>Due Date</th>
              <th>Sumber</th>
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
                  <td>{r.nip}</td>
                  <td>{r.employeeName}</td>
                  <td>{r.jobPositionTitle}</td>
                  <td>{r.certificationCode}</td>
                  <td>{r.certificationLevelName || "-"}</td>
                  <td>{r.subFieldCode || "-"}</td>
                  <td>
                    {r.joinDate
                      ? new Date(r.joinDate).toLocaleDateString("id-ID")
                      : "-"}
                  </td>
                  <td>{r.status}</td>
                  <td>
                    {r.dueDate
                      ? new Date(r.dueDate).toLocaleDateString("id-ID")
                      : "-"}
                  </td>
                  <td>{r.source === "BY_JOB" ? "By Job" : "By Name"}</td>
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