import { useEffect, useState, useMemo } from "react";
import toast from "react-hot-toast";
import { fetchUnits, toggleUnit } from "../../services/unitService";

export default function UnitPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);

  const [q, setQ] = useState("");
  const [page, setPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const apiParams = useMemo(
    () => ({
      q: q?.trim() || undefined,
      page: page - 1, // BE 0-based
      size: rowsPerPage,
    }),
    [q, page, rowsPerPage]
  );

  async function load() {
    setLoading(true);
    try {
      const data = await fetchUnits(apiParams);
      setRows(data.content || []);
      setTotalPages(Math.max(data.totalPages || 1, 1));
      setTotalElements(data.totalElements ?? (data.content?.length ?? 0));
    } catch {
      toast.error("❌ Gagal memuat unit");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiParams]);

  async function onToggle(id) {
    try {
      await toggleUnit(id);
      toast.success("✅ Status unit berhasil diperbarui");
      load();
    } catch (err) {
      const msg = err?.response?.data?.message || "❌ Gagal update status unit";
      toast.error(msg);
    }
  }

  // 🔹 Pagination info
  const startIdx = totalElements === 0 ? 0 : (page - 1) * rowsPerPage + 1;
  const endIdx = Math.min(page * rowsPerPage, totalElements);

  const handleChangePage = (newPage) => {
    if (newPage < 1 || newPage > totalPages) return;
    setPage(newPage);
  };

  return (
    <div>
      {/* 🔍 Toolbar */}
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
            placeholder="Cari Unit…"
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
              <option key={n} value={n}>{n}</option>
            ))}
          </select>
        </div>
      </div>

      {/* 📋 Tabel */}
      <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
        <table className="table text-xs">
          <thead className="bg-base-200">
            <tr>
              <th>No</th>
              <th>Nama Unit</th>
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
              rows.map((u, idx) => (
                <tr key={u.id}>
                  <td>{(page - 1) * rowsPerPage + idx + 1}</td>
                  <td>{u.name}</td>
                  <td>
                    {u.isActive ? (
                      <span className="badge badge-success text-xs">Aktif</span>
                    ) : (
                      <span className="badge badge-warning text-xs">Tidak Aktif</span>
                    )}
                  </td>
                  <td>{new Date(u.createdAt).toLocaleDateString("id-ID")}</td>
                  <td>
                    <button
                      className={`btn btn-xs ${
                        u.isActive
                          ? "btn-warning btn-soft border-warning" // tombol kuning buat Nonaktifkan
                          : "btn-success btn-soft border-success" // tombol hijau buat Aktifkan
                      }`}
                      onClick={() => onToggle(u.id)}
                    >
                      {u.isActive ? "Nonaktifkan" : "Aktifkan"}
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* 📄 Pagination */}
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
              if (start > 2) {
                pages.push(
                  <span key="start-ellipsis" className="btn btn-sm btn-disabled">
                    …
                  </span>
                );
              }
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
              if (end < totalPages - 1) {
                pages.push(
                  <span key="end-ellipsis" className="btn btn-sm btn-disabled">
                    …
                  </span>
                );
              }
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
    </div>
  );
}