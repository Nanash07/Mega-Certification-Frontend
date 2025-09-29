import { useState } from "react";
import toast from "react-hot-toast";
import AsyncSelect from "react-select/async";
import { addEmployeesToBatchBulk, fetchEligibleEmployees } from "../../services/employeeBatchService";

export default function AddEmployeeBatchModal({ open, onClose, batchId, onSaved }) {
    const [employees, setEmployees] = useState([]);

    function handleClose() {
        setEmployees([]);
        onClose();
    }

    const loadEligible = async (inputValue) => {
        try {
            const data = await fetchEligibleEmployees(batchId);
            return data
                .filter((e) =>
                    !inputValue ? true : `${e.nip} ${e.employeeName}`.toLowerCase().includes(inputValue.toLowerCase())
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
        if (!employees || employees.length === 0) return;
        try {
            const ids = employees.map((emp) => emp.value);
            const res = await addEmployeesToBatchBulk(batchId, ids);

            toast.success(`${res.length} peserta berhasil ditambahkan`);
            onSaved?.();
            handleClose();
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
                        value={employees}
                        onChange={setEmployees}
                        isMulti
                        placeholder="Cari pegawai eligible..."
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
