package com.bankmega.certification.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_certifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCertification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @ManyToOne
    @JoinColumn(name = "sub_field_id")
    private SubField subField;

    @ManyToOne
    @JoinColumn(name = "certification_level_id")
    private CertificationLevel certificationLevel;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    // 🔹 Relasi baru ke CertificationRule
    @ManyToOne
    @JoinColumn(name = "certification_rule_id", nullable = false)
    private CertificationRule certificationRule;

    @Column(name = "cert_number", length = 100)
    private String certNumber;

    @Column(name = "cert_date")
    private LocalDate certDate;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(name = "file_url", length = 255)
    private String fileUrl;

    @Column(length = 30)
    private String status; // ACTIVE / DUE / EXPIRED / REVOKED

    @Column(name = "process_type", length = 30)
    private String processType; // SERTIFIKASI / REFRESHMENT / TRAINING

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}