import { useState } from "react";
import toast from "react-hot-toast";
import AsyncSelect from "react-select/async";
import { addEmployeeToBatch, fetchEligibleEmployees } from "../../services/employeeBatchService";

export default function AddEmployeeBatchModal({ open, onClose, batchId, onSaved }) {
  const [employee, setEmployee] = useState(null);

  // 🔹 Reset & Close helper
  function handleClose() {
    setEmployee(null); // reset nama tiap kali modal ditutup
    onClose();
  }

  // Async load eligible employees
  const loadEligible = async (inputValue) => {
    try {
      const data = await fetchEligibleEmployees(batchId);
      return data
        .filter((e) =>
          !inputValue
            ? true
            : `${e.nip} ${e.employeeName}`.toLowerCase().includes(inputValue.toLowerCase())
        )
        .map((e) => ({
          value: e.employeeId,
          label: `${e.nip} - ${e.employeeName}`,
        }));
    } catch {
      return [];
    }
  };

  async function handleSave() {
    if (!employee) return;
    try {
      await addEmployeeToBatch(batchId, employee.value);
      toast.success("Peserta berhasil ditambahkan");
      onSaved?.();
      handleClose(); // 🔹 close sekalian reset
    } catch (err) {
      toast.error(err?.response?.data?.message || "Gagal menambahkan peserta");
    }
  }

  if (!open) return null;

  return (
    <dialog className="modal" open={open}>
      <div className="modal-box w-11/12 max-w-3xl">
        <h3 className="font-bold text-lg mb-4">Tambah Peserta</h3>
        <div className="space-y-3">
          <label className="block text-sm">Pilih Pegawai Eligible</label>
          <AsyncSelect
            cacheOptions
            defaultOptions
            loadOptions={loadEligible}
            value={employee}
            onChange={setEmployee}
            placeholder="Cari pegawai eligible..."
            isClearable
            menuPortalTarget={document.body}
            styles={{
              menuPortal: (base) => ({ ...base, zIndex: 9999 }),
              control: (base) => ({
                ...base,
                minHeight: 45,
                fontSize: "0.875rem",
              }),
              menu: (base) => ({
                ...base,
                fontSize: "0.875rem",
              }),
            }}
          />
        </div>
        <div className="modal-action">
          <button className="btn" onClick={handleClose}>
            Batal
          </button>
          <button className="btn btn-primary" onClick={handleSave}>
            Simpan
          </button>
        </div>
      </div>
      <form method="dialog" className="modal-backdrop">
        <button onClick={handleClose}>close</button>
      </form>
    </dialog>
  );
}
