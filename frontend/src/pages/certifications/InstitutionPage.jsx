import { useEffect, useState, useMemo } from "react";
import toast from "react-hot-toast";
import {
  fetchInstitutions,
  deleteInstitution,
} from "../../services/institutionService";
import CreateInstitutionModal from "../../components/institutions/CreateInstitutionModal";
import EditInstitutionModal from "../../components/institutions/EditInstitutionModal";

export default function InstitutionPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [openCreate, setOpenCreate] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [confirm, setConfirm] = useState({ open: false, id: undefined });

  async function load() {
    setLoading(true);
    try {
      const list = await fetchInstitutions();
      setRows(list);
    } catch {
      toast.error("Gagal memuat data institution");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function onDelete(id) {
    try {
      await deleteInstitution(id);
      toast.success("Institution dihapus");
      load();
    } catch {
      toast.error("Gagal menghapus institution");
    }
  }

  const filtered = useMemo(() => rows, [rows]);

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <button className="btn btn-primary" onClick={() => setOpenCreate(true)}>
          + Tambah Institution
        </button>
      </div>

      <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
        <table className="table table-zebra">
          <thead className="bg-base-200 text-xs">
            <tr>
              <th>No</th>
              <th>Nama</th>
              <th>Type</th>
              <th>Alamat</th>
              <th>Contact Person</th>
              <th>Aksi</th>
            </tr>
          </thead>
          <tbody className="text-xs">
            {loading ? (
              <tr>
                <td colSpan={6} className="text-center py-10">
                  <span className="loading loading-dots loading-md" />
                </td>
              </tr>
            ) : filtered.length === 0 ? (
              <tr>
                <td colSpan={6} className="text-center text-gray-400 py-10">
                  Tidak ada data
                </td>
              </tr>
            ) : (
              filtered.map((r, idx) => (
                <tr key={r.id}>
                  <td>{idx + 1}</td>
                  <td>{r.name}</td>
                  <td>{r.type}</td>
                  <td>{r.address}</td>
                  <td>{r.contactPerson}</td>
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
      <CreateInstitutionModal
        open={openCreate}
        onClose={() => setOpenCreate(false)}
        onSaved={() => {
          setOpenCreate(false);
          load();
        }}
      />
      <EditInstitutionModal
        open={!!editItem}
        initial={editItem}
        onClose={() => setEditItem(null)}
        onSaved={() => {
          setEditItem(null);
          load();
        }}
      />

      {/* Confirm Delete */}
      <dialog className="modal" open={confirm.open}>
        <div className="modal-box">
          <h3 className="font-bold text-lg">Hapus Institution?</h3>
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