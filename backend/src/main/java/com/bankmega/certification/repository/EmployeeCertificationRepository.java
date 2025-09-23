package com.bankmega.certification.repository;

import com.bankmega.certification.entity.EmployeeCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EmployeeCertificationRepository extends
        JpaRepository<EmployeeCertification, Long>,
        JpaSpecificationExecutor<EmployeeCertification> {

    // 🔹 Ambil by ID hanya kalau belum soft delete
    Optional<EmployeeCertification> findByIdAndDeletedAtIsNull(Long id);

    // 🔹 Ambil sertifikat terakhir untuk pegawai + rule (berdasarkan validUntil desc)
    Optional<EmployeeCertification> findTopByEmployeeIdAndCertificationRuleIdAndDeletedAtIsNullOrderByValidUntilDesc(
            Long employeeId, Long certificationRuleId);

    // 🔹 Ambil semua sertifikasi aktif untuk list pegawai
    List<EmployeeCertification> findByEmployeeIdInAndDeletedAtIsNull(List<Long> employeeIds);

    // 🔹 Ambil semua sertifikasi aktif untuk 1 pegawai
    List<EmployeeCertification> findByEmployeeIdAndDeletedAtIsNull(Long employeeId);

    // 🔹 Cek apakah ada sertifikasi aktif (hindari duplikat create)
    Optional<EmployeeCertification> findFirstByEmployeeIdAndCertificationRuleIdAndDeletedAtIsNull(
            Long employeeId, Long certificationRuleId);
}