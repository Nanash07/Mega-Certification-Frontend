import { useEffect, useMemo, useState } from "react";
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
  // filters & pagination
  const [q, setQ] = useState("");
  const [page, setPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const [regionalIds, setRegionalIds] = useState([]);
  const [divisionIds, setDivisionIds] = useState([]);
  const [unitIds, setUnitIds] = useState([]);
  const [jobPositionIds, setJobPositionIds] = useState([]);

  // master data
  const [regionals, setRegionals] = useState([]);
  const [divisions, setDivisions] = useState([]);
  const [units, setUnits] = useState([]);
  const [jobPositions, setJobPositions] = useState([]);

  // data
  const [rows, setRows] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);

  // modals
  const [openCreate, setOpenCreate] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [confirm, setConfirm] = useState({ open: false, id: undefined });

  // load filters (langsung semua, ga cascading)
  useEffect(() => {
    Promise.all([fetchRegionals(), fetchDivisions(), fetchUnits(), fetchJobPositions()])
      .then(([r, d, u, j]) => {
        console.log("Regionals:", r);
        console.log("Divisions:", d);
        console.log("Units:", u);
        console.log("JobPositions:", j);

        setRegionals(r);
        setDivisions(d);
        setUnits(u);
        setJobPositions(j);
      })
      .catch(() => toast.error("❌ Gagal memuat filter master data"));
  }, []);

  // param API
  const apiParams = useMemo(
    () => ({
      q: q?.trim() || undefined,
      regionalIds: regionalIds.map((r) => r.value),
      divisionIds: divisionIds.map((d) => d.value),
      unitIds: unitIds.map((u) => u.value),
      jobPositionIds: jobPositionIds.map((j) => j.value),
      page: page - 1,
      size: rowsPerPage,
    }),
    [q, regionalIds, divisionIds, unitIds, jobPositionIds, page, rowsPerPage]
  );

  // load data
  async function load() {
    setLoading(true);
    try {
      const data = await fetchEmployees(apiParams);
      setRows(data.content || []);
      setTotalPages(Math.max(data.totalPages || 1, 1));
      setTotalElements(data.totalElements ?? 0);
    } catch (e) {
      toast.error(e?.response?.data?.message ?? "❌ Gagal memuat data pegawai");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiParams]);

  // delete
  async function onDelete(id) {
    try {
      await deleteEmployee(id);
      toast.success("✅ Pegawai berhasil dihapus");
      load();
    } catch (err) {
      toast.error(err?.response?.data?.message || "❌ Gagal menghapus pegawai");
    }
  }

  // import excel
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

  // download template
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
    } catch (err) {
      toast.error("❌ Gagal download template");
    }
  }

  // helper react-select
  const toOptions = (list, labelKey = "name") =>
    Array.isArray(list) ? list.map((i) => ({ value: i.id, label: i[labelKey] })) : [];

  const startIdx = totalElements === 0 ? 0 : (page - 1) * rowsPerPage + 1;

  return (
    <div>
      {/* Toolbar */}
      <div className="mb-4 flex flex-col gap-3 md:flex-row md:items-end md:gap-6">
        <div className="flex-1 min-w-[16rem]">
          <label className="label pb-1 font-semibold">Search</label>
          <input
            className="input input-bordered w-full"
            value={q}
            onChange={(e) => {
              setPage(1);
              setQ(e.target.value);
            }}
            placeholder="Cari NIP / Nama / Email…"
          />
        </div>

        <div className="md:ml-auto flex gap-2">
          <label className="btn btn-success cursor-pointer">
            Import Excel
            <input type="file" accept=".xlsx,.xls" className="hidden" onChange={handleImport} />
          </label>
          <button className="btn btn-outline" onClick={handleDownloadTemplate}>
            Download Template
          </button>
        </div>
      </div>

      {/* Filter */}
      <div className="mb-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-3">
        <Select isMulti options={toOptions(regionals)} value={regionalIds} onChange={setRegionalIds} placeholder="Filter Regional" />
        <Select isMulti options={toOptions(divisions)} value={divisionIds} onChange={setDivisionIds} placeholder="Filter Division" />
        <Select isMulti options={toOptions(units)} value={unitIds} onChange={setUnitIds} placeholder="Filter Unit" />
        <Select isMulti options={toOptions(jobPositions)} value={jobPositionIds} onChange={setJobPositionIds} placeholder="Filter Jabatan" />
      </div>

      {/* Table */}
      <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
        <table className="table">
          <thead className="bg-base-200 text-xs">
            <tr>
              <th>No</th>
              <th>NIP</th>
              <th>Nama</th>
              <th>Email</th>
              <th>Gender</th>
              <th>Jabatan</th>
              <th>Status</th>
              <th>SK Efektif</th>
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
              rows.map((e, idx) => (
                <tr key={e.id}>
                  <td>{startIdx + idx}</td>
                  <td>{e.nip}</td>
                  <td>{e.name}</td>
                  <td>{e.email}</td>
                  <td>{e.gender}</td>
                  <td>{e.jobName || "-"}</td>
                  <td>{e.status}</td>
                  <td>{e.joinDate ? new Date(e.joinDate).toLocaleDateString("id-ID") : "-"}</td>
                  <td>
                    <div className="flex gap-1">
                      <button className="btn btn-xs btn-outline btn-warning" onClick={() => setEditItem(e)}>
                        Edit
                      </button>
                      <button
                        className="btn btn-xs btn-outline btn-error"
                        onClick={() => setConfirm({ open: true, id: e.id })}
                      >
                        Delete
                      </button>
                    </div>
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
      <CreateEmployeeModal open={openCreate} onClose={() => setOpenCreate(false)} onSaved={load} />
      <EditEmployeeModal open={!!editItem} initial={editItem} onClose={() => setEditItem(null)} onSaved={load} />

      {/* Confirm Delete */}
      {confirm.open && (
        <dialog open className="modal">
          <div className="modal-box">
            <h3 className="font-bold text-lg">Hapus Pegawai?</h3>
            <p>Data pegawai akan dipindahkan ke arsip (soft delete).</p>
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
      )}
    </div>
  );
}