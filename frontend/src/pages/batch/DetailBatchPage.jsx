// src/pages/batches/DetailBatchPage.jsx
import { useEffect, useState, useMemo } from "react";
import { useParams, useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import {
  fetchEmployeeBatches,
  updateEmployeeBatchStatus,
  deleteEmployeeFromBatch,
} from "../../services/employeeBatchService";
import { fetchBatchById } from "../../services/batchService";
import AddEmployeeBatchModal from "../../components/batches/AddEmployeeBatchModal";
import {
  ArrowLeft,
  Plus,
} from "lucide-react";
import Select from "react-select";

export default function DetailBatchPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [batch, setBatch] = useState(null);
  const [participants, setParticipants] = useState([]);
  const [loading, setLoading] = useState(false);
  const [openAdd, setOpenAdd] = useState(false);

  // Filters
  const [filterEmployee, setFilterEmployee] = useState(null);
  const [filterStatus, setFilterStatus] = useState(null);

  const statusOptions = [
    { value: "REGISTERED", label: "Registered" },
    { value: "ATTENDED", label: "Attended" },
    { value: "PASSED", label: "Passed" },
    { value: "FAILED", label: "Failed" },
  ];

  async function loadData() {
    setLoading(true);
    try {
      const [batchData, participantData] = await Promise.all([
        fetchBatchById(id),
        fetchEmployeeBatches(id),
      ]);
      setBatch(batchData);
      setParticipants(participantData);
    } catch {
      toast.error("Gagal memuat data batch");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadData();
  }, [id]);

  async function handleUpdateStatus(pid, status) {
    try {
      await updateEmployeeBatchStatus(pid, status);
      toast.success("Status peserta diperbarui");
      loadData();
    } catch {
      toast.error("Gagal update status");
    }
  }

  async function handleDelete(pid) {
    try {
      await deleteEmployeeFromBatch(pid);
      toast.success("Peserta dihapus");
      loadData();
    } catch {
      toast.error("Gagal hapus peserta");
    }
  }

  // Options untuk filter employee
  const employeeOptions = useMemo(
    () =>
      participants.map((p) => ({
        value: p.id,
        label: `${p.employeeNip} - ${p.employeeName}`,
      })),
    [participants]
  );

  // Apply filter
  const filteredParticipants = useMemo(() => {
    let data = [...participants];

    if (filterEmployee) {
      data = data.filter((p) => p.id === filterEmployee.value);
    }

    if (filterStatus) {
      data = data.filter((p) => p.status === filterStatus.value);
    }

    return data;
  }, [participants, filterEmployee, filterStatus]);

  function resetFilter() {
    setFilterEmployee(null);
    setFilterStatus(null);
    toast.success("Filter direset");
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <button
          className="btn btn-sm btn-accent flex items-center gap-1"
          onClick={() => navigate(-1)}
        >
          <ArrowLeft className="w-4 h-4" />
          Kembali
        </button>
        <h2 className="text-xl font-bold mt-2">Detail Batch</h2>
      </div>

      {/* Batch Info */}
      {batch && (
        <div className="card bg-base-100 shadow border border-gray-200">
          <div className="card-body grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-y-3 text-sm">
            <div>
              <span className="font-bold text-gray-800">Nama Batch:</span>
              <div className="text-gray-700">{batch.batchName}</div>
            </div>
            <div>
              <span className="font-bold text-gray-800">Sertifikasi:</span>
              <div className="text-gray-700">
                {batch.certificationName}
                {batch.certificationLevelName
                  ? ` - ${batch.certificationLevelName}`
                  : ""}
              </div>
            </div>
            <div>
              <span className="font-bold text-gray-800">Lembaga:</span>
              <div className="text-gray-700">{batch.institutionName || "-"}</div>
            </div>
            <div>
              <span className="font-bold text-gray-800">Tanggal Mulai:</span>
              <div className="text-gray-700">
                {batch.startDate
                  ? new Date(batch.startDate).toLocaleDateString("id-ID")
                  : "-"}
              </div>
            </div>
            <div>
              <span className="font-bold text-gray-800">Tanggal Selesai:</span>
              <div className="text-gray-700">
                {batch.endDate
                  ? new Date(batch.endDate).toLocaleDateString("id-ID")
                  : "-"}
              </div>
            </div>
            <div>
              <span className="font-bold text-gray-800">Quota:</span>
              <div className="text-gray-700">{batch.quota ?? "-"}</div>
            </div>
            <div>
              <span className="font-bold text-gray-800">Total Peserta:</span>
              <div className="text-gray-700">{batch.totalParticipants ?? 0}</div>
            </div>
            <div>
              <span className="font-bold text-gray-800">Total Lulus:</span>
              <div className="text-gray-700">{batch.totalPassed ?? 0}</div>
            </div>
            <div>
              <span className="font-bold text-gray-800">Status:</span>
              <div>
                <span
                  className={`badge badge-sm text-white ${
                    batch.status === "PLANNED"
                      ? "badge-info"
                      : batch.status === "ONGOING"
                      ? "badge-warning"
                      : batch.status === "FINISHED"
                      ? "badge-success"
                      : "badge-error"
                  }`}
                >
                  {batch.status}
                </span>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Filters + Tambah Peserta */}
      <div className="mb-4 space-y-3">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-6 gap-3 text-xs">
          <Select
            options={employeeOptions}
            value={filterEmployee}
            onChange={setFilterEmployee}
            placeholder="Filter Nama/NIP"
            isClearable
          />
          <Select
            options={statusOptions}
            value={filterStatus}
            onChange={setFilterStatus}
            placeholder="Filter Status"
            isClearable
          />
          <div className="col-span-1">
            <button
              className="btn btn-accent btn-soft border-accent btn-sm w-full"
              onClick={resetFilter}
            >
              Clear Filter
            </button>
          </div>
          <div className="col-span-2"></div>
          <div className="col-span-1">
            <button
              className="btn btn-success btn-sm w-full flex items-center gap-1"
              onClick={() => setOpenAdd(true)}
            >
              <Plus className="w-4 h-4" />
              Tambah Peserta
            </button>
          </div>
        </div>
      </div>

      {/* Participants Table */}
      <div className="card bg-base-100 shadow border border-gray-200">
        <div className="card-body p-0">
          <table className="table table-zebra text-sm">
            <thead className="bg-base-200">
              <tr>
                <th>No</th>
                <th>NIP</th>
                <th>Nama</th>
                <th>Status</th>
                <th className="text-center">Aksi</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={5} className="text-center py-10">
                    <span className="loading loading-dots loading-md" />
                  </td>
                </tr>
              ) : filteredParticipants.length === 0 ? (
                <tr>
                  <td colSpan={5} className="text-center text-gray-400 py-10">
                    Tidak ada peserta
                  </td>
                </tr>
              ) : (
                filteredParticipants.map((p, idx) => (
                  <tr key={p.id}>
                    <td>{idx + 1}</td>
                    <td>{p.employeeNip}</td>
                    <td>{p.employeeName}</td>
                    <td>
                      <span
                        className={`badge badge-sm text-white ${
                          p.status === "REGISTERED"
                            ? "badge-info"
                            : p.status === "ATTENDED"
                            ? "badge-warning"
                            : p.status === "PASSED"
                            ? "badge-success"
                            : p.status === "FAILED"
                            ? "badge-error"
                            : "badge-neutral"
                        }`}
                      >
                        {p.status}
                      </span>
                    </td>
                    <td className="flex justify-center gap-2">
                      {p.status === "REGISTERED" && (
                        <button
                          className="btn btn-xs btn-warning btn-soft border-warning"
                          onClick={() => handleUpdateStatus(p.id, "ATTENDED")}
                        >
                          Attend
                        </button>
                      )}

                      {p.status === "ATTENDED" && (
                        <>
                          <button
                            className="btn btn-xs btn-success btn-soft border-success"
                            onClick={() => handleUpdateStatus(p.id, "PASSED")}
                          >
                            Passed
                          </button>
                          <button
                            className="btn btn-xs btn-error btn-soft border-error"
                            onClick={() => handleUpdateStatus(p.id, "FAILED")}
                          >
                            Failed
                          </button>
                        </>
                      )}

                      <button
                        className="btn btn-xs btn-neutral btn-soft border-neutral"
                        onClick={() => handleDelete(p.id)}
                      >
                        Hapus
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal Tambah Peserta */}
      <AddEmployeeBatchModal
        open={openAdd}
        batchId={id}
        onClose={() => setOpenAdd(false)}
        onSaved={loadData}
      />
    </div>
  );
}
