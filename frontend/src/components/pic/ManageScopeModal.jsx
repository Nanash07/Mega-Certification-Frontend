import { useEffect, useState } from "react";
import Select from "react-select";
import toast from "react-hot-toast";
import { fetchCertifications } from "../../services/certificationService";
import { assignPicScope } from "../../services/picScopeService";

export default function ManageScopeModal({ open, onClose, user, onSaved }) {
  const [options, setOptions] = useState([]);
  const [selected, setSelected] = useState([]);
  const [loading, setLoading] = useState(false);

  // 🔹 Custom style react-select
  const customStyles = {
    control: (provided) => ({
      ...provided,
      minHeight: "48px",
      fontSize: "1rem",
      zIndex: 50,
    }),
    menuPortal: (base) => ({
      ...base,
      zIndex: 9999, // ✅ biar di atas modal
    }),
    option: (provided, state) => ({
      ...provided,
      padding: "12px 16px",
      fontSize: "1rem",
      backgroundColor: state.isFocused ? "#e5e7eb" : "white",
      color: "#111827",
    }),
    multiValue: (provided) => ({
      ...provided,
      backgroundColor: "#dbeafe",
    }),
    multiValueLabel: (provided) => ({
      ...provided,
      fontSize: "0.9rem",
      color: "#1e3a8a",
    }),
  };

  // 🔹 Load certifications & preselect current scope
  useEffect(() => {
    if (!open || !user) return;

    async function loadData() {
      try {
        const certs = await fetchCertifications();
        const opts = certs.map((c) => ({
          value: c.id,
          label: `${c.code}`,
        }));
        setOptions(opts);

        const current = user.certifications?.map((c) => ({
          value: c.certificationId,
          label: c.certificationCode,
        })) || [];
        setSelected(current);
      } catch (e) {
        console.error("❌ Gagal load certifications:", e);
        toast.error("Gagal memuat certifications");
      }
    }

    loadData();
  }, [open, user]);

  // 🔹 Save scope
  async function handleSave() {
    if (!user) return;
    setLoading(true);
    try {
      const ids = selected.map((s) => s.value);
      await assignPicScope(user.userId, ids);
      toast.success("Scope PIC berhasil disimpan");
      onSaved?.();
      onClose();
    } catch (e) {
      console.error("❌ Gagal simpan scope PIC:", e);
      toast.error("Gagal menyimpan scope PIC");
    } finally {
      setLoading(false);
    }
  }

  if (!open || !user) return null;

  return (
    <dialog open={open} className="modal modal-open">
      <div className="modal-box max-w-2xl">
        <h3 className="font-bold text-xl mb-4">
          Kelola Scope untuk <span className="text-primary">{user.username}</span>
        </h3>

        <div className="mb-6">
          <Select
            isMulti
            options={options}
            value={selected}
            onChange={setSelected}
            className="text-base"
            styles={customStyles}
            placeholder="Pilih certifications..."
            menuPortalTarget={document.body} // ✅ dropdown tembus modal
          />
        </div>

        <div className="modal-action">
          <button className="btn" onClick={onClose} disabled={loading}>
            Batal
          </button>
          <button
            className="btn btn-primary"
            onClick={handleSave}
            disabled={loading}
          >
            {loading ? <span className="loading loading-spinner" /> : "Simpan"}
          </button>
        </div>
      </div>
    </dialog>
  );
}
