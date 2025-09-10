export default function DashboardPegawai() {
  const stats = [
    { label: "Sertifikat Aktif", value: 3 },
    { label: "Sertifikat Due", value: 1 },
    { label: "Sertifikat Expired", value: 2 },
  ];

  const certificates = [
    { name: "AAJI", status: "ACTIVE", expired: "2026-01-01" },
    { name: "OJK", status: "DUE", expired: "2025-09-15" },
    { name: "LSPP", status: "EXPIRED", expired: "2024-12-20" },
  ];

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold">Dashboard Saya</h1>

      {/* Statistik */}
      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        {stats.map((s, i) => (
          <div key={i} className="bg-white shadow rounded-xl p-4 text-center">
            <div className="text-lg font-bold">{s.value}</div>
            <div className="text-sm text-gray-500">{s.label}</div>
          </div>
        ))}
      </div>

      {/* Sertifikat Saya */}
      <div className="bg-white shadow rounded-xl p-4">
        <h2 className="font-semibold mb-2">Sertifikat Saya</h2>
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b">
              <th className="text-left">Nama Sertifikat</th>
              <th className="text-left">Expired</th>
              <th className="text-left">Status</th>
            </tr>
          </thead>
          <tbody>
            {certificates.map((c, i) => (
              <tr key={i} className="border-b">
                <td>{c.name}</td>
                <td className={c.status === "EXPIRED" ? "text-red-500" : c.status === "DUE" ? "text-yellow-600" : ""}>
                  {c.expired}
                </td>
                <td>
                  <span
                    className={`px-2 py-1 rounded text-xs ${
                      c.status === "ACTIVE"
                        ? "bg-green-200 text-green-800"
                        : c.status === "DUE"
                        ? "bg-yellow-200 text-yellow-800"
                        : "bg-red-200 text-red-800"
                    }`}
                  >
                    {c.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}