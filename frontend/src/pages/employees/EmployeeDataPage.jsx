import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import Select from "react-select";
import {
  fetchEmployees,
  deleteEmployee,
  importEmployeesExcel,
  downloadEmployeeTemplate,
  fetchRegionals,
  fetchDivisions,
  fetchUnits,
  fetchJobPositions,
} from "../../services/employeeService";
import Pagination from "../../components/common/Pagination";
import CreateEmployeeModal from "../../components/employees/CreateEmployeeModal";
import EditEmployeeModal from "../../components/employees/EditEmployeeModal";

export default function EmployeePage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);

  // Pagination
  const [page, setPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Search
  const [search, setSearch] = useState("");

  // Filters
  const [regionalIds, setRegionalIds] = useState([]);
  const [divisionIds, setDivisionIds] = useState([]);
  const [unitIds, setUnitIds] = useState([]);
  const [jobPositionIds, setJobPositionIds] = useState([]);
  const [statuses, setStatuses] = useState([]);

  // Master options
  const [regionalOptions, setRegionalOptions] = useState([]);
  const [divisionOptions, setDivisionOptions] = useState([]);
  const [unitOptions, setUnitOptions] = useState([]);
  const [jobOptions, setJobOptions] = useState([]);
  const statusOptions = [
    { value: "ACTIVE", label: "Active" },
    { value: "INACTIVE", label: "Inactive" },
    { value: "MUTASI", label: "Mutasi" },
  ];

  // Modals
  const [openCreate, setOpenCreate] = useState(false);
  const [editItem, setEditItem] = useState(null);

  // Load master data
  useEffect(() => {
    Promise.all([fetchRegionals(), fetchDivisions(), fetchUnits(), fetchJobPositions()])
      .then(([r, d, u, j]) => {
        setRegionalOptions(r.map((x) => ({ value: x.id, label: x.name })));
        setDivisionOptions(d.map((x) => ({ value: x.id, label: x.name })));
        setUnitOptions(u.map((x) => ({ value: x.id, label: x.name })));
        setJobOptions(j.map((x) => ({ value: x.id, label: x.name })));
      })
      .catch(() => toast.error("❌ Gagal memuat filter master data"));
  }, []);

  // Load employees
  async function load() {
    setLoading(true);
    try {
      const params = {
        page: page - 1,
        size: rowsPerPage,
        search: search || null,
        regionalIds: regionalIds.map((i) => i.value),
        divisionIds: divisionIds.map((i) => i.value),
        unitIds: unitIds.map((i) => i.value),
        jobPositionIds: jobPositionIds.map((i) => i.value),
        statuses: statuses.map((i) => i.value),
      };

      const res = await fetchEmployees(params);
      setRows(res.content || []);
      setTotalPages(res.totalPages || 1);
      setTotalElements(res.totalElements || 0);
    } catch {
      toast.error("❌ Gagal memuat data pegawai");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, [page, rowsPerPage, search, regionalIds, divisionIds, unitIds, jobPositionIds, statuses]);

  // Delete employee
  async function onDelete(id) {
    if (!confirm("Hapus pegawai ini?")) return;
    try {
      await deleteEmployee(id);
      toast.success("✅ Pegawai berhasil dihapus");
      load();
    } catch (err) {
      toast.error(err?.response?.data?.message || "❌ Gagal menghapus pegawai");
    }
  }

  // Import excel
  async function handleImport(e) {
    const file = e.target.files?.[0];
    if (!file) return;
    const formData = new FormData();
    formData.append("file", file);
    try {
      await importEmployeesExcel(formData);
      toast.success("✅ Data pegawai berhasil diimport");
      load();
    } catch (err) {
      toast.error(err?.response?.data?.message || "❌ Gagal import pegawai");
    } finally {
      e.target.value = "";
    }
  }

  // Download template
  async function handleDownloadTemplate() {
    try {
      const blob = await downloadEmployeeTemplate();
      const url = window.URL.createObjectURL(new Blob([blob]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "employee_template.xlsx");
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      toast.success("✅ Template berhasil diunduh");
    } catch {
      toast.error("❌ Gagal download template");
    }
  }

  const startIdx = totalElements === 0 ? 0 : (page - 1) * rowsPerPage + 1;

  return (
    <div>
      {/* Toolbar */}
      <div className="mb-4 space-y-3">
        <div className="flex justify-between gap-3">
          <input
            type="text"
            className="input input-sm input-bordered w-md"
            placeholder="🔍 Cari NIP, Nama, Email..."
            value={search}
            onChange={(e) => {
              setPage(1);
              setSearch(e.target.value);
            }}
          />
          <div className="flex gap-2">
            <label className="btn btn-success btn-sm cursor-pointer">
              Import Excel
              <input type="file" accept=".xlsx,.xls" className="hidden" onChange={handleImport} />
            </label>
            <button className="btn btn-outline btn-sm" onClick={handleDownloadTemplate}>
              Download Template
            </button>
          </div>
        </div>
      </div>

      {/* Filters */}
      <div className="mb-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-3">
        <Select isMulti options={regionalOptions} value={regionalIds} onChange={setRegionalIds} placeholder="Filter Regional" />
        <Select isMulti options={divisionOptions} value={divisionIds} onChange={setDivisionIds} placeholder="Filter Division" />
        <Select isMulti options={unitOptions} value={unitIds} onChange={setUnitIds} placeholder="Filter Unit" />
        <Select isMulti options={jobOptions} value={jobPositionIds} onChange={setJobPositionIds} placeholder="Filter Jabatan" />
        <Select isMulti options={statusOptions} value={statuses} onChange={setStatuses} placeholder="Filter Status" />
      </div>

      {/* Table */}
      <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
        <table className="table table-zebra">
          <thead className="bg-base-200 text-xs">
            <tr>
              <th>No</th>
              <th>NIP</th>
              <th>Nama</th>
              <th>Email</th>
              <th>Gender</th>
              <th>Regional</th>
              <th>Division</th>
              <th>Unit</th>
              <th>Jabatan</th>
              <th>Status</th>
              <th>SK Efektif</th>
              <th>Aksi</th>
            </tr>
          </thead>
          <tbody className="text-xs">
            {loading ? (
              <tr>
                <td colSpan={12} className="text-center py-10">
                  <span className="loading loading-dots loading-md" />
                </td>
              </tr>
            ) : rows.length === 0 ? (
              <tr>
                <td colSpan={12} className="text-center text-gray-400 py-10">
                  Tidak ada data
                </td>
              </tr>
            ) : (
              rows.map((e, idx) => (
                <tr key={e.id}>
                  <td>{startIdx + idx}</td>
                  <td>{e.nip}</td>
                  <td>{e.name}</td>
                  <td>{e.email}</td>
                  <td>{e.gender}</td>
                  <td>{e.regionalName || "-"}</td>
                  <td>{e.divisionName || "-"}</td>
                  <td>{e.unitName || "-"}</td>
                  <td>{e.jobName || "-"}</td>
                  <td>{e.status}</td>
                  <td>{e.joinDate ? new Date(e.joinDate).toLocaleDateString("id-ID") : "-"}</td>
                  <td className="flex gap-2">
                    <button className="btn btn-xs btn-warning" onClick={() => setEditItem(e)}>
                      Edit
                    </button>
                    <button className="btn btn-xs btn-error" onClick={() => onDelete(e.id)}>
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
      <EditEmployeeModal open={!!editItem} initial={editItem} onClose={() => setEditItem(null)} onSaved={load} />
    </div>
  );
}