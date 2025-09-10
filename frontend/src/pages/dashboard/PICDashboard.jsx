import { ResponsiveContainer, PieChart, Pie, Cell } from "recharts";

export default function DashboardPIC() {
  const stats = [
    { label: "Pegawai dalam Scope", value: 520 },
    { label: "Certified", value: 400 },
    { label: "Due", value: 50 },
    { label: "Expired", value: 20 },
  ];

  const chartByJenis = [
    { name: "AAJI", value: 300 },
    { name: "LSPP", value: 150 },
    { name: "OJK", value: 70 },
  ];

  const reminderList = [
    { nip: "0001", name: "Budi", cert: "AAJI", expired: "2025-09-10" },
    { nip: "0002", name: "Sari", cert: "OJK", expired: "2025-09-15" },
  ];

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold">Dashboard PIC</h1>

      {/* Statistik */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {stats.map((s, i) => (
          <div key={i} className="bg-white shadow rounded-xl p-4 text-center">
            <div className="text-lg font-bold">{s.value}</div>
            <div className="text-sm text-gray-500">{s.label}</div>
          </div>
        ))}
      </div>

      {/* Chart */}
      <div className="bg-white shadow rounded-xl p-4">
        <h2 className="font-semibold mb-2">Sertifikat per Jenis (Scope Saya)</h2>
        <ResponsiveContainer width="100%" height={250}>
          <PieChart>
            <Pie data={chartByJenis} dataKey="value" outerRadius={100} label>
              {chartByJenis.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={["#4CAF50", "#2196F3", "#FF9800"][index % 3]} />
              ))}
            </Pie>
          </PieChart>
        </ResponsiveContainer>
      </div>

      {/* Reminder */}
      <div className="bg-white shadow rounded-xl p-4">
        <h2 className="font-semibold mb-2">Sertifikat Mau Expired</h2>
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b">
              <th className="text-left">NIP</th>
              <th className="text-left">Nama</th>
              <th className="text-left">Sertifikat</th>
              <th className="text-left">Expired</th>
            </tr>
          </thead>
          <tbody>
            {reminderList.map((r, i) => (
              <tr key={i} className="border-b">
                <td>{r.nip}</td>
                <td>{r.name}</td>
                <td>{r.cert}</td>
                <td className="text-red-500">{r.expired}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}