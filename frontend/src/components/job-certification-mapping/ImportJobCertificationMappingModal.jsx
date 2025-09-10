import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import {
  dryRunJobCertImport,
  confirmJobCertImport,
} from "../../services/jobCertificationImportService";

export default function ImportJobCertificationMappingModal({ open, onClose, onImported }) {
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [loading, setLoading] = useState(false);

  // 🔹 Reset tiap kali modal tutup
  useEffect(() => {
    if (!open) {
      setFile(null);
      setPreview(null);
      setLoading(false);
    }
  }, [open]);

  async function handleDryRun() {
    if (!file) {
      toast.error("⚠️ Pilih file Excel dulu");
      return;
    }
    setLoading(true);
    try {
      const res = await dryRunJobCertImport(file);
      setPreview(res);
      toast.success("✅ File berhasil dicek");
    } catch {
      toast.error("❌ Gagal memeriksa file");
    } finally {
      setLoading(false);
    }
  }

  async function handleConfirm() {
    if (!file) return;
    setLoading(true);
    try {
      const res = await confirmJobCertImport(file);
      toast.success(res.message || "✅ Data berhasil diimport");
      onImported?.();
      onClose();
    } catch {
      toast.error("❌ Import gagal, coba cek ulang file");
    } finally {
      setLoading(false);
    }
  }

  if (!open) return null;

  return (
    <dialog className="modal modal-open">
      <div className="modal-box max-w-2xl">
        <h3 className="font-bold text-lg">Import Data Mapping Jabatan ↔ Sertifikasi</h3>
        <p className="text-sm text-gray-600 mb-4">
          Upload file Excel untuk menambahkan atau memperbarui mapping jabatan dengan sertifikasi.
        </p>

        {/* File Input */}
        <input
          type="file"
          accept=".xlsx"
          className="file-input file-input-bordered w-full my-4"
          onChange={(e) => setFile(e.target.files[0])}
        />

        {/* Info File */}
        {file && (
          <p className="text-xs text-gray-500 mb-2">
            File dipilih: <span className="font-medium">{file.name}</span>
          </p>
        )}

        {/* Preview Result */}
        {preview && (
          <div className="mt-3 p-3 border rounded bg-base-200 text-sm space-y-1">
            <p className="font-semibold mb-2">Hasil Pengecekan File:</p>
            <ul className="list-disc list-inside space-y-1">
              <li>Total baris dibaca: <b>{preview.processed}</b></li>
              <li>Data baru yang akan ditambahkan: <b>{preview.inserted}</b></li>
              <li>Data lama yang akan diaktifkan kembali: <b>{preview.reactivated}</b></li>
              <li>Data yang sudah ada (tidak berubah): <b>{preview.skipped}</b></li>
              <li>Baris dengan error: <b>{preview.errors}</b></li>
            </ul>

            {preview.errorDetails?.length > 0 && (
              <details className="mt-3">
                <summary className="cursor-pointer text-red-600 font-medium">
                  ⚠️ Lihat daftar error
                </summary>
                <ul className="list-disc list-inside text-red-600 mt-1">
                  {preview.errorDetails.map((err, idx) => (
                    <li key={idx}>{err}</li>
                  ))}
                </ul>
              </details>
            )}
          </div>
        )}

        {/* Actions */}
        <div className="modal-action">
          <button
            className="btn"
            onClick={() => {
              setFile(null);
              setPreview(null);
              onClose();
            }}
            disabled={loading}
          >
            Batal
          </button>
          {!preview ? (
            <button
              className="btn btn-info"
              onClick={handleDryRun}
              disabled={loading || !file}
            >
              {loading ? <span className="loading loading-spinner" /> : "Cek File Dulu"}
            </button>
          ) : (
            <button
              className="btn btn-success"
              onClick={handleConfirm}
              disabled={loading || !file}
            >
              {loading ? <span className="loading loading-spinner" /> : "Lanjutkan Import"}
            </button>
          )}
        </div>
      </div>
    </dialog>
  );
}
