package com.bankmega.certification.repository;

import com.bankmega.certification.entity.Employee;
import com.bankmega.certification.entity.EmployeeCertification;
import com.bankmega.certification.entity.CertificationRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeCertificationRepository extends JpaRepository<EmployeeCertification, Long> {

    // ==== Ambil sertifikat terakhir (untuk 1 pegawai + 1 rule) ====
    Optional<EmployeeCertification> findTopByEmployeeAndCertificationRuleAndDeletedAtIsNullOrderByCertDateDesc(
            Employee employee,
            CertificationRule rule
    );

    // ==== Ambil semua sertifikasi aktif (not deleted) untuk list pegawai ====
    List<EmployeeCertification> findByEmployeeIdInAndDeletedAtIsNull(List<Long> employeeIds);

    // ==== Ambil semua sertifikasi aktif (not deleted) untuk 1 pegawai ====
    List<EmployeeCertification> findByEmployeeIdAndDeletedAtIsNull(Long employeeId);
}
