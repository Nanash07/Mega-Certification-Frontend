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
    <div className="flex items-center justify-between mt-2 px-3 py-2 text-sm">
      {/* Rows per page */}
      <div className="grid grid-cols-2 items-center gap-2">
        Rows per page
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

      {/* Info & controls */}
      <div className="flex items-center gap-4">
        <span>
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