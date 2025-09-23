package com.bankmega.certification.dto;

import com.bankmega.certification.entity.EmployeeCertification;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCertificationResponse {

    private Long id;

    // 🔹 Data Pegawai
    private Long employeeId;
    private String nip;
    private String employeeName;
    private String jobPositionTitle;

    // 🔹 Data Aturan Sertifikasi (CertificationRule)
    private Long certificationRuleId;
    private String certificationName;
    private String certificationCode;
    private String certificationLevelName;
    private Integer certificationLevelLevel;
    private String subFieldCode;
    private String subFieldName;

    // 🔹 Lembaga Penyelenggara
    private Long institutionId;
    private String institutionName;

    // 🔹 Info Sertifikat
    private String certNumber;
    private LocalDate certDate;      // Tanggal sertifikat diterbitkan
    private LocalDate validFrom;     // Berlaku dari
    private LocalDate validUntil;    // Berlaku sampai
    private LocalDate reminderDate;  // Tanggal mulai reminder
    private String fileUrl;

    private EmployeeCertification.Status status;
    private EmployeeCertification.ProcessType processType;

    // 🔹 Audit
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}
