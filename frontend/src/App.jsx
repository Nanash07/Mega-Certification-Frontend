import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Login from "./pages/auth/Login";
import ForgotPassword from "./pages/auth/ForgotPassword";

import ProtectedRoute from "./routes/ProtectedRoute";
import MainLayout from "./layouts/MainLayout";

import Dashboard from "./pages/dashboard/Dashboard";
import EmployeeDataPage from "./pages/employees/EmployeeDataPage"; 
import EmployeeEligibilityPage from "./pages/employees/EmployeeEligibilityPage";
import EmployeeEligibilityManualPage from "./pages/employees/EmployeeEligibilityManualPage";

import RegionalPage from "./pages/organizations/RegionalPage";
import DivisionPage from "./pages/organizations/DivisionPage";
import UnitPage from "./pages/organizations/UnitPage";
import JobPositionPage from "./pages/organizations/JobPositionPage";
import ProfilePage from "./pages/profile/ProfilePage";
import JobCertificationMappingPage from "./pages/mappings/JobCertificationMappingPage";
//import EmployeeCertificationExceptionPage from "./pages/mapping/EmployeeCertificationExceptionPage"; // ⬅️ import baru

import CertificationPage from "./pages/certifications/CertificationPage";
import CertificationLevelPage from "./pages/certifications/CertificationLevelPage";
import SubFieldPage from "./pages/certifications/SubFieldPage";
import CertificationRulePage from "./pages/certifications/CertificationRulePage";
import InstitutionPage from "./pages/certifications/InstitutionPage"; 
import UserPage from "./pages/users/UserPage";

import PicCertificationScopePage from "./pages/pic/PicCertificationScopePage";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Redirect root ke dashboard */}
        <Route path="/" element={<Navigate to="/dashboard" replace />} />

        {/* ========== AUTH ========== */}
        <Route path="/login" element={<Login />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />

        {/* ========== PROTECTED ========== */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <MainLayout>
                <Dashboard />
              </MainLayout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <MainLayout>
                <ProfilePage />
              </MainLayout>
            </ProtectedRoute>
          }
        />

        {/* Employee */}
        <Route
          path="/employee/data"
          element={
            <ProtectedRoute>
              <MainLayout>
                <EmployeeDataPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/employee/eligibility"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <EmployeeEligibilityPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/employee/exception"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <EmployeeEligibilityManualPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />

        {/*
        <Route
          path="/employee/exception"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <EmployeeCertificationExceptionPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />
        */}

        {/* Organization */}
        <Route
          path="/organization/regional"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <RegionalPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/organization/division"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <DivisionPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/organization/unit"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <UnitPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/organization/job-position"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <JobPositionPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/mapping/job-certification"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <JobCertificationMappingPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/mapping/pic-certification-scope"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <PicCertificationScopePage />
              </MainLayout>
            </ProtectedRoute>
          }
        />

        {/* Sertifikasi */}
        <Route
          path="/sertifikasi/jenis"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <CertificationPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/sertifikasi/jenjang"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <CertificationLevelPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/sertifikasi/sub-bidang"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <SubFieldPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/sertifikasi/aturan-sertifikat"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <CertificationRulePage />
              </MainLayout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/sertifikasi/lembaga"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <InstitutionPage /> {/* ⬅️ page baru */}
              </MainLayout>
            </ProtectedRoute>
          }
        />

        {/* Manajemen User */}
        <Route
          path="/user"
          element={
            <ProtectedRoute roles={["SUPERADMIN"]}>
              <MainLayout>
                <UserPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}