import { useRef, useState } from "react";
import toast from "react-hot-toast";
import { reuploadCertificationFile, deleteCertificationFile } from "../../services/employeeCertificationService";

export default function ViewEmployeeCertificationModal({ open, certId, onClose, onUpdated }) {
    const fileInputRef = useRef(null);
    const [confirmDelete, setConfirmDelete] = useState(false);

    if (!open) return null;

    const fileUrl = `/api/employee-certifications/${certId}/file`;

    const handleReupload = async (e) => {
        const file = e.target.files[0];
        if (!file) return;
        try {
            await reuploadCertificationFile(certId, file);
            toast.success("Sertifikat berhasil diunggah ulang");
            onClose();
            onUpdated?.(); // Refresh data
        } catch {
            toast.error("Gagal mengunggah ulang sertifikat");
        }
    };

    const handleDelete = async () => {
        try {
            await deleteCertificationFile(certId);
            toast.success("Sertifikat berhasil dihapus");
            setConfirmDelete(false);
            onClose();
            onUpdated?.(); // Refresh data
        } catch {
            toast.error("Gagal menghapus sertifikat");
        }
    };

    return (
        <>
            {/* Modal Utama: Lihat Sertifikat */}
            <dialog className="modal" open={open}>
                <div className="modal-box max-w-4xl h-[80vh] flex flex-col">
                    <h3 className="font-bold text-lg mb-3">Lihat Sertifikat</h3>

                    <div className="flex-1 overflow-auto flex items-center justify-center bg-base-200 rounded-lg p-2">
                        <img
                            src={fileUrl}
                            alt="Sertifikat"
                            className="max-h-full max-w-full object-contain rounded-lg"
                        />
                    </div>

                    <div className="modal-action flex justify-between w-full">
                        <div className="flex gap-2">
                            <button className="btn btn-warning" onClick={() => fileInputRef.current.click()}>
                                Unggah Ulang
                            </button>
                            <input
                                ref={fileInputRef}
                                type="file"
                                accept="image/*"
                                className="hidden"
                                onChange={handleReupload}
                            />

                            <button className="btn btn-error" onClick={() => setConfirmDelete(true)}>
                                Hapus
                            </button>
                        </div>

                        <div className="flex gap-2">
                            <a
                                href={`/api/employee-certifications/${certId}/file?download=true`}
                                className="btn btn-success"
                            >
                                Unduh
                            </a>
                            <button className="btn" onClick={onClose}>
                                Tutup
                            </button>
                        </div>
                    </div>
                </div>

                <form method="dialog" className="modal-backdrop">
                    <button onClick={onClose}>close</button>
                </form>
            </dialog>

            {/* Modal Konfirmasi Hapus */}
            <dialog className="modal" open={confirmDelete}>
                <div className="modal-box">
                    <h3 className="font-bold text-lg text-error">Konfirmasi Hapus</h3>
                    <p className="mt-2 text-sm">
                        Apakah Anda yakin ingin menghapus sertifikat ini? <br />
                        Tindakan ini tidak dapat dibatalkan.
                    </p>

                    <div className="modal-action">
                        <button className="btn" onClick={() => setConfirmDelete(false)}>
                            Batal
                        </button>
                        <button className="btn btn-error" onClick={handleDelete}>
                            Hapus
                        </button>
                    </div>
                </div>

                <form method="dialog" className="modal-backdrop">
                    <button onClick={() => setConfirmDelete(false)}>close</button>
                </form>
            </dialog>
        </>
    );
}
