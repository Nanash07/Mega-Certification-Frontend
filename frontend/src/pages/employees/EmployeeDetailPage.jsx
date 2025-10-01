import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { ArrowLeft } from "lucide-react";

import { getEmployeeDetail } from "../../services/employeeService";
import { fetchCertifications } from "../../services/employeeCertificationService";
import ViewEmployeeCertificationModal from "../../components/employee-certifications/ViewEmployeeCertificationModal";
import UploadCertificationModal from "../../components/employee-certifications/UploadEmployeeCertificationModal";
import EditCertificationModal from "../../components/employee-certifications/EditEmployeeCertificationModal";

export default function EmployeeDetailPage() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [employee, setEmployee] = useState(null);
    const [certifications, setCertifications] = useState([]);
    const [loading, setLoading] = useState(true);

    const [viewData, setViewData] = useState(null);
    const [uploadData, setUploadData] = useState(null);
    const [editData, setEditData] = useState(null);

    const loadData = async () => {
        try {
            setLoading(true);
            const [emp, certRes] = await Promise.all([
                getEmployeeDetail(id),
                fetchCertifications({ employeeIds: [id], page: 0, size: 100 }),
            ]);
            setEmployee(emp);
            setCertifications(certRes.content || []);
        } catch {
            toast.error("Gagal memuat detail pegawai");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadData();
    }, [id]);

    const formatDate = (date) => {
        if (!date) return "-";
        return new Date(date).toLocaleDateString("id-ID", {
            day: "2-digit",
            month: "short",
            year: "numeric",
        });
    };

    const translateGender = (gender) => {
        if (!gender) return "-";
        return gender === "F" ? "Perempuan" : gender === "M" ? "Laki-laki" : gender;
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center h-64">
                <span className="loading loading-dots loading-lg" />
            </div>
        );
    }

    if (!employee) return null;

    return (
        <div className="space-y-6">
            {/* Tombol Back */}
            <div>
                <button className="btn btn-sm btn-accent mb-2 flex items-center gap-2" onClick={() => navigate(-1)}>
                    <ArrowLeft size={16} />
                    Kembali
                </button>
            </div>

            {/* Informasi Pribadi */}
            <div className="card bg-base-100 shadow p-5">
                <h2 className="font-bold text-xl mb-4">Informasi Pribadi</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 text-sm">
                    <div>
                        <p className="text-gray-500">NIP</p>
                        <p className="font-medium">{employee.nip}</p>
                    </div>
                    <div>
                        <p className="text-gray-500">Nama</p>
                        <p className="font-medium">{employee.name}</p>
                    </div>
                    <div>
                        <p className="text-gray-500">Email</p>
                        <p className="font-medium">{employee.email || "-"}</p>
                    </div>
                    <div>
                        <p className="text-gray-500">Jenis Kelamin</p>
                        <p className="font-medium">{translateGender(employee.gender)}</p>
                    </div>
                    <div>
                        <p className="text-gray-500">Status</p>
                        <span className="badge badge-sm badge-success text-white">{employee.status || "-"}</span>
                    </div>
                </div>
            </div>

            {/* Detail Pekerjaan */}
            <div className="card bg-base-100 shadow p-5">
                <h2 className="font-bold text-xl mb-4">Detail Pekerjaan</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 text-sm">
                    <div>
                        <p className="text-gray-500">Jabatan</p>
                        <p className="font-medium">{employee.jobName || "-"}</p>
                    </div>
                    <div>
                        <p className="text-gray-500">Unit</p>
                        <p className="font-medium">{employee.unitName || "-"}</p>
                    </div>
                    <div>
                        <p className="text-gray-500">Divisi</p>
                        <p className="font-medium">{employee.divisionName || "-"}</p>
                    </div>
                    <div>
                        <p className="text-gray-500">Regional</p>
                        <p className="font-medium">{employee.regionalName || "-"}</p>
                    </div>
                    <div>
                        <p className="text-gray-500">Tanggal SK</p>
                        <p className="font-medium">{formatDate(employee.joinDate)}</p>
                    </div>
                    <div>
                        <p className="text-gray-500">Diupdate</p>
                        <p className="font-medium">{formatDate(employee.updatedAt)}</p>
                    </div>
                </div>
            </div>

            {/* Sertifikasi */}
            <div className="card bg-base-100 shadow p-5">
                <h2 className="font-bold text-xl mb-4">Sertifikasi</h2>

                {certifications.length === 0 ? (
                    <p className="text-gray-400 text-sm">Belum ada sertifikasi</p>
                ) : (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                        {certifications.map((c) => (
                            <div key={c.id} className="card bg-slate-50 shadow">
                                {/* Preview sertifikat */}
                                {c.fileUrl && (
                                    <figure className="h-40 bg-base-300 flex items-center justify-center">
                                        <img
                                            src={`/api/employee-certifications/${c.id}/file`}
                                            alt={c.certificationName}
                                            className="h-full w-full object-cover rounded"
                                        />
                                    </figure>
                                )}
                                <div className="card-body p-4">
                                    <h3 className="font-semibold text-base">{c.certificationName}</h3>
                                    <p className="text-xs text-gray-500">{c.certificationCode}</p>

                                    {/* Tambahin snapshot jabatan */}
                                    <p className="text-xs text-gray-400 italic">
                                        Jabatan saat menerima: {c.jobPositionTitle || "-"}
                                    </p>

                                    <div className="mt-2 text-sm space-y-1">
                                        <p>
                                            <strong>No:</strong> {c.certNumber || "-"}
                                        </p>
                                        <p>
                                            <strong>Tanggal:</strong> {formatDate(c.certDate)}
                                        </p>
                                        <p>
                                            <strong>Exp:</strong> {formatDate(c.validUntil)}
                                        </p>
                                        <p>
                                            <strong>Status:</strong>{" "}
                                            <span
                                                className={`badge badge-sm text-white ${
                                                    c.status === "ACTIVE"
                                                        ? "badge-success"
                                                        : c.status === "EXPIRED"
                                                        ? "badge-error"
                                                        : c.status === "DUE"
                                                        ? "badge-warning"
                                                        : c.status === "PENDING"
                                                        ? "badge-info"
                                                        : "badge-ghost"
                                                }`}
                                            >
                                                {c.status}
                                            </span>
                                        </p>
                                    </div>

                                    <div className="mt-3 flex flex-col gap-2">
                                        {c.fileUrl ? (
                                            <button
                                                className="btn btn-sm btn-primary w-full"
                                                onClick={() => setViewData(c)}
                                            >
                                                Lihat
                                            </button>
                                        ) : (
                                            <button
                                                className="btn btn-sm btn-info w-full"
                                                onClick={() => setUploadData(c)}
                                            >
                                                Upload
                                            </button>
                                        )}

                                        <button
                                            className="btn btn-sm btn-secondary w-full"
                                            onClick={() => setEditData(c)}
                                        >
                                            Edit Detail
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Modal View Sertifikat */}
            {viewData && (
                <ViewEmployeeCertificationModal
                    open={!!viewData}
                    certId={viewData.id}
                    onClose={() => setViewData(null)}
                    onUpdated={loadData}
                />
            )}

            {/* Modal Upload Sertifikat */}
            {uploadData && (
                <UploadCertificationModal
                    open={!!uploadData}
                    certId={uploadData.id}
                    onClose={() => setUploadData(null)}
                    onUploaded={loadData}
                />
            )}

            {/* Modal Edit Sertifikat */}
            {editData && (
                <EditCertificationModal
                    open={!!editData}
                    data={editData}
                    onClose={() => setEditData(null)}
                    onSaved={loadData}
                />
            )}
        </div>
    );
}
