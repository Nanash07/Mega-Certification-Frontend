package com.bankmega.certification.repository;

import com.bankmega.certification.entity.Employee;
import com.bankmega.certification.entity.EmployeeCertification;
import com.bankmega.certification.entity.CertificationRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeCertificationRepository extends JpaRepository<EmployeeCertification, Long> {

    // Ambil sertifikat terakhir (paling baru) untuk 1 pegawai & rule tertentu
    Optional<EmployeeCertification> findTopByEmployeeAndCertificationRuleAndDeletedAtIsNullOrderByCertDateDesc(
            Employee employee,
            CertificationRule rule
    );
}
