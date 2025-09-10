import { useEffect, useMemo, useState } from "react";
import toast from "react-hot-toast";
import Select from "react-select";
import {
  fetchEmployees,
  deleteEmployee,
  importEmployeesExcel,
  downloadEmployeeTemplate,
} from "../../services/employeeService";
import { fetchRegionals } from "../../services/regionalService";
import { fetchDivisions } from "../../services/divisionService";
import { fetchUnits } from "../../services/unitService";
import { fetchJobPositions } from "../../services/jobPositionService";
import CreateEmployeeModal from "../../components/employees/CreateEmployeeModal";
import EditEmployeeModal from "../../components/employees/EditEmployeeModal";

export default function EmployeePage() {
  // filters
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

  // load regionals awal
  useEffect(() => {
    fetchRegionals()
      .then(setRegionals)
      .catch(() => toast.error("❌ Gagal memuat regional"));
  }, []);

  // cascading filters
  useEffect(() => {
    if (regionalIds.length > 0) {
      fetchDivisions(regionalIds.map((r) => r.value))
        .then(setDivisions)
        .catch(() => toast.error("❌ Gagal memuat division"));
    } else {
      setDivisions([]);
      setDivisionIds([]);
    }
    setUnits([]);
    setUnitIds([]);
    setJobPositions([]);
    setJobPositionIds([]);
  }, [regionalIds]);

  useEffect(() => {
    if (divisionIds.length > 0) {
      fetchUnits(divisionIds.map((d) => d.value))
        .then(setUnits)
        .catch(() => toast.error("❌ Gagal memuat unit"));
    } else {
      setUnits([]);
      setUnitIds([]);
    }
    setJobPositions([]);
    setJobPositionIds([]);
  }, [divisionIds]);

  useEffect(() => {
    if (unitIds.length > 0) {
      fetchJobPositions(unitIds.map((u) => u.value))
        .then(setJobPositions)
        .catch(() => toast.error("❌ Gagal memuat job position"));
    } else {
      setJobPositions([]);
      setJobPositionIds([]);
    }
  }, [unitIds]);

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
      setTotalElements(data.totalElements ?? (data.content?.length ?? 0));
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
      toast.error(err?.response?.data?.message || "❌ Gagal download template");
    }
  }

  // pagination info
  const startIdx = totalElements === 0 ? 0 : (page - 1) * rowsPerPage + 1;
  const endIdx = Math.min(page * rowsPerPage, totalElements);
  const handleChangePage = (newPage) => {
    if (newPage < 1 || newPage > totalPages) return;
    setPage(newPage);
  };

  // helper react-select
  const toOptions = (list, labelKey = "name") =>
    Array.isArray(list) ? list.map((i) => ({ value: i.id, label: i[labelKey] })) : [];

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
        <div>
          <label className="label pb-1 font-semibold">Regional</label>
          <Select
            isMulti
            options={toOptions(regionals)}
            value={regionalIds}
            onChange={(v) => {
              setRegionalIds(v);
              setPage(1);
            }}
            placeholder="Pilih Regional..."
          />
        </div>
        <div>
          <label className="label pb-1 font-semibold">Division</label>
          <Select
            isMulti
            options={toOptions(divisions)}
            value={divisionIds}
            onChange={(v) => {
              setDivisionIds(v);
              setPage(1);
            }}
            placeholder="Pilih Division..."
          />
        </div>
        <div>
          <label className="label pb-1 font-semibold">Unit</label>
          <Select
            isMulti
            options={toOptions(units)}
            value={unitIds}
            onChange={(v) => {
              setUnitIds(v);
              setPage(1);
            }}
            placeholder="Pilih Unit..."
          />
        </div>
        <div>
          <label className="label pb-1 font-semibold">Job Position</label>
          <Select
            isMulti
            options={toOptions(jobPositions, "name")}
            value={jobPositionIds}
            onChange={(v) => {
              setJobPositionIds(v);
              setPage(1);
            }}
            placeholder="Pilih Jabatan..."
          />
        </div>
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
              <th>Jabatan</th>
              <th>Unit</th>
              <th>Divisi</th>
              <th>Regional</th>
              <th>SK Efektif</th>
              <th>Dibuat</th>
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
              rows.map((e, idx) => (
                <tr key={e.id}>
                  <td>{(page - 1) * rowsPerPage + idx + 1}</td>
                  <td>{e.nip}</td>
                  <td>{e.name}</td>
                  <td>{e.email}</td>
                  <td>{e.jobName || "-"}</td>
                  <td>{e.unitName || "-"}</td>
                  <td>{e.divisionName || "-"}</td>
                  <td>{e.regionalName || "-"}</td>
                  <td>{e.joinDate ? new Date(e.joinDate).toLocaleDateString("id-ID") : "-"}</td>
                  <td>{e.createdAt ? new Date(e.createdAt).toLocaleDateString("id-ID") : "-"}</td>
                  <td>
                    <div className="flex gap-1">
                      <button
                        className="btn btn-xs btn-outline btn-warning"
                        onClick={() => setEditItem(e)}
                      >
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
          {(() => {
            const pages = [];
            const maxPagesToShow = 5;
            let start = Math.max(1, page - Math.floor(maxPagesToShow / 2));
            let end = start + maxPagesToShow - 1;

            if (end > totalPages) {
              end = totalPages;
              start = Math.max(1, end - maxPagesToShow + 1);
            }

            if (start > 1) {
              pages.push(
                <button
                  key={1}
                  className="btn btn-sm btn-ghost"
                  onClick={() => handleChangePage(1)}
                >
                  1
                </button>
              );
              if (start > 2)
                pages.push(
                  <span key="start-ellipsis" className="btn btn-sm btn-disabled">
                    …
                  </span>
                );
            }

            for (let i = start; i <= end; i++) {
              pages.push(
                <button
                  key={i}
                  className={`btn btn-sm ${page === i ? "btn-primary" : "btn-ghost"}`}
                  onClick={() => handleChangePage(i)}
                >
                  {i}
                </button>
              );
            }

            if (end < totalPages) {
              if (end < totalPages - 1)
                pages.push(
                  <span key="end-ellipsis" className="btn btn-sm btn-disabled">
                    …
                  </span>
                );
              pages.push(
                <button
                  key={totalPages}
                  className="btn btn-sm btn-ghost"
                  onClick={() => handleChangePage(totalPages)}
                >
                  {totalPages}
                </button>
              );
            }

            return pages;
          })()}
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
      <CreateEmployeeModal
        open={openCreate}
        onClose={() => setOpenCreate(false)}
        onSaved={load}
      />
      <EditEmployeeModal
        open={!!editItem}
        initial={editItem}
        onClose={() => setEditItem(null)}
        onSaved={load}
      />

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