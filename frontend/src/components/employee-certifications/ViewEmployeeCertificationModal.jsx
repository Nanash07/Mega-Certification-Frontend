export default function ViewEmployeeCertificationModal({ open, certId, onClose }) {
    if (!open) return null;

    const fileUrl = `/api/employee-certifications/${certId}/file`;

    return (
        <dialog className="modal" open={open}>
            <div className="modal-box max-w-4xl h-[80vh] flex flex-col">
                <h3 className="font-bold text-lg mb-3">Lihat Sertifikat</h3>

                <div className="flex-1 overflow-auto flex items-center justify-center bg-base-200 rounded-lg p-2">
                    <img src={fileUrl} alt="Sertifikat" className="max-h-full max-w-full object-contain rounded-lg" />
                </div>

                <div className="modal-action">
                    <a href={`/api/employee-certifications/${certId}/file?download=true`} className="btn btn-success">
                        Download
                    </a>
                    <button className="btn" onClick={onClose}>
                        Tutup
                    </button>
                </div>
            </div>

            <form method="dialog" className="modal-backdrop">
                <button onClick={onClose}>close</button>
            </form>
        </dialog>
    );
}
