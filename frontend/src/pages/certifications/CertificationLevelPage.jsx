// src/pages/certifications/CertificationLevelPage.jsx
import { useEffect, useMemo, useState } from "react";
import toast from "react-hot-toast";
import {
  fetchCertificationLevels,
  deleteCertificationLevel,
} from "../../services/certificationLevelService";
import CreateCertificationLevelModal from "../../components/certification-levels/CreateCertificationLevelModal";
import EditCertificationLevelModal from "../../components/certification-levels/EditCertificationLevelModal";

export default function CertificationLevelPage() {
  const [q, setQ] = useState("");
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);

  const [openCreate, setOpenCreate] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [confirm, setConfirm] = useState({ open: false, id: undefined, name: "" });

  async function load() {
    setLoading(true);
    try {
      const levelList = await fetchCertificationLevels();
      levelList.sort((a, b) => (a.level ?? 0) - (b.level ?? 0));
      setRows(levelList);
    } catch (e) {
      console.error(e);
      toast.error(e?.response?.data?.message || "Gagal memuat data jenjang");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  const filtered = useMemo(() => {
    const s = q.trim().toLowerCase();
    if (!s) return rows;
    return rows.filter(
      (r) =>
        String(r.name || "").toLowerCase().includes(s) ||
        String(r.level || "").toLowerCase().includes(s)
    );
  }, [q, rows]);

  async function onDelete(id) {
    try {
      await deleteCertificationLevel(id);
      toast.success("Jenjang sertifikasi dihapus");
      load();
    } catch (e) {
      toast.error(e?.response?.data?.message || "Gagal menghapus");
    }
  }

  return (
    <div>
      {/* Header + actions */}
      <div className="mb-4 flex">
        <button
          className="btn btn-primary w-full md:w-auto"
          onClick={() => setOpenCreate(true)}
        >
          + Tambah Jenjang
        </button>
      </div>

      {/* Table */}
      <div className="overflow-x-auto rounded-xl border border-gray-200 shadow bg-base-100">
        <table className="table table-zebra">
          <thead className="bg-base-200 text-xs">
            <tr>
              <th>No.</th>
              <th>Level</th>
              <th>Nama Jenjang</th>
              <th>Aksi</th>
            </tr>
          </thead>
          <tbody className="text-xs">
            {loading ? (
              <tr>
                <td colSpan={4} className="py-10 text-center">
                  <span className="loading loading-dots loading-md" />
                </td>
              </tr>
            ) : filtered.length === 0 ? (
              <tr>
                <td colSpan={4} className="text-center text-gray-400 py-10">
                  Tidak ada data
                </td>
              </tr>
            ) : (
              filtered.map((row, idx) => (
                <tr key={row.id}>
                  <td>{idx + 1}</td>
                  <td>{row.level}</td>
                  <td>{row.name}</td>
                  <td className="space-x-2">
                    <button
                      className="btn btn-xs btn-soft btn-warning border-warning"
                      onClick={() => setEditItem(row)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-xs btn-soft btn-error border-error"
                      onClick={() =>
                        setConfirm({
                          open: true,
                          id: row.id,
                          name: `${row.name} (Level ${row.level})`,
                        })
                      }
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
      <CreateCertificationLevelModal
        open={openCreate}
        onClose={() => setOpenCreate(false)}
        onSaved={() => {
          setOpenCreate(false);
          load();
        }}
      />
      <EditCertificationLevelModal
        open={!!editItem}
        initial={editItem}
        onClose={() => setEditItem(null)}
        onSaved={() => {
          setEditItem(null);
          load();
        }}
      />

      {/* Confirm delete */}
      <dialog className="modal" open={confirm.open}>
        <div className="modal-box">
          <h3 className="font-bold text-lg">Hapus Jenjang Sertifikasi?</h3>
          <p className="py-3">
            Yakin mau hapus <b>{confirm.name}</b>?
          </p>
          <div className="modal-action">
            <button
              className="btn"
              onClick={() => setConfirm({ open: false })}
            >
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
        <form
          method="dialog"
          className="modal-backdrop"
          onSubmit={() => setConfirm({ open: false })}
        >
          <button>close</button>
        </form>
      </dialog>
    </div>
  );
}