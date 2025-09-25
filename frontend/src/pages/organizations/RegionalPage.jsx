import { useEffect, useState, useMemo } from "react";
import toast from "react-hot-toast";
import { fetchRegionals, toggleRegional } from "../../services/regionalService";
import Pagination from "../../components/common/Pagination";

export default function RegionalPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);

  const [q, setQ] = useState("");
  const [page, setPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const apiParams = useMemo(
    () => ({ q: q?.trim() || undefined, page: page - 1, size: rowsPerPage }),
    [q, page, rowsPerPage]
  );

  async function load() {
    setLoading(true);
    try {
      const data = await fetchRegionals(apiParams);
      setRows(data.content || []);
      setTotalPages(Math.max(data.totalPages || 1, 1));
      setTotalElements(data.totalElements ?? (data.content?.length ?? 0));
    } catch {
      toast.error("❌ Gagal memuat regional");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, [apiParams]);

  async function onToggle(id) {
    try {
      await toggleRegional(id);
      toast.success("✅ Status regional berhasil diperbarui");
      load();
    } catch (err) {
      toast.error(err?.response?.data?.message || "❌ Gagal update status regional");
    }
  }

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
            onChange={(e) => { setPage(1); setQ(e.target.value); }}
            placeholder="Cari Regional..."
          />
        </div>
      </div>

      {/* Table */}
      <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
        <table className="table table-zebra text-xs">
          <thead className="text-xs bg-base-200">
            <tr>
              <th>No</th>
              <th>Nama Regional</th>
              <th>Status</th>
              <th>Dibuat</th>
              <th>Aksi</th>
            </tr>
          </thead>
          <tbody className="text-xs">
            {loading ? (
              <tr>
                <td colSpan={5} className="text-center py-10">
                  <span className="loading loading-dots loading-md" />
                </td>
              </tr>
            ) : rows.length === 0 ? (
              <tr>
                <td colSpan={5} className="text-center text-gray-400 py-10">
                  Tidak ada data
                </td>
              </tr>
            ) : (
              rows.map((r, idx) => (
                <tr key={r.id}>
                  <td>{startIdx + idx}</td>
                  <td>{r.name}</td>
                  <td>
                    {r.isActive ? (
                      <span className="badge badge-sm badge-success">Aktif</span>
                    ) : (
                      <span className="badge badge-sm badge-warning">Tidak Aktif</span>
                    )}
                  </td>
                  <td>{new Date(r.createdAt).toLocaleDateString("id-ID")}</td>
                  <td>
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
    </div>
  );
}