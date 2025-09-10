import { useEffect, useState, useMemo } from "react";
import toast from "react-hot-toast";
import {
  fetchExceptions,
  deleteException,
} from "../../services/employeeCertificationExceptionService";
import CreateExceptionModal from "../../components/exceptions/CreateExceptionModal";
import EditExceptionModal from "../../components/exceptions/EditExceptionModal";

export default function EmployeeCertificationExceptionPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [openCreate, setOpenCreate] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [confirm, setConfirm] = useState({ open: false, id: undefined });

  async function load() {
    setLoading(true);
    try {
      const list = await fetchExceptions();
      setRows(list);
    } catch {
      toast.error("Gagal memuat data manual requirement");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function onDelete(id) {
    try {
      await deleteException(id);
      toast.success("Data dihapus");
      load();
    } catch {
      toast.error("Gagal menghapus data");
    }
  }

  const filtered = useMemo(() => rows, [rows]);

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-xl font-bold">Pegawai Eligible Manual</h1>
        <button className="btn btn-primary" onClick={() => setOpenCreate(true)}>
          + Tambah Kewajiban
        </button>
      </div>

      <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
        <table className="table table-zebra">
          <thead className="bg-base-200">
            <tr>
              <th>No</th>
              <th>NIP</th>
              <th>Nama</th>
              <th>Jabatan</th>
              <th>Sertifikasi</th>
              <th>Jenjang</th>
              <th>Sub Bidang</th>
              <th>Reason</th>
              <th>Aksi</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={9} className="text-center py-10">
                  <span className="loading loading-dots loading-md" />
                </td>
              </tr>
            ) : filtered.length === 0 ? (
              <tr>
                <td colSpan={9} className="text-center text-gray-400 py-10">
                  Tidak ada data
                </td>
              </tr>
            ) : (
              filtered.map((r, idx) => (
                <tr key={r.id}>
                  <td>{idx + 1}</td>
                  <td>{r.nip}</td>
                  <td>{r.employeeName}</td>
                  <td>{r.jobPositionTitle}</td>
                  <td>{r.certificationCode}</td>
                  <td>{r.certificationLevel}</td>
                  <td>{r.subFieldCode}</td>
                  <td>{r.reason || "-"}</td>
                  <td className="space-x-2">
                    <button
                      className="btn btn-sm btn-soft btn-warning border-warning"
                      onClick={() => setEditItem(r)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-sm btn-soft btn-error border-error"
                      onClick={() => setConfirm({ open: true, id: r.id })}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Modals */}
      <CreateExceptionModal
        open={openCreate}
        onClose={() => setOpenCreate(false)}
        onSaved={() => {
          setOpenCreate(false);
          load();
        }}
      />
      <EditJobCertificationMappingModal
        open={!!editItem}
        id={editItem?.id}
        onClose={() => setEditItem(null)}
        onSaved={() => {
          setEditItem(null);
          load();
        }}
      />

      {/* Confirm Delete */}
      <dialog className="modal" open={confirm.open}>
        <div className="modal-box">
          <h3 className="font-bold text-lg">Hapus Data?</h3>
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
    </div>
  );
}