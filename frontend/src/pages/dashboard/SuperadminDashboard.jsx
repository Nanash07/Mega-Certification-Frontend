import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, PieChart, Pie, Cell } from "recharts";

const SuperadminDashboard = () => {
  // Dummy data sementara (ganti nanti dengan API)
  const stats = [
    { label: "Pegawai", value: 3520 },
    { label: "Sertifikat Aktif", value: 1800 },
    { label: "Sertifikat Due", value: 200 },
    { label: "Sertifikat Expired", value: 150 },
    { label: "Batch Aktif", value: 12 },
  ];

  const chartByDivision = [
    { division: "Finance", certified: 120, notCertified: 30 },
    { division: "IT", certified: 200, notCertified: 50 },
    { division: "HR", certified: 80, notCertified: 10 },
  ];

  const chartByJenis = [
    { name: "AAJI", value: 1200 },
    { name: "LSPP", value: 500 },
    { name: "OJK", value: 300 },
  ];

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold mb-4">Dashboard Superadmin</h1>

      {/* Statistik */}
      <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
        {stats.map((s, i) => (
          <div key={i} className="bg-white shadow rounded-xl p-4 text-center">
            <div className="text-lg font-bold">{s.value}</div>
            <div className="text-sm text-gray-500">{s.label}</div>
          </div>
        ))}
      </div>

      {/* Grafik */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Bar Chart */}
        <div className="bg-white shadow rounded-xl p-4">
          <h2 className="font-semibold mb-2">Certified per Division</h2>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={chartByDivision}>
              <XAxis dataKey="division" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="certified" stackId="a" fill="#4CAF50" />
              <Bar dataKey="notCertified" stackId="a" fill="#F44336" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Pie Chart */}
        <div className="bg-white shadow rounded-xl p-4">
          <h2 className="font-semibold mb-2">Sertifikat per Jenis</h2>
          <ResponsiveContainer width="100%" height={250}>
            <PieChart>
              <Pie data={chartByJenis} dataKey="value" outerRadius={100} label>
                {chartByJenis.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={["#4CAF50", "#2196F3", "#FF9800"][index % 3]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};

export default SuperadminDashboard;