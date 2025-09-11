import {
  ChevronLeft,
  ChevronRight,
  ChevronsLeft,
  ChevronsRight,
} from "lucide-react";

export default function Pagination({
  page,
  totalPages,
  totalElements,
  rowsPerPage,
  onPageChange,
  onRowsPerPageChange,
}) {
  const startIdx = totalElements === 0 ? 0 : (page - 1) * rowsPerPage + 1;
  const endIdx = Math.min(page * rowsPerPage, totalElements);

  return (
    <div className="flex flex-wrap items-center justify-between gap-3 mt-3 px-3 py-2 text-xs">
      {/* Rows per page → selalu tampil di semua screen */}
      <div className="flex items-center gap-2">
        <span className="whitespace-nowrap">Rows per page</span>
        <select
          className="select select-sm select-bordered"
          value={rowsPerPage}
          onChange={(e) => onRowsPerPageChange(Number(e.target.value))}
        >
          {[5, 10, 20, 25, 50, 100].map((n) => (
            <option key={n} value={n}>
              {n}
            </option>
          ))}
        </select>
      </div>

      {/* Info + controls */}
      <div className="flex items-center gap-4 flex-1 justify-between md:justify-end">
        <span className="text-gray-500">
          {startIdx} - {endIdx} of {totalElements}
        </span>

        <div className="flex items-center gap-1">
          <button
            className="btn btn-xs btn-ghost"
            onClick={() => onPageChange(1)}
            disabled={page === 1}
          >
            <ChevronsLeft size={16} />
          </button>
          <button
            className="btn btn-xs btn-ghost"
            onClick={() => onPageChange(page - 1)}
            disabled={page === 1}
          >
            <ChevronLeft size={16} />
          </button>
          <button
            className="btn btn-xs btn-ghost"
            onClick={() => onPageChange(page + 1)}
            disabled={page === totalPages}
          >
            <ChevronRight size={16} />
          </button>
          <button
            className="btn btn-xs btn-ghost"
            onClick={() => onPageChange(totalPages)}
            disabled={page === totalPages}
          >
            <ChevronsRight size={16} />
          </button>
        </div>
      </div>
    </div>
  );
}