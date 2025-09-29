import { useState } from "react";
import toast from "react-hot-toast";
import { uploadCertificationFile } from "../../services/employeeCertificationService";

export default function UploadCertificationModal({ open, onClose, certId, onUploaded }) {
    const [file, setFile] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleUpload = async () => {
        if (!file) {
            toast.error("Pilih file terlebih dahulu");
            return;
        }

        try {
            setLoading(true);
            await uploadCertificationFile(certId, file);
            toast.success("Upload sertifikat berhasil");    
            onUploaded();
            onClose();
        } catch {
            toast.error("Gagal upload sertifikat");
        } finally {
            setLoading(false);
            setFile(null);
        }
    };

    if (!open) return null;

    return (
        <dialog className="modal" open={open}>
            <div className="modal-box">
                <h3 className="font-bold text-lg mb-3">Upload Sertifikat</h3>

                <input
                    type="file"
                    accept=".jpg,.jpeg,.png"
                    onChange={(e) => setFile(e.target.files[0])}
                    className="file-input file-input-bordered w-full"
                />

                <div className="modal-action">
                    <button className="btn" onClick={onClose}>
                        Batal
                    </button>
                    <button className="btn btn-primary" onClick={handleUpload} disabled={loading}>
                        {loading ? "Mengupload..." : "Upload"}
                    </button>
                </div>
            </div>
            <form method="dialog" className="modal-backdrop">
                <button onClick={onClose}>close</button>
            </form>
        </dialog>
    );
}
